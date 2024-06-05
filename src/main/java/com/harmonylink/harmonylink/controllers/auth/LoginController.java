package com.harmonylink.harmonylink.controllers.auth;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth/login")
public class LoginController {

    @GetMapping
    public String showLoginPage(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("loginError", "Podane dane logowania są nieprawidłowe lub konto zostało zablokowane");
        }

        return "authPages/login/login";
    }

}
