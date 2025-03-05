package com.castaneda.oauth2login.controller;

import com.castaneda.oauth2login.service.GoogleContactsService;
import com.google.api.services.people.v1.model.Person;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@Controller
@RequestMapping("/contacts")
public class ContactsController {

    private final GoogleContactsService googleContactsService;

    public ContactsController(GoogleContactsService googleContactsService) {
        this.googleContactsService = googleContactsService;
    }

    @GetMapping
    public String getContacts(@RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient authorizedClient,
                             @AuthenticationPrincipal OAuth2User oauth2User,
                             Model model) throws GeneralSecurityException, IOException {
        List<Person> contacts = googleContactsService.getContacts(authorizedClient);
        model.addAttribute("contacts", contacts);
        model.addAttribute("user", oauth2User.getAttributes());
        return "contacts";
    }

    @PostMapping("/create")
    public String createContact(@RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient authorizedClient,
                               @ModelAttribute Person contact) throws GeneralSecurityException, IOException {
        googleContactsService.createContact(authorizedClient, contact);
        return "redirect:/contacts";
    }

    @PostMapping("/update/{resourceName}")
    public String updateContact(@RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient authorizedClient,
                               @PathVariable String resourceName,
                               @ModelAttribute Person contact) throws GeneralSecurityException, IOException {
        googleContactsService.updateContact(authorizedClient, resourceName, contact);
        return "redirect:/contacts";
    }

    @PostMapping("/delete/{resourceName}")
    public String deleteContact(@RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient authorizedClient,
                               @PathVariable String resourceName) throws GeneralSecurityException, IOException {
        googleContactsService.deleteContact(authorizedClient, resourceName);
        return "redirect:/contacts";
    }
}