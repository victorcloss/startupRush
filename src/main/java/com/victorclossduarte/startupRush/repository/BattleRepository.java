package com.victorclossduarte.startupRush.repository;

import com.victorclossduarte.startupRush.enums.BattleStatus;
import com.victorclossduarte.startupRush.model.BattleModel;
import com.victorclossduarte.startupRush.model.RoundModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BattleRepository extends JpaRepository<BattleModel, Integer> {
    List<BattleModel> findByRound(RoundModel round);
    List<BattleModel> findByStatus(BattleStatus status);
    Optional<BattleModel> findById(int id);
}