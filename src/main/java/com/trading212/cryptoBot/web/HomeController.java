package com.trading212.cryptoBot.web;

import com.trading212.cryptoBot.service.impl.CryptoApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @Autowired
    public CryptoApiService cryptoApiService;

    @GetMapping
    public String viewIndex(){
        cryptoApiService.getTopGainers();
        return "index";
    }
}