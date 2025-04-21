package com.victorclossduarte.startupRush.service;


import com.victorclossduarte.startupRush.model.StartupModel;
import com.victorclossduarte.startupRush.repository.BattleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BattleService {

    @Autowired
    BattleRepository repository;


    // metodos de pontuacao

    public void pitchConv(StartupModel startupX) {
        startupX.setPoints(startupX.getPoints() + 6);
    }

    public void prodBugs(StartupModel startupX) {
        startupX.setPoints(startupX.getPoints() - 4);
    }

    public void userTraction(StartupModel startupX) {
        startupX.setPoints(startupX.getPoints() + 3);
    }

    public void irritInvest(StartupModel startupX) {
        startupX.setPoints(startupX.getPoints() - 6);
    }

    public void fakeNewsP(StartupModel startupX) {
        startupX.setPoints(startupX.getPoints() - 8);
    }

    public void winner(StartupModel startupX) {
        startupX.setPoints(startupX.getPoints() + 30);
    }



}
