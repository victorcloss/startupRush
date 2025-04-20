package com.victorclossduarte.startupRush.model;

import com.victorclossduarte.startupRush.enums.RoundStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_rounds")
@Data
public class RoundModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int roundId;

    @Column(nullable = false)
    private Integer numero;

    @Enumerated(EnumType.STRING)
    private RoundStatus status = RoundStatus.EM_ANDAMENTO; // EM_ANDAMENTO, FINALIZADA

    // muitas rodadas para um torneio
    @ManyToOne
    @JoinColumn(name = "tournament_id", nullable = false)
    private TournamentModel tournament;

    // uma rodada para muitas batalhas
    @OneToMany(mappedBy = "round", cascade = CascadeType.ALL)
    private List<BattleModel> battles = new ArrayList<>();

}
