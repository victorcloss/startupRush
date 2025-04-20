package com.victorclossduarte.startupRush.controller;


import com.victorclossduarte.startupRush.service.StartupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping
public class StartupController {
    @Autowired
    StartupService service;

    @GetMapping("/")
    public String home(){
        return "home-page";
    }

}
