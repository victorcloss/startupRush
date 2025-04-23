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

import java.util.ArrayList;
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
        try {
            // Log para depuração
            System.out.println("Iniciando finalização da batalha ID: " + battleId);

            // Recuperando a batalha pelo ID
            BattleModel battle = battleRepository.findById(battleId)
                    .orElseThrow(() -> new RuntimeException("Batalha não encontrada"));

            System.out.println("Batalha recuperada. Status atual: " + battle.getStatus());

            if (battle.getStatus() == BattleStatus.FINALIZADA) {
                throw new RuntimeException("Esta batalha já foi finalizada");
            }

            if (battle.getParticipants().size() != 2) {
                throw new RuntimeException("Uma batalha deve ter exatamente 2 startups");
            }

            // Recuperando explicitamente as startups participantes do banco de dados
            // para garantir que estamos trabalhando com entidades gerenciadas
            List<StartupBattleModel> participants = battle.getParticipants();
            StartupModel startup1 = startupRepository.findById(participants.get(0).getStartup().getId()).get();
            StartupModel startup2 = startupRepository.findById(participants.get(1).getStartup().getId()).get();

            System.out.println("Participantes: " + startup1.getName() + " vs " + startup2.getName());
            System.out.println("Pontuações: " + startup1.getPoints() + " vs " + startup2.getPoints());

            // Definir o vencedor primeiro (IMPORTANTE: esta é a mudança principal)
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

            System.out.println("Vencedor determinado: " + winner.getName());

            // Definir explicitamente o vencedor na batalha ANTES de salvar
            battle.setWinner(winner);
            battle.setStatus(BattleStatus.FINALIZADA);

            // Salvar a batalha PRIMEIRO
            BattleModel savedBattle = battleRepository.saveAndFlush(battle);
            System.out.println("Batalha salva. ID: " + savedBattle.getId() + ", Status: " + savedBattle.getStatus());

            // Depois de salvar a batalha, atualizar as pontuações finais e os participantes
            for (StartupBattleModel participant : participants) {
                participant.setPontuacaoFinal(participant.getStartup().getPoints());
                startupBattleRepository.save(participant);
            }

            // Adicionar pontos ao vencedor DEPOIS de salvar a batalha
            winner.setPoints(winner.getPoints() + 30);
            winner.setWins(winner.getWins() + 1);
            startupRepository.save(winner);
            System.out.println("Pontos do vencedor atualizados: " + winner.getPoints());

            // Verificação final
            BattleModel verifiedBattle = battleRepository.findById(battleId).get();
            System.out.println("Verificação final da batalha - Status: " + verifiedBattle.getStatus());
            System.out.println("Verificação final da batalha - Vencedor: " +
                    (verifiedBattle.getWinner() != null ? verifiedBattle.getWinner().getName() : "null"));

            return verifiedBattle;
        } catch (Exception e) {
            System.out.println("ERRO ao finalizar batalha: " + e.getMessage());
            e.printStackTrace();
            throw e; // Relançar a exceção para que o controller possa tratá-la
        }
    }

    private StartupModel executeSharkFight(BattleModel battle, StartupModel startup1, StartupModel startup2) {
        try {
            System.out.println("Executando SharkFight entre " + startup1.getName() + " e " + startup2.getName());

            Random random = new Random();
            boolean isStartup1Winner = random.nextBoolean();

            StartupModel winner = isStartup1Winner ? startup1 : startup2;
            System.out.println("Vencedor do SharkFight: " + winner.getName());

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
        } catch (Exception e) {
            System.out.println("ERRO no SharkFight: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}