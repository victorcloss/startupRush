package com.victorclossduarte.startupRush.service;


import com.victorclossduarte.startupRush.model.StartupModel;
import com.victorclossduarte.startupRush.model.TournamentModel;
import com.victorclossduarte.startupRush.repository.BattleRepository;
import com.victorclossduarte.startupRush.repository.RoundRepository;
import com.victorclossduarte.startupRush.repository.StartupRepository;
import com.victorclossduarte.startupRush.repository.TournamentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TournamentService {

    @Autowired
    private TournamentRepository tRepo;
    @Autowired
    private StartupRepository sRepo;
    @Autowired
    private RoundRepository rRepo;


    public TournamentModel createTournament(){
        TournamentModel tournament = new TournamentModel();
        return tRepo.save(tournament);
    }



    public TournamentModel addStartupToTournament(int tournamentId, int startupId){
        TournamentModel tournament = tRepo.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Torneio não encontrado"));

        StartupModel startup = sRepo.findById(startupId)
                .orElseThrow(() -> new RuntimeException("Startup não encontrada"));

        tournament.getStartups().add(startup);
        return tRepo.save(tournament);
    }
}
