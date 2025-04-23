package com.victorclossduarte.startupRush.controller;

import com.victorclossduarte.startupRush.enums.BattleStatus;
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
    public String getBattleDetails(@PathVariable int id, Model model) {
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
    public String applyPitchConvincente(@PathVariable int battleId, @PathVariable int startupId,
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
    public String applyProdutoComBugs(@PathVariable int battleId, @PathVariable int startupId,
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
    public String applyBoaTracaoUsuarios(@PathVariable int battleId, @PathVariable int startupId,
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
    public String applyInvestidorIrritado(@PathVariable int battleId, @PathVariable int startupId,
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
    public String applyFakeNewsNoPitch(@PathVariable int battleId, @PathVariable int startupId,
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
    public String finalizeBattle(@PathVariable int id, RedirectAttributes redirectAttributes) {
        System.out.println("Controller: Solicitação para finalizar batalha ID: " + id);

        try {
            BattleModel battle = battleService.finalizeBattle(id);

            if (battle.getStatus() != BattleStatus.FINALIZADA) {
                System.out.println("ERRO: Batalha não foi finalizada corretamente!");
                redirectAttributes.addFlashAttribute("errorMessage",
                        "A batalha não pôde ser finalizada corretamente. Por favor, tente novamente.");
                return "redirect:/battles/" + id;
            }

            if (battle.getWinner() == null) {
                System.out.println("ERRO: Vencedor não definido após finalização!");
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Erro ao determinar o vencedor. Por favor, tente novamente.");
                return "redirect:/battles/" + id;
            }

            String message = "Batalha finalizada com sucesso! Vencedor: " + battle.getWinner().getName();
            if (battle.getSharkFight()) {
                message += " (após SharkFight)";
            }

            System.out.println("Batalha finalizada com sucesso: " + message);
            redirectAttributes.addFlashAttribute("successMessage", message);

            if (battle.getRound() != null) {
                return "redirect:/rounds/" + battle.getRound().getRoundId();
            } else {
                return "redirect:/";
            }

        } catch (Exception e) {
            System.out.println("Erro ao finalizar batalha: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao finalizar a batalha: " + e.getMessage());
            return "redirect:/battles/" + id;
        }
    }
}