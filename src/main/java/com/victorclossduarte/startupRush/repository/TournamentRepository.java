package com.victorclossduarte.startupRush.repository;

import com.victorclossduarte.startupRush.enums.TournamentStatus;
import com.victorclossduarte.startupRush.model.TournamentModel;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TournamentRepository extends CrudRepository<TournamentModel, Integer> {

    List<TournamentModel> findByStatus(TournamentStatus status);

}
