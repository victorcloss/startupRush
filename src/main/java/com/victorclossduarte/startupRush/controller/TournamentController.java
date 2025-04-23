package com.victorclossduarte.startupRush.controller;

import com.victorclossduarte.startupRush.enums.TournamentStatus;
import com.victorclossduarte.startupRush.model.RoundModel;
import com.victorclossduarte.startupRush.model.TournamentModel;
import com.victorclossduarte.startupRush.service.StartupService;
import com.victorclossduarte.startupRush.service.TournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/tournaments")
public class TournamentController {
    @Autowired
    private TournamentService tournamentService;

    @Autowired
    private StartupService startupService;

    @GetMapping
    public String getAllTournaments(Model model) {
        List<TournamentModel> tournaments = tournamentService.getAllTournaments();
        model.addAttribute("tournaments", tournaments);

        long activeCount = tournaments.stream()
                .filter(t -> t.getStatus() == TournamentStatus.EM_ANDAMENTO)
                .count();

        long finishedCount = tournaments.stream()
                .filter(t -> t.getStatus() == TournamentStatus.FINALIZADO)
                .count();

        long notStartedCount = tournaments.stream()
                .filter(t -> t.getStatus() == TournamentStatus.NAO_INICIADO)
                .count();

        model.addAttribute("activeCount", activeCount);
        model.addAttribute("finishedCount", finishedCount);
        model.addAttribute("notStartedCount", notStartedCount);

        return "tournament/list";
    }
    @GetMapping("/{id}")
    public String getTournamentDetails(@PathVariable int id, Model model) {
        TournamentModel tournament = tournamentService.getTournamentById(id);
        model.addAttribute("tournament", tournament);

        // Adiciona as rodadas do torneio ao modelo
        List<RoundModel> rounds = tournamentService.getTournamentRounds(id);
        model.addAttribute("rounds", rounds);

        return "tournament/details";
    }

    @GetMapping("/new")
    public String newTournamentForm() {
        return "tournament/form";
    }

    @PostMapping
    public String createTournament(RedirectAttributes redirectAttributes) {
        try {
            TournamentModel tournament = tournamentService.createTournament();
            redirectAttributes.addFlashAttribute("successMessage", "Torneio criado com sucesso!");
            return "redirect:/tournaments/" + tournament.getTournamentId() + "/startups";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/tournaments/new";
        }
    }

    @GetMapping("/{id}/startups")
    public String selectStartups(@PathVariable int id, Model model) {
        TournamentModel tournament = tournamentService.getTournamentById(id);
        model.addAttribute("tournament", tournament);
        model.addAttribute("availableStartups", startupService.getAllStartups());
        return "tournament/select-startups.html";
    }

    @PostMapping("/{tournamentId}/startups/{startupId}")
    public String addStartupToTournament(@PathVariable int tournamentId, @PathVariable int startupId,
                                         RedirectAttributes redirectAttributes) {
        try {
            tournamentService.addStartupToTournament(tournamentId, startupId);
            redirectAttributes.addFlashAttribute("successMessage", "Startup adicionada ao torneio com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/tournaments/" + tournamentId + "/startups";
    }

    @GetMapping("/{tournamentId}/startups/{startupId}/remove")
    public String removeStartupFromTournament(@PathVariable int tournamentId, @PathVariable int startupId,
                                              RedirectAttributes redirectAttributes) {
        try {
            tournamentService.removeStartupFromTournament(tournamentId, startupId);
            redirectAttributes.addFlashAttribute("successMessage", "Startup removida do torneio com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/tournaments/" + tournamentId + "/startups";
    }

    @PostMapping("/{id}/start")
    public String startTournament(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            tournamentService.startTournament(id);
            redirectAttributes.addFlashAttribute("successMessage", "Torneio iniciado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/tournaments/" + id;
    }

    @PostMapping("/{id}/next-round")
    public String advanceToNextRound(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            TournamentModel tournament = tournamentService.advanceToNextRound(id);
            if (tournament.getStatus().toString().equals("FINALIZADO")) {
                redirectAttributes.addFlashAttribute("successMessage",
                        "Torneio finalizado! Campeão: " + tournament.getChampion().getName());
            } else {
                redirectAttributes.addFlashAttribute("successMessage",
                        "Avançado para a rodada " + tournament.getCurrentRound() + " com sucesso!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao avançar rodada: " + e.getMessage());
            e.printStackTrace(); // Log completo do erro para debugging
        }
        return "redirect:/tournaments/" + id;
    }

    @GetMapping("/{id}/current-round")
    public String getCurrentRound(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            RoundModel currentRound = tournamentService.getCurrentRound(id);
            return "redirect:/rounds/" + currentRound.getRoundId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao buscar rodada atual: " + e.getMessage());
            return "redirect:/tournaments/" + id;
        }
    }
}