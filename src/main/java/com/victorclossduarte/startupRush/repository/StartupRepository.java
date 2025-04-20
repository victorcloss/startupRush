package com.victorclossduarte.startupRush.repository;

import com.victorclossduarte.startupRush.model.StartupModel;

import org.springframework.data.repository.CrudRepository;

public interface StartupRepository extends CrudRepository<StartupModel,Integer> {
}
