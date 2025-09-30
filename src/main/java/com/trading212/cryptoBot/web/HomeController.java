package com.trading212.cryptoBot.web;

import com.trading212.cryptoBot.service.impl.CryptoApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {
    @Autowired
    public CryptoApiService cryptoApiService;

    @GetMapping
    public String viewIndex(){
        List<String> arr = new ArrayList<>();
        arr.add("bitcoin");
        arr.add("ethereum");
        arr.add("binancecoin");
        cryptoApiService.getPricesById(arr);
        return "index";
    }
}