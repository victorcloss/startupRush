package com.victorclossduarte.startupRush.repository;

import com.victorclossduarte.startupRush.enums.BattleStatus;
import com.victorclossduarte.startupRush.model.BattleModel;
import com.victorclossduarte.startupRush.model.RoundModel;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BattleRepository extends CrudRepository<BattleModel, Integer> {

    List<BattleModel> findByRound(RoundModel round);

    List<BattleModel> findByStatus(BattleStatus status);

}
