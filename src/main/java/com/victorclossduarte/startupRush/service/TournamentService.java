package com.victorclossduarte.startupRush.service;

import com.victorclossduarte.startupRush.enums.BattleStatus;
import com.victorclossduarte.startupRush.enums.RoundStatus;
import com.victorclossduarte.startupRush.enums.TournamentStatus;
import com.victorclossduarte.startupRush.model.*;
import com.victorclossduarte.startupRush.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class TournamentService {
    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private StartupRepository startupRepository;

    @Autowired
    private RoundRepository roundRepository;

    @Autowired
    private BattleRepository battleRepository;

    @Autowired
    private StartupBattleRepository startupBattleRepository;

    public List<TournamentModel> getAllTournaments() {
        return (List<TournamentModel>) tournamentRepository.findAll();
    }

    public TournamentModel getTournamentById(int id) {
        return tournamentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Torneio não encontrado"));
    }

    public TournamentModel createTournament() {
        TournamentModel tournament = new TournamentModel();
        tournament.setStatus(TournamentStatus.NAO_INICIADO);
        tournament.setCurrentRound(0);
        return tournamentRepository.save(tournament);
    }

    @Transactional
    public TournamentModel addStartupToTournament(int tournamentId, int startupId) {
        TournamentModel tournament = getTournamentById(tournamentId);

        if (tournament.getStatus() != TournamentStatus.NAO_INICIADO) {
            throw new RuntimeException("Não é possível adicionar startups em um torneio já iniciado");
        }

        StartupModel startup = startupRepository.findById(startupId)
                .orElseThrow(() -> new RuntimeException("Startup não encontrada"));

        if (tournament.getStartups().contains(startup)) {
            throw new RuntimeException("Startup já está registrada neste torneio");
        }

        tournament.getStartups().add(startup);
        return tournamentRepository.save(tournament);
    }

    @Transactional
    public TournamentModel removeStartupFromTournament(int tournamentId, int startupId) {
        TournamentModel tournament = getTournamentById(tournamentId);

        if (tournament.getStatus() != TournamentStatus.NAO_INICIADO) {
            throw new RuntimeException("Não é possível remover startups de um torneio já iniciado");
        }

        StartupModel startup = startupRepository.findById(startupId)
                .orElseThrow(() -> new RuntimeException("Startup não encontrada"));

        tournament.getStartups().remove(startup);
        return tournamentRepository.save(tournament);
    }

    @Transactional
    public TournamentModel startTournament(int tournamentId) {
        TournamentModel tournament = getTournamentById(tournamentId);

        if (tournament.getStatus() != TournamentStatus.NAO_INICIADO) {
            throw new RuntimeException("Torneio já foi iniciado");
        }

        int startupCount = tournament.getStartups().size();
        if (startupCount < 4) {
            throw new RuntimeException("O torneio precisa de pelo menos 4 startups para iniciar");
        }

        if ((startupCount & (startupCount - 1)) != 0) {
            throw new RuntimeException("O número de startups deve ser uma potência de 2 (4, 6, 8)");
        }
        tournament.setStatus(TournamentStatus.EM_ANDAMENTO);
        tournament.setCurrentRound(1);
        tournament = tournamentRepository.save(tournament);

        createNewRound(tournament);

        return tournament;
    }

    @Transactional
    public RoundModel createNewRound(TournamentModel tournament) {
        int roundNumber = tournament.getCurrentRound();

        RoundModel round = new RoundModel();
        round.setNumero(roundNumber);
        round.setStatus(RoundStatus.EM_ANDAMENTO);
        round.setTournament(tournament);
        round = roundRepository.save(round);

        if (roundNumber == 1) {
            List<StartupModel> startups = new ArrayList<>(tournament.getStartups());
            Collections.shuffle(startups);
            createBattlesForRound(round, startups);
        } else {
            RoundModel previousRound = roundRepository.findByTournamentAndNumero(tournament, roundNumber - 1)
                    .get(0);

            List<StartupModel> winners = new ArrayList<>();
            for (BattleModel battle : previousRound.getBattles()) {
                if (battle.getWinner() == null) {
                    throw new RuntimeException("Ainda existem batalhas pendentes na rodada anterior");
                }
                winners.add(battle.getWinner());
            }

            createBattlesForRound(round, winners);
        }

        return round;
    }

    private void createBattlesForRound(RoundModel round, List<StartupModel> startups) {
        int battlesCount = startups.size() / 2;

        for (int i = 0; i < battlesCount; i++) {
            BattleModel battle = new BattleModel();
            battle.setRound(round);
            battle.setStatus(BattleStatus.PENDENTE);
            battle = battleRepository.save(battle);

            addStartupToBattle(battle, startups.get(i * 2));
            addStartupToBattle(battle, startups.get(i * 2 + 1));

            round.getBattles().add(battle);
        }

        roundRepository.save(round);
    }

    private void addStartupToBattle(BattleModel battle, StartupModel startup) {
        StartupBattleModel participation = new StartupBattleModel();
        participation.setBattle(battle);
        participation.setStartup(startup);
        participation.setPontuacaoInicial(startup.getPoints());
        participation.setPontuacaoFinal(startup.getPoints());

        startupBattleRepository.save(participation);
        battle.getParticipants().add(participation);
    }

    @Transactional
    public TournamentModel advanceToNextRound(int tournamentId) {
        TournamentModel tournament = getTournamentById(tournamentId);

        if (tournament.getStatus() != TournamentStatus.EM_ANDAMENTO) {
            throw new RuntimeException("Torneio não está em andamento");
        }

        int currentRoundNumber = tournament.getCurrentRound();
        List<RoundModel> currentRoundList = roundRepository.findByTournamentAndNumero(tournament, currentRoundNumber);

        if (currentRoundList.isEmpty()) {
            throw new RuntimeException("Rodada atual não encontrada");
        }

        RoundModel currentRound = currentRoundList.get(0);

        for (BattleModel battle : currentRound.getBattles()) {
            if (battle.getStatus() == BattleStatus.PENDENTE) {
                throw new RuntimeException("Ainda existem batalhas pendentes na rodada atual");
            }

            if (battle.getWinner() == null) {
                throw new RuntimeException("Batalha #" + battle.getId() + " não tem um vencedor definido");
            }
        }

        currentRound.setStatus(RoundStatus.FINALIZADA);
        roundRepository.save(currentRound);


        if (currentRound.getBattles().size() == 1) {
            tournament.setStatus(TournamentStatus.FINALIZADO);

            BattleModel finalBattle = currentRound.getBattles().get(0);
            if (finalBattle.getWinner() != null) {
                StartupModel champion = finalBattle.getWinner();
                tournament.setChampion(champion);

                startupRepository.save(champion);
                tournament = tournamentRepository.save(tournament);

                System.out.println("Torneio finalizado. Campeão: " + champion.getName() + " (ID: " + champion.getId() + ")");
            } else {
                throw new RuntimeException("A batalha final não tem um vencedor definido");
            }

            return tournament;
        }


        tournament.setCurrentRound(currentRoundNumber + 1);
        tournament = tournamentRepository.save(tournament);

        createNewRound(tournament);

        return tournament;
    }

    public List<RoundModel> getTournamentRounds(int tournamentId) {
        TournamentModel tournament = getTournamentById(tournamentId);
        return roundRepository.findByTournament(tournament);
    }

    public RoundModel getCurrentRound(int tournamentId) {
        TournamentModel tournament = getTournamentById(tournamentId);
        int currentRoundNumber = tournament.getCurrentRound();
        List<RoundModel> rounds = roundRepository.findByTournamentAndNumero(tournament, currentRoundNumber);
        if (rounds.isEmpty()) {
            throw new RuntimeException("Rodada atual não encontrada");
        }
        return rounds.get(0);
    }
}