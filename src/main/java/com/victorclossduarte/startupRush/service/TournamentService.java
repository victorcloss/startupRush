package com.victorclossduarte.startupRush.service;

import com.victorclossduarte.startupRush.enums.BattleStatus;
import com.victorclossduarte.startupRush.enums.RoundStatus;
import com.victorclossduarte.startupRush.enums.TournamentStatus;
import com.victorclossduarte.startupRush.model.*;
import com.victorclossduarte.startupRush.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public TournamentModel startTournament(int tournamentId) {
        TournamentModel tournament = getTournamentById(tournamentId);

        if (tournament.getStatus() != TournamentStatus.NAO_INICIADO) {
            throw new RuntimeException("Torneio já foi iniciado");
        }

        int startupCount = tournament.getStartups().size();
        if (startupCount < 4) {
            throw new RuntimeException("O torneio precisa de pelo menos 4 startups para iniciar");
        }

        // Verifica se o número de startups é uma potência de 2 (4, 8, 16...)
        if ((startupCount & (startupCount - 1)) != 0) {
            throw new RuntimeException("O número de startups deve ser uma potência de 2 (4, 8, 16...)");
        }

        // Inicia o torneio
        tournament.setStatus(TournamentStatus.EM_ANDAMENTO);
        tournament.setCurrentRound(1);
        tournament = tournamentRepository.save(tournament);

        // Cria a primeira rodada
        createNewRound(tournament);

        return tournament;
    }

    public RoundModel createNewRound(TournamentModel tournament) {
        int roundNumber = tournament.getCurrentRound();

        RoundModel round = new RoundModel();
        round.setNumero(roundNumber);
        round.setStatus(RoundStatus.EM_ANDAMENTO);
        round.setTournament(tournament);
        round = roundRepository.save(round);

        // Se for a primeira rodada, usa todas as startups do torneio
        if (roundNumber == 1) {
            List<StartupModel> startups = new ArrayList<>(tournament.getStartups());
            Collections.shuffle(startups); // Embaralha para criar confrontos aleatórios
            createBattlesForRound(round, startups);
        } else {
            // Para as próximas rodadas, usa os vencedores da rodada anterior
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

            // Adiciona as duas startups à batalha
            addStartupToBattle(battle, startups.get(i * 2));
            addStartupToBattle(battle, startups.get(i * 2 + 1));

            // Adiciona a batalha à lista de batalhas da rodada
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

    public TournamentModel advanceToNextRound(int tournamentId) {
        TournamentModel tournament = getTournamentById(tournamentId);

        if (tournament.getStatus() != TournamentStatus.EM_ANDAMENTO) {
            throw new RuntimeException("Torneio não está em andamento");
        }

        int currentRoundNumber = tournament.getCurrentRound();
        RoundModel currentRound = roundRepository.findByTournamentAndNumero(tournament, currentRoundNumber)
                .get(0);

        // Verifica se todas as batalhas da rodada atual foram finalizadas
        for (BattleModel battle : currentRound.getBattles()) {
            if (battle.getStatus() == BattleStatus.PENDENTE) {
                throw new RuntimeException("Ainda existem batalhas pendentes na rodada atual");
            }
        }

        // Marca a rodada atual como finalizada
        currentRound.setStatus(RoundStatus.FINALIZADA);
        roundRepository.save(currentRound);

        // Verifica se é a última rodada (apenas uma batalha)
        if (currentRound.getBattles().size() == 1) {
            // Finaliza o torneio
            tournament.setStatus(TournamentStatus.FINALIZADO);
            tournament.setChampion(currentRound.getBattles().get(0).getWinner());
            return tournamentRepository.save(tournament);
        }

        // Avança para a próxima rodada
        tournament.setCurrentRound(currentRoundNumber + 1);
        tournament = tournamentRepository.save(tournament);

        // Cria a nova rodada
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