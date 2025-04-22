package com.victorclossduarte.startupRush.controller;

import com.victorclossduarte.startupRush.model.BattleModel;
import com.victorclossduarte.startupRush.service.BattleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/battles")
public class BattleController {
    @Autowired
    private BattleService battleService;

    @GetMapping("/{id}")
    public String getBattleDetails(@PathVariable long id, Model model) {
        try {
            BattleModel battle = battleService.getBattleById(id);
            model.addAttribute("battle", battle);
            return "battle/details";
        } catch (Exception e) {
            return "redirect:/";
        }
    }

    // Métodos para aplicar eventos nas batalhas

    @PostMapping("/{battleId}/startup/{startupId}/pitch")
    public String applyPitchConvincente(@PathVariable long battleId, @PathVariable int startupId,
                                        RedirectAttributes redirectAttributes) {
        try {
            battleService.applyPitchConvincente(battleId, startupId);
            redirectAttributes.addFlashAttribute("successMessage", "Pitch convincente aplicado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/battles/" + battleId;
    }

    @PostMapping("/{battleId}/startup/{startupId}/bugs")
    public String applyProdutoComBugs(@PathVariable long battleId, @PathVariable int startupId,
                                      RedirectAttributes redirectAttributes) {
        try {
            battleService.applyProdutoComBugs(battleId, startupId);
            redirectAttributes.addFlashAttribute("successMessage", "Produto com bugs aplicado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/battles/" + battleId;
    }

    @PostMapping("/{battleId}/startup/{startupId}/traction")
    public String applyBoaTracaoUsuarios(@PathVariable long battleId, @PathVariable int startupId,
                                         RedirectAttributes redirectAttributes) {
        try {
            battleService.applyBoaTracaoUsuarios(battleId, startupId);
            redirectAttributes.addFlashAttribute("successMessage", "Boa tração de usuários aplicada com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/battles/" + battleId;
    }

    @PostMapping("/{battleId}/startup/{startupId}/investor")
    public String applyInvestidorIrritado(@PathVariable long battleId, @PathVariable int startupId,
                                          RedirectAttributes redirectAttributes) {
        try {
            battleService.applyInvestidorIrritado(battleId, startupId);
            redirectAttributes.addFlashAttribute("successMessage", "Investidor irritado aplicado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/battles/" + battleId;
    }

    @PostMapping("/{battleId}/startup/{startupId}/fakenews")
    public String applyFakeNewsNoPitch(@PathVariable long battleId, @PathVariable int startupId,
                                       RedirectAttributes redirectAttributes) {
        try {
            battleService.applyFakeNewsNoPitch(battleId, startupId);
            redirectAttributes.addFlashAttribute("successMessage", "Fake news no pitch aplicada com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/battles/" + battleId;
    }

    @PostMapping("/{id}/finalize")
    public String finalizeBattle(@PathVariable long id, RedirectAttributes redirectAttributes) {
        try {
            BattleModel battle = battleService.finalizeBattle(id);
            String message = "Batalha finalizada com sucesso! Vencedor: " + battle.getWinner().getName();
            if (battle.getSharkFight()) {
                message += " (após SharkFight)";
            }
            redirectAttributes.addFlashAttribute("successMessage", message);
            return "redirect:/rounds/" + battle.getRound().getRoundId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/battles/" + id;
        }
    }
}