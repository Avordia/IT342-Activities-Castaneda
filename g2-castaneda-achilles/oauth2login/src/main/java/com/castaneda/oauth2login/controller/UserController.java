package com.castaneda.oauth2login.controller;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.castaneda.oauth2login.service.GoogleContactsService;
import com.google.api.services.people.v1.model.Person;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class UserController {

    private final GoogleContactsService googleContactsService;

    public UserController(GoogleContactsService googleContactsService) {
        this.googleContactsService = googleContactsService;
    }
    
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/")
    public String index(@RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient authorizedClient,
                        @AuthenticationPrincipal OAuth2User oauth2User,
                        Model model) throws GeneralSecurityException, IOException {
        if (oauth2User != null) {
            model.addAttribute("user", oauth2User.getAttributes());
        }

        java.util.List<Person> contacts = googleContactsService.getContacts(authorizedClient);
        model.addAttribute("contacts", contacts);

        return "index";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());
        return "redirect:/login"; 
    }


    // @GetMapping("/secured")
    // public String secured() {
    //     return "secured";
    // }

    @GetMapping("/user-info")
    public String getUserProfile(Model model, @AuthenticationPrincipal OAuth2User oAuth2User) {
        if (oAuth2User != null) {
            model.addAttribute("user", oAuth2User.getAttributes());
        }
        return "user-info";
    }
}