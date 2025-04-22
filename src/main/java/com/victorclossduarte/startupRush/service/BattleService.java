package com.victorclossduarte.startupRush.service;

import com.victorclossduarte.startupRush.enums.BattleStatus;
import com.victorclossduarte.startupRush.model.BattleModel;
import com.victorclossduarte.startupRush.model.RoundModel;
import com.victorclossduarte.startupRush.model.StartupBattleModel;
import com.victorclossduarte.startupRush.model.StartupModel;
import com.victorclossduarte.startupRush.repository.BattleRepository;
import com.victorclossduarte.startupRush.repository.StartupBattleRepository;
import com.victorclossduarte.startupRush.repository.StartupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class BattleService {
    @Autowired
    private BattleRepository battleRepository;

    @Autowired
    private StartupRepository startupRepository;

    @Autowired
    private StartupBattleRepository startupBattleRepository;

    public List<BattleModel> getAllBattles() {
        return (List<BattleModel>) battleRepository.findAll();
    }

    public BattleModel getBattleById(int id) {
        return battleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Batalha não encontrada"));
    }

    public List<BattleModel> getBattlesByRound(RoundModel round) {
        return battleRepository.findByRound(round);
    }

    public StartupBattleModel getParticipation(int battleId, int startupId) {
        BattleModel battle = getBattleById(battleId);
        StartupModel startup = startupRepository.findById(startupId)
                .orElseThrow(() -> new RuntimeException("Startup não encontrada"));

        Optional<StartupBattleModel> participation = startupBattleRepository.findByStartupAndBattle(startup, battle);
        if (!participation.isPresent()) {
            throw new RuntimeException("Startup não está participando desta batalha");
        }

        return participation.get();
    }

    // Métodos para aplicar eventos nas batalhas

    @Transactional
    public BattleModel applyPitchConvincente(int battleId, int startupId) {
        BattleModel battle = getBattleById(battleId);

        if (battle.getStatus() == BattleStatus.FINALIZADA) {
            throw new RuntimeException("Não é possível aplicar eventos em uma batalha finalizada");
        }

        StartupBattleModel participation = getParticipation(battleId, startupId);
        if (participation == null) {
            throw new RuntimeException("Participação não encontrada");
        }
        if (participation.getPitchConvincente()) {
            throw new RuntimeException("Pitch convincente já foi aplicado para esta startup");
        }

        StartupModel startup = participation.getStartup();
        startup.setPoints(startup.getPoints() + 6);
        startup.setGoodPitch(startup.getGoodPitch() + 1);

        participation.setPitchConvincente(true);
        participation.setPontuacaoFinal(startup.getPoints());

        startupRepository.save(startup);
        startupBattleRepository.save(participation);

        return battleRepository.save(battle);
    }

    @Transactional
    public BattleModel applyProdutoComBugs(int battleId, int startupId) {
        BattleModel battle = getBattleById(battleId);

        if (battle.getStatus() == BattleStatus.FINALIZADA) {
            throw new RuntimeException("Não é possível aplicar eventos em uma batalha finalizada");
        }

        StartupBattleModel participation = getParticipation(battleId, startupId);

        if (participation.getProdutoComBugs()) {
            throw new RuntimeException("Produto com bugs já foi aplicado para esta startup");
        }

        StartupModel startup = participation.getStartup();
        startup.setPoints(startup.getPoints() - 4);
        startup.setBugProd(startup.getBugProd() + 1);

        participation.setProdutoComBugs(true);
        participation.setPontuacaoFinal(startup.getPoints());

        startupRepository.save(startup);
        startupBattleRepository.save(participation);

        return battleRepository.save(battle);
    }

    @Transactional
    public BattleModel applyBoaTracaoUsuarios(int battleId, int startupId) {
        BattleModel battle = getBattleById(battleId);

        if (battle.getStatus() == BattleStatus.FINALIZADA) {
            throw new RuntimeException("Não é possível aplicar eventos em uma batalha finalizada");
        }

        StartupBattleModel participation = getParticipation(battleId, startupId);

        if (participation.getBoaTracaoUsuarios()) {
            throw new RuntimeException("Boa tração de usuários já foi aplicada para esta startup");
        }

        StartupModel startup = participation.getStartup();
        startup.setPoints(startup.getPoints() + 3);
        startup.setUserTract(startup.getUserTract() + 1);

        participation.setBoaTracaoUsuarios(true);
        participation.setPontuacaoFinal(startup.getPoints());

        startupRepository.save(startup);
        startupBattleRepository.save(participation);

        return battleRepository.save(battle);
    }

    @Transactional
    public BattleModel applyInvestidorIrritado(int battleId, int startupId) {
        BattleModel battle = getBattleById(battleId);

        if (battle.getStatus() == BattleStatus.FINALIZADA) {
            throw new RuntimeException("Não é possível aplicar eventos em uma batalha finalizada");
        }

        StartupBattleModel participation = getParticipation(battleId, startupId);

        if (participation.getInvestidorIrritado()) {
            throw new RuntimeException("Investidor irritado já foi aplicado para esta startup");
        }

        StartupModel startup = participation.getStartup();
        startup.setPoints(startup.getPoints() - 6);
        startup.setIrritInv(startup.getIrritInv() + 1);

        participation.setInvestidorIrritado(true);
        participation.setPontuacaoFinal(startup.getPoints());

        startupRepository.save(startup);
        startupBattleRepository.save(participation);

        return battleRepository.save(battle);
    }

    @Transactional
    public BattleModel applyFakeNewsNoPitch(int battleId, int startupId) {
        BattleModel battle = getBattleById(battleId);

        if (battle.getStatus() == BattleStatus.FINALIZADA) {
            throw new RuntimeException("Não é possível aplicar eventos em uma batalha finalizada");
        }

        StartupBattleModel participation = getParticipation(battleId, startupId);

        if (participation.getFakeNewsNoPitch()) {
            throw new RuntimeException("Fake news no pitch já foi aplicado para esta startup");
        }

        StartupModel startup = participation.getStartup();
        startup.setPoints(startup.getPoints() - 8);
        startup.setFakeNewsP(startup.getFakeNewsP() + 1);

        participation.setFakeNewsNoPitch(true);
        participation.setPontuacaoFinal(startup.getPoints());

        startupRepository.save(startup);
        startupBattleRepository.save(participation);

        return battleRepository.save(battle);
    }

    @Transactional
    public BattleModel finalizeBattle(int battleId) {
        // Recuperando a batalha pelo ID Long, não int
        Optional<BattleModel> battleOpt = battleRepository.findById(battleId);
        if (!battleOpt.isPresent()) {
            throw new RuntimeException("Batalha não encontrada");
        }

        BattleModel battle = battleOpt.get();

        if (battle.getStatus() == BattleStatus.FINALIZADA) {
            throw new RuntimeException("Esta batalha já foi finalizada");
        }

        if (battle.getParticipants().size() != 2) {
            throw new RuntimeException("Uma batalha deve ter exatamente 2 startups");
        }

        // Atualizando as pontuações finais nas participações
        for (StartupBattleModel participant : battle.getParticipants()) {
            participant.setPontuacaoFinal(participant.getStartup().getPoints());
            startupBattleRepository.save(participant);
        }

        StartupModel startup1 = battle.getParticipants().get(0).getStartup();
        StartupModel startup2 = battle.getParticipants().get(1).getStartup();

        // Lógica para determinar o vencedor
        StartupModel winner;
        if (startup1.getPoints() > startup2.getPoints()) {
            winner = startup1;
            battle.setSharkFight(false);
        } else if (startup2.getPoints() > startup1.getPoints()) {
            winner = startup2;
            battle.setSharkFight(false);
        } else {
            // Empate - SharkFight
            battle.setSharkFight(true);
            winner = executeSharkFight(battle, startup1, startup2);
        }

        // Atribuindo pontos ao vencedor
        winner.setPoints(winner.getPoints() + 30);
        winner.setWins(winner.getWins() + 1);
        startupRepository.save(winner);

        // Atualizando a batalha
        battle.setWinner(winner);
        battle.setStatus(BattleStatus.FINALIZADA);

        // Salvando todas as entidades
        startupRepository.save(startup1);
        startupRepository.save(startup2);

        // Salvando a batalha após ter atribuído todos os valores
        return battleRepository.save(battle);
    }

    private StartupModel executeSharkFight(BattleModel battle, StartupModel startup1, StartupModel startup2) {
        Random random = new Random();
        boolean isStartup1Winner = random.nextBoolean();

        StartupModel winner = isStartup1Winner ? startup1 : startup2;

        // Encontra a participação do vencedor
        StartupBattleModel winnerParticipation = null;
        for (StartupBattleModel participation : battle.getParticipants()) {
            if (participation.getStartup().getId() == winner.getId()) {
                winnerParticipation = participation;
                break;
            }
        }

        if (winnerParticipation == null) {
            throw new RuntimeException("Erro ao encontrar participação da startup vencedora");
        }

        // Adiciona 2 pontos ao vencedor
        winner.setPoints(winner.getPoints() + 2);
        winner.setSharkFights(winner.getSharkFights() + 1);

        winnerParticipation.setGanhouSharkFight(true);
        winnerParticipation.setPontuacaoFinal(winner.getPoints());

        startupBattleRepository.save(winnerParticipation);
        startupRepository.save(winner);

        return winner;
    }
}