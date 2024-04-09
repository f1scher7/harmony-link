package com.harmonylink.harmonylink.controllers;

import com.harmonylink.harmonylink.models.user.UserAccount;
import com.harmonylink.harmonylink.services.user.useraccount.RegistrationService;
import com.harmonylink.harmonylink.services.user.useraccount.exceptions.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequestMapping("/auth")
public class RegistrationController {

    private final RegistrationService registrationService;
    private final AuthenticationSuccessHandler successHandler;


    @Autowired
    public RegistrationController(RegistrationService registrationService, AuthenticationSuccessHandler successHandler) {
        this.registrationService = registrationService;
        this.successHandler = successHandler;
    }


    @GetMapping("/registration")
    public String showRegistrationPage() {
        return "authPages/registration/registration";
    }

    @PostMapping("/registration")
    public ModelAndView initiateNewUserRegistration(@ModelAttribute UserAccount userAccount, RedirectAttributes redirectAttributes, HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        ModelAndView modelAndView = new ModelAndView();

        String email = "";
        String login = "";
        String password = "";

        if (userAccount != null) {
            modelAndView.addObject("userData", userAccount);
            login = userAccount.getLogin();
            email = userAccount.getEmail();
            password = userAccount.getPassword();
        }

        try {
            this.registrationService.registerNewUserAccount(userAccount);
            this.registrationService.autoLogin(login, password);

            redirectAttributes.addFlashAttribute("email", "Sprawdź maila (" + email + ") w celu weryfikacji konta.");
            modelAndView.setViewName("redirect:/auth/confirm-email");
        } catch (UserAlreadyExistsException | InvalidLoginException e) {
            modelAndView.setViewName("authPages/registration/registration");
            modelAndView.addObject("errorLogin", e.getMessage());
        } catch (InvalidPasswordException e) {
            modelAndView.setViewName("authPages/registration/registration");
            modelAndView.addObject("errorPassword", e.getMessage());
        } catch (EmailAlreadyExistsException e) {
            modelAndView.setViewName("authPages/registration/registration");
            modelAndView.addObject("errorEmail", e.getMessage());
        } catch (EmailNotFoundException e) {
            modelAndView.setViewName("authPages/registration/registration");
            modelAndView.addObject("errorNotCorrectEmail", e.getMessage());
        } catch (UserTooYoungException e) {
            modelAndView.setViewName("authPages/registration/registration");
            modelAndView.addObject("errorUserAge", e.getMessage());
        }

        return modelAndView;
    }


    @GetMapping("/confirm-email")
    public ModelAndView showConfirmEmailPage(@ModelAttribute("email") String email) {
        ModelAndView modelAndView = new ModelAndView("authPages/registration/confirmEmail");
        modelAndView.addObject("confirmEmailInfo", "Sprawdź maila (" + email + ") w celu weryfikacji konta");

        return modelAndView;
    }


    @GetMapping("/activate-account")
    public String showActivateAccountPage(@RequestParam("token") String token) {
        try {
            this.registrationService.verifyNewUserAccount(token);
            return "authPages/registration/activateAccount";
        } catch (Exception e) {
            return "errorTemplates/tokenError";
        }
    }

}