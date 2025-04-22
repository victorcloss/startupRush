package com.victorclossduarte.startupRush.controller;

import com.victorclossduarte.startupRush.model.RoundModel;
import com.victorclossduarte.startupRush.repository.RoundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/rounds")
public class RoundController {
    @Autowired
    private RoundRepository roundRepository;

    @GetMapping("/{id}")
    public String getRoundDetails(@PathVariable int id, Model model) {
        Optional<RoundModel> round = roundRepository.findById(id);
        if (!round.isPresent()) {
            return "redirect:/";
        }

        model.addAttribute("round", round.get());
        return "round/details";
    }
}