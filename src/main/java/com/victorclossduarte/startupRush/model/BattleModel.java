package com.victorclossduarte.startupRush.model;

import com.victorclossduarte.startupRush.enums.BattleStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Entity
@Table(name = "tb_battles")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BattleModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "rodada_id", nullable = false)
    private RoundModel round;

    @Enumerated(EnumType.STRING)
    private BattleStatus status = BattleStatus.PENDENTE;

    private Boolean sharkFight = false;

    @OneToMany(mappedBy = "battle", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<StartupBattleModel> participants = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "winner_id")
    private StartupModel winner;


    public StartupModel verifyWinner(StartupModel startupX, StartupModel startupY) {
        if (startupX.getPoints() > startupY.getPoints()) {
            this.winner = startupX;
            this.sharkFight = false;
        } else if (startupX.getPoints() < startupY.getPoints()) {
            this.winner = startupY;
            this.sharkFight = false;
        } else {
            this.sharkFight = true;
            this.winner = sharkFight(startupX, startupY);
        }
        return this.winner;
    }

    public StartupModel sharkFight(StartupModel startupX, StartupModel startupY) {
        this.sharkFight = true;

        Random random = new Random();
        boolean isStartup1Winner = random.nextBoolean();

        StartupModel winner;
        if (isStartup1Winner) {
            startupX.setPoints(startupX.getPoints() + 2);
            startupX.setSharkFights(startupX.getSharkFights() + 1);
            winner = startupX;
        } else {
            startupY.setPoints(startupY.getPoints() + 2);
            startupY.setSharkFights(startupY.getSharkFights() + 1);
            winner = startupY;
        }

        return winner;
    }
}