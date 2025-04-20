package com.victorclossduarte.startupRush.service;


import com.victorclossduarte.startupRush.model.StartupModel;
import com.victorclossduarte.startupRush.repository.StartupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StartupService {
    @Autowired
    StartupRepository repository;

    public List<StartupModel> getAllStartups() {
        return (List<StartupModel>) repository.findAll();
    }

    public StartupModel getStartupById(int id) {
        Optional<StartupModel> result = repository.findById(id);
        if(result.isPresent()){
            return result.get();
        }
        return new StartupModel();
    }
}
