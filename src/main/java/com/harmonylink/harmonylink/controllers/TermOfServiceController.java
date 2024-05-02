package com.harmonylink.harmonylink.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/terms-of-service")
public class TermOfServiceController {

    @GetMapping
    public String showTermsOfServicePage() {
        return "termsOfService";
    }

}
