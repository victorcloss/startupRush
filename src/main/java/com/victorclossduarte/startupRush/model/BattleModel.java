package com.victorclossduarte.startupRush.model;

import com.victorclossduarte.startupRush.enums.BattleStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_battles")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BattleModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;


    @ManyToOne
    @JoinColumn(name = "rodada_id", nullable = false)
    private RoundModel round;

    @Enumerated(EnumType.STRING)
    private BattleStatus status = BattleStatus.PENDENTE; // PENDENTE, FINALIZADA

    private Boolean sharkFight = false;

    @OneToMany(mappedBy = "battle", cascade = CascadeType.ALL)
    private List<StartupBattleModel> participants = new ArrayList<>();

    @OneToOne
    private StartupModel winner;



    // metodos de pontuacao

    public void pitchConv(StartupModel startupX) {
        startupX.setPoints(startupX.getPoints() + 6);
    }

    public void prodBugs(StartupModel startupX) {
        startupX.setPoints(startupX.getPoints() - 4);
    }

    public void userTraction(StartupModel startupX) {
        startupX.setPoints(startupX.getPoints() + 3);
    }

    public void irritInvest(StartupModel startupX) {
        startupX.setPoints(startupX.getPoints() - 6);
    }

    public void fakeNewsP(StartupModel startupX) {
        startupX.setPoints(startupX.getPoints() - 8);
    }

    public void winner(StartupModel startupX) {
        startupX.setPoints(startupX.getPoints() + 30);
    }

    public StartupModel verifyWinner(StartupModel startupX, StartupModel startupY) {
        if (startupX.getPoints() > startupY.getPoints()) {
           this.winner = startupX;
        } else if (startupX.getPoints()< startupY.getPoints()) {
            this.winner = startupY;
        } else{this.winner = sharkFight(startupX, startupY);}
    return this.winner;
    }

    public StartupModel sharkFight(StartupModel startupX, StartupModel startupY) {

        // terminar logica de sharkFight usando random
        // Se houver empate ao final da batalha, o sistema automaticamente realiza uma Shark Fight, uma
        // rodada relâmpago onde uma startup recebe aleatoriamente +2 pontos. O novo placar decide a
        //vencedora da disputa.


        return startupX;
    }
}
