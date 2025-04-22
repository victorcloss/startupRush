package com.victorclossduarte.startupRush.repository;

import com.victorclossduarte.startupRush.model.BattleModel;
import com.victorclossduarte.startupRush.model.StartupBattleModel;
import com.victorclossduarte.startupRush.model.StartupModel;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface StartupBattleRepository extends CrudRepository<StartupBattleModel, Long> {

    List<StartupBattleModel> findByStartup(StartupModel startup);
    List<StartupBattleModel> findByBattle(BattleModel battle);
    Optional<StartupBattleModel> findByStartupAndBattle(StartupModel startup, BattleModel battle);

}
