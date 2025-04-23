package com.victorclossduarte.startupRush.controller;

import com.victorclossduarte.startupRush.enums.TournamentStatus;
import com.victorclossduarte.startupRush.model.TournamentModel;
import com.victorclossduarte.startupRush.service.StartupService;
import com.victorclossduarte.startupRush.service.TournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class HomeController {
    @Autowired
    private TournamentService tournamentService;

    @Autowired
    private StartupService startupService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("startups", startupService.getAllStartups());
        List<TournamentModel> tournaments = tournamentService.getAllTournaments();
        model.addAttribute("tournaments", tournaments);

        long activeTournaments = tournaments.stream()
                .filter(t -> t.getStatus() == TournamentStatus.EM_ANDAMENTO)
                .count();
        model.addAttribute("activeTournaments", activeTournaments);

        long champions = tournaments.stream()
                .filter(t -> t.getChampion() != null)
                .count();
        model.addAttribute("champions", champions);

        return "home-page";
    }

    @PostMapping("/new-tournament")
    public String createStartupsForm(@RequestParam("numStartups") int numStartups, RedirectAttributes redirectAttributes) {
        try {if (startupService.getAllStartups().size() < numStartups) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Não há startups suficientes para criar um torneio com " + numStartups + " participantes");
                return "redirect:/";
            }
            TournamentModel tournament = tournamentService.createTournament();
            redirectAttributes.addFlashAttribute("numStartups", numStartups);
            redirectAttributes.addFlashAttribute("successMessage", "Torneio criado com sucesso! Selecione as startups participantes.");
            return "redirect:/tournaments/" + tournament.getTournamentId() + "/startups";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/";
        }
    }
}