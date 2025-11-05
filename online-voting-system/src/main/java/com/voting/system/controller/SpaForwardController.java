package com.voting.system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaForwardController {

    // Serve SPA entry at root. For deep links, use the root URL.
    @GetMapping("/")
    public String index() {
        return "forward:/index.html";
    }
}


