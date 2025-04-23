package com.victorclossduarte.startupRush.controller;

import com.victorclossduarte.startupRush.model.StartupModel;
import com.victorclossduarte.startupRush.service.StartupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

@Controller
@RequestMapping("/market-analysis")
public class MarketAnalysisController {

    @Autowired
    private StartupService startupService;

    @GetMapping
    public String getAllMarketAnalysis(Model model) {
        List<StartupModel> startups = startupService.getAllStartups();
        model.addAttribute("startups", startups);
        return "market-analysis/list";
    }


    @GetMapping("/{id}")
    public String getMarketAnalysis(@PathVariable int id, Model model) {
        try {
            StartupModel startup = startupService.getStartupById(id);

            // Gerar dados de análise de mercado simulados
            Map<String, Object> marketData = generateMarketData(startup);
            model.addAttribute("startup", startup);
            model.addAttribute("marketData", marketData);

            return "market-analysis/dashboard";
        } catch (Exception e) {
            return "redirect:/startups";
        }
    }

    private Map<String, Object> generateMarketData(StartupModel startup) {
        Map<String, Object> data = new HashMap<>();
        Random random = new Random(startup.getId()); // Usar ID como seed para consistência

        // Crescimento potencial (baseado na pontuação)
        double growthRate = 5 + (startup.getPoints() / 10.0);
        data.put("growthRate", Math.round(growthRate * 10.0) / 10.0); // Arredondar para 1 casa decimal

        // Dados de crescimento para gráfico (últimos 6 meses simulados)
        List<Integer> growthData = new ArrayList<>();
        int baseValue = 100;
        for (int i = 0; i < 6; i++) {
            baseValue += (int)(baseValue * (growthRate/100));
            growthData.add(baseValue);
        }
        data.put("growthData", growthData);

        // Taxa de adoção (%)
        double adoptionRate = Math.min(80, 20 + (startup.getPoints() / 5.0) + random.nextInt(10));
        data.put("adoptionRate", Math.round(adoptionRate * 10.0) / 10.0); // Arredondar para 1 casa decimal

        // Avaliação estimada
        long valuation = (long) (500000 + (startup.getPoints() * 50000) + random.nextInt(1000000));
        data.put("valuation", valuation);

        // Métrica de retenção (%)
        double retention = Math.min(95, 60 + (startup.getGoodPitch() * 5) + (startup.getUserTract() * 3) - (startup.getBugProd() * 2));
        data.put("retentionRate", Math.round(retention * 10.0) / 10.0);

        // Custo de aquisição de cliente (CAC)
        int cac = 50 - (startup.getGoodPitch() * 2) + (startup.getIrritInv() * 5);
        if (cac < 10) cac = 10;
        data.put("cac", cac);

        // Setores de mercado (baseado no ano de fundação e características da startup)
        List<String> sectors = new ArrayList<>();
        if (startup.getFoundationYear() > 2015) {
            sectors.add("Mobile");
            sectors.add("SaaS");
        }
        if (startup.getFoundationYear() > 2018) {
            sectors.add("AI/ML");
        }
        if (startup.getGoodPitch() > 2) {
            sectors.add("Enterprise");
        }
        if (startup.getUserTract() > 1) {
            sectors.add("Consumidor");
        }
        if (startup.getBugProd() > 2) {
            sectors.add("Hardware");
        }
        if (sectors.isEmpty()) {
            sectors.add("B2C");
            sectors.add("Web");
        }
        data.put("sectors", sectors);

        // Matriz de mercado: Tamanho (eixo X) vs. Competitividade (eixo Y)
        // Um valor maior indica um mercado maior ou mais competitivo
        int marketSize = 30 + (startup.getPoints() / 3) + random.nextInt(20);
        int marketCompetitiveness = 20 + random.nextInt(60);
        data.put("marketSize", marketSize);
        data.put("marketCompetitiveness", marketCompetitiveness);

        // Matriz de investimento: Risco (eixo X) vs. Retorno potencial (eixo Y)
        int investmentRisk = 70 - (startup.getPoints() / 3) + (startup.getFakeNewsP() * 5) + random.nextInt(20);
        int investmentReturn = 40 + (startup.getPoints() / 2) + (startup.getWins() * 10) + random.nextInt(30);
        data.put("investmentRisk", Math.min(100, Math.max(10, investmentRisk)));
        data.put("investmentReturn", Math.min(100, Math.max(10, investmentReturn)));

        // Próxima rodada de investimento recomendada
        String fundingRound;
        int wins = startup.getWins();
        if (wins == 0) {
            fundingRound = "Pre-seed";
        } else if (wins == 1) {
            fundingRound = "Seed";
        } else if (wins == 2) {
            fundingRound = "Series A";
        } else if (wins == 3) {
            fundingRound = "Series B";
        } else {
            fundingRound = "Series C+";
        }
        data.put("fundingRound", fundingRound);

        return data;
    }
}