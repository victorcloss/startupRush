package com.victorclossduarte.startupRush.model;


// classe criada como entidade associativa do banco de dados para uma relacao n para n

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_startup_battles")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StartupBattleModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "startup_id", nullable = false)
    private StartupModel startup;

    @ManyToOne
    @JoinColumn(name = "battle_id", nullable = false)
    private BattleModel battle;

    private Integer pontuacaoInicial;
    private Integer pontuacaoFinal;

    // eventos que so podem ocorrer 1 vez
    private Boolean pitchConvincente = false;
    private Boolean produtoComBugs = false;
    private Boolean boaTracaoUsuarios = false;
    private Boolean investidorIrritado = false;
    private Boolean fakeNewsNoPitch = false;
    private Boolean ganhouSharkFight = false;
}
