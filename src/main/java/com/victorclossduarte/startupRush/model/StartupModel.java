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

    // atributos de pontuação
    private int goodPitch = 0;
    private int bugProd = 0;
    private int userTract = 0;
    private int irritInv = 0;
    private int fakeNewsP = 0;
    private int sharkFights = 0;
    private int wins = 0;

    // muitas startups participam de muitos torneios
    // usar EAGER para garantir que os dados sejam carregados
    @ManyToMany(mappedBy = "startups", fetch = FetchType.EAGER)
    private Set<TournamentModel> tournaments = new HashSet<>();

    // 1 startup pode ter várias participações
    @OneToMany(mappedBy = "startup")
    private List<StartupBattleModel> participations = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StartupModel that = (StartupModel) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}