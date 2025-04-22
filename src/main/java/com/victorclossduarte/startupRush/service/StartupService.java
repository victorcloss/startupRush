package com.victorclossduarte.startupRush.service;

import com.victorclossduarte.startupRush.model.StartupModel;
import com.victorclossduarte.startupRush.model.TournamentModel;
import com.victorclossduarte.startupRush.repository.StartupRepository;
import com.victorclossduarte.startupRush.repository.TournamentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StartupService {
    @Autowired
    StartupRepository repository;

    @Autowired
    TournamentRepository tournamentRepository;

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

    public StartupModel createStartup(StartupModel startup) {
        if (startup.getName() == null || startup.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("O nome da startup é obrigatório");
        }
        if (startup.getSlogan() == null || startup.getSlogan().trim().isEmpty()) {
            throw new IllegalArgumentException("O slogan da startup é obrigatório");
        }
        if (startup.getFoundationYear() <= 0) {
            throw new IllegalArgumentException("O ano de fundação deve ser válido");
        }


        if (startup.getPoints() <= 0) {
            startup.setPoints(70);
        }
        return repository.save(startup);
    }

    public StartupModel updateStartup(int id, StartupModel updatedStartup) {
        StartupModel existingStartup = getStartupById(id);
        if (existingStartup.getId() == 0) {
            throw new RuntimeException("Startup não encontrada");
        }
        existingStartup.setName(updatedStartup.getName());
        existingStartup.setSlogan(updatedStartup.getSlogan());
        existingStartup.setFoundationYear(updatedStartup.getFoundationYear());

        return repository.save(existingStartup);
    }

    public void deleteStartup(int id) {
        Optional<StartupModel> startup = repository.findById(id);
        if (startup.isPresent()) {
            repository.deleteById(id);
        } else {
            throw new RuntimeException("Startup não encontrada");
        }
    }

    public void resetStartupPoints(int id) {
        StartupModel startup = getStartupById(id);
        if (startup.getId() == 0) {
            throw new RuntimeException("Startup não encontrada");
        }
        startup.setPoints(70);
        startup.setGoodPitch(0);
        startup.setBugProd(0);
        startup.setUserTract(0);
        startup.setIrritInv(0);
        startup.setFakeNewsP(0);
        startup.setSharkFights(0);
        startup.setWins(0);
        repository.save(startup);
    }

    public void registerStartupInTournament(int startupId, int tournamentId) {
        StartupModel startup = getStartupById(startupId);
        if (startup.getId() == 0) {
            throw new RuntimeException("Startup não encontrada");
        }
        TournamentModel tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Torneio não encontrado"));
        tournament.getStartups().add(startup);
        tournamentRepository.save(tournament);
    }
}