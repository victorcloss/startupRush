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
    StartupRepository repo;

    public List<StartupModel> getAllStartups() {
        return (List<StartupModel>) repo.findAll();
    }

    public StartupModel getStartupById(int id) {
        Optional<StartupModel> result = repo.findById(id);
        if(result.isPresent()){
            return result.get();
        }
        return new StartupModel();
    }

    public StartupModel addStartup(StartupModel startup) {
        if(startup.getName() == null || startup.getName().isEmpty()){
            throw new IllegalArgumentException("Nome deve ser preenchido");
        }else if(startup.getFoundationYear()< 1800 || startup.getFoundationYear()>2025){
            throw new IllegalArgumentException("Digite o ano correto");
        }
        return repo.save(startup);
    }

    public StartupModel updateStartup(int id, StartupModel updatedStartup) {
        StartupModel oldStartup = getStartupById(id);
        if (oldStartup.getId() == 0) {
            throw new RuntimeException("Startup não encontrada");
        }

        oldStartup.setName(updatedStartup.getName());
        oldStartup.setSlogan(updatedStartup.getSlogan());
        oldStartup.setFoundationYear(updatedStartup.getFoundationYear());
        oldStartup.setPoints(updatedStartup.getPoints());
        // pontuacoes atualizadas
        oldStartup.setBugProd(updatedStartup.getBugProd());
        oldStartup.setFakeNewsP(updatedStartup.getFakeNewsP());
        oldStartup.setIrritInv(updatedStartup.getIrritInv());
        oldStartup.setGoodPitch(updatedStartup.getGoodPitch());
        oldStartup.setUserTract(updatedStartup.getUserTract());
        oldStartup.setSharkFights(updatedStartup.getSharkFights());

        return repo.save(oldStartup);
    }


}
