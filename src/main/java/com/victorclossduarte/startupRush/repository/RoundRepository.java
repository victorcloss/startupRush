package com.victorclossduarte.startupRush.repository;

import com.victorclossduarte.startupRush.enums.RoundStatus;
import com.victorclossduarte.startupRush.model.RoundModel;
import com.victorclossduarte.startupRush.model.TournamentModel;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RoundRepository extends CrudRepository<RoundModel, Integer> {
    List<RoundModel> findByTournament(TournamentModel tournament);
    List<RoundModel> findByTournamentAndNumero(TournamentModel tournament, Integer numero);
    List<RoundModel> findByStatus(RoundStatus status);
}