package com.harmonylink.harmonylink.controllers.auth;

import com.harmonylink.harmonylink.models.token.ResetPasswordToken;
import com.harmonylink.harmonylink.repositories.token.ResetPasswordTokenRepository;
import com.harmonylink.harmonylink.services.user.useraccount.ResetPasswordService;
import com.harmonylink.harmonylink.services.user.useraccount.exceptions.EmailNotFoundException;
import com.harmonylink.harmonylink.services.user.useraccount.exceptions.InvalidPasswordException;
import com.harmonylink.harmonylink.services.user.useraccount.exceptions.InvalidTokenException;
import com.harmonylink.harmonylink.services.user.useraccount.exceptions.PasswordsMatchingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/auth")
public class ResetPasswordController {

    private final ResetPasswordService resetPasswordService;
    private final ResetPasswordTokenRepository resetPasswordTokenRepository;


    @Autowired
    public ResetPasswordController(ResetPasswordService resetPasswordService, ResetPasswordTokenRepository resetPasswordTokenRepository) {
        this.resetPasswordService = resetPasswordService;
        this.resetPasswordTokenRepository = resetPasswordTokenRepository;
    }


    @GetMapping("/forgot-password")
    public String showForgotPasswordPage() {
        return "authPages/login/forgotPassword";
    }

    @PostMapping("/forgot-password")
    public ModelAndView forgotPassword(@RequestParam String email) {
        ModelAndView modelAndView = new ModelAndView();

        try {
            this.resetPasswordService.forgotPassword(email);
            modelAndView.addObject("message", "Link do resetowania hasła został wysłany na Twój adres email.");
            modelAndView.addObject("email", email);
            modelAndView.setViewName("authPages/login/forgotPassword");
        } catch (EmailNotFoundException e) {
            modelAndView.addObject("errorEmail", e.getMessage());
            modelAndView.setViewName("authPages/login/forgotPassword");
        }

        return modelAndView;
    }


    @GetMapping("/reset-password")
    public ModelAndView showResetPasswordPage(@RequestParam("token") String token) {
        ModelAndView modelAndView = new ModelAndView();

        ResetPasswordToken resetPasswordToken = this.resetPasswordTokenRepository.findByToken(token);

        if (resetPasswordToken == null) {
            modelAndView.setViewName("errorTemplates/tokenError");
        } else {
            modelAndView.addObject("token", token);
            modelAndView.setViewName("authPages/login/resetPassword");
        }

        return modelAndView;
    }

    @PostMapping("/reset-password")
    public ModelAndView resetPassword(@RequestParam("token") String token, @RequestParam String newPassword, @RequestParam String confirmPassword) {
        ModelAndView modelAndView = new ModelAndView();

        try {
            this.resetPasswordService.resetPassword(token, newPassword, confirmPassword);
            modelAndView.addObject("messageSuccess", "Twoje hasło zostało pomyślnie zresetowane. Możesz teraz zalogować się używając nowego hasła.");
            modelAndView.setViewName("authPages/login/resetPassword");
        } catch (InvalidTokenException e) {
            modelAndView.setViewName("errorTemplates/tokenError");
        } catch (InvalidPasswordException e) {
            modelAndView.addObject("token", token);
            modelAndView.addObject("errorInvalidPassword", e.getMessage());
            modelAndView.setViewName("authPages/login/resetPassword");
        } catch (PasswordsMatchingException e) {
            modelAndView.addObject("token", token);
            modelAndView.addObject("errorPasswordsMatching", e.getMessage());
            modelAndView.setViewName("authPages/login/resetPassword");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return modelAndView;
    }

}
