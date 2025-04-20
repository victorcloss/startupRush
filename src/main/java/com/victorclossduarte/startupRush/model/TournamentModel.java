package com.victorclossduarte.startupRush.model;

import com.victorclossduarte.startupRush.enums.TournamentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "tb_tournament")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TournamentModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int tournamentId;

    @Enumerated(EnumType.STRING)
    private TournamentStatus status = TournamentStatus.NAO_INICIADO;

    private int currentRound =0;

    // registro startups no torneio
    @ManyToMany
    @JoinTable(
            name = "tournament_startup",
            joinColumns = @JoinColumn(name = "tournament_id"),
            inverseJoinColumns = @JoinColumn(name = "startup_id")
    )
    private Set<StartupModel> startups = new HashSet<>();

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL)
    private List<RoundModel> rounds = new ArrayList<>();

    @OneToOne
    private StartupModel champion;



}
