package com.victorclossduarte.startupRush.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "tb_startups")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StartupModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String slogan;
    @Column(nullable = false)
    private int foundationYear;
    @Column(nullable = false)
    private int points = 70;

    // atributos de pontuacao
    private int goodPitc = 0;
    private int bugProd = 0;
    private int userTract = 0;
    private int irritInv = 0;
    private int fakeNewsP = 0;
    private int sharkFights = 0;
    private int wins = 0;


    // muitas startups participam de muitos torneios
    // estrutura de caso para a criacao de varios torneios na memoria
    @ManyToMany(mappedBy = "startups")
    private Set<TournamentModel> tournaments = new HashSet<>();

    // 1 startup pode ter varias participacoes
    @OneToMany(mappedBy = "startup")
    private List<StartupBattleModel> participations = new ArrayList<>();


}
