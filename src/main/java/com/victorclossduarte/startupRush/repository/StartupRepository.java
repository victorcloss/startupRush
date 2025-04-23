package com.victorclossduarte.startupRush.repository;

import com.victorclossduarte.startupRush.model.StartupModel;

import org.hibernate.sql.ast.tree.expression.Star;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface StartupRepository extends CrudRepository<StartupModel,Integer> {

    Optional<StartupModel> findByName(String name);
    List<StartupModel> findByPointsGreaterThan(int points);
}
