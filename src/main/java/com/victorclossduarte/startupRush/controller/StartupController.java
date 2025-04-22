package com.victorclossduarte.startupRush.controller;

import com.victorclossduarte.startupRush.model.StartupModel;
import com.victorclossduarte.startupRush.service.StartupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/startups")
public class StartupController {
    @Autowired
    private StartupService service;

    @GetMapping
    public String getAllStartups(Model model) {
        List<StartupModel> startups = service.getAllStartups();
        model.addAttribute("startups", startups);

        // Verifica se há startups suficientes para iniciar um torneio
        boolean canStartTournament = startups.size() >= 4;
        model.addAttribute("canStartTournament", canStartTournament);

        // Opções de quantidade para torneio
        List<Integer> tournamentOptions = new ArrayList<>();
        if (startups.size() >= 4) tournamentOptions.add(4);
        if (startups.size() >= 6) tournamentOptions.add(6);
        if (startups.size() >= 8) tournamentOptions.add(8);
        model.addAttribute("tournamentOptions", tournamentOptions);

        return "startup/list";
    }

    @GetMapping("/{id}")
    public String getStartupDetails(@PathVariable int id, Model model) {
        model.addAttribute("startup", service.getStartupById(id));
        return "startup/details";
    }

    @GetMapping("/new")
    public String newStartupForm(Model model) {
        model.addAttribute("startup", new StartupModel());
        return "startup/form";
    }

    @PostMapping
    public String createStartup(@ModelAttribute StartupModel startup, RedirectAttributes redirectAttributes) {
        try {
            service.createStartup(startup);
            redirectAttributes.addFlashAttribute("successMessage", "Startup criada com sucesso!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/startups/new";
        }
        return "redirect:/startups";
    }

    @GetMapping("/edit/{id}")
    public String editStartupForm(@PathVariable int id, Model model) {
        model.addAttribute("startup", service.getStartupById(id));
        return "startup/form";
    }

    @PostMapping("/update/{id}")
    public String updateStartup(@PathVariable int id, @ModelAttribute StartupModel startup,
                                RedirectAttributes redirectAttributes) {
        try {
            service.updateStartup(id, startup);
            redirectAttributes.addFlashAttribute("successMessage", "Startup atualizada com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/startups/edit/" + id;
        }
        return "redirect:/startups";
    }

    @GetMapping("/delete/{id}")
    public String deleteStartup(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            service.deleteStartup(id);
            redirectAttributes.addFlashAttribute("successMessage", "Startup excluída com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/startups";
    }

    @GetMapping("/reset/{id}")
    public String resetStartupPoints(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            service.resetStartupPoints(id);
            redirectAttributes.addFlashAttribute("successMessage", "Pontuação da startup resetada com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/startups/" + id;
    }
}