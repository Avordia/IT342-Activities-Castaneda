package com.castaneda.oauth2login.controller;

import com.castaneda.oauth2login.service.GoogleContactsService;
import com.google.api.services.people.v1.model.Person;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
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
                             Model model) throws GeneralSecurityException, IOException {
        List<Person> contacts = googleContactsService.getContacts(authorizedClient);
        model.addAttribute("contacts", contacts);
        return "index";
    }

    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<Person> createContact(
            @RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient authorizedClient,
            @RequestBody Person contact) throws GeneralSecurityException, IOException {
        Person createdContact = googleContactsService.createContact(authorizedClient, contact);
        return ResponseEntity.ok(createdContact);
    }

    @PutMapping("/{resourceName}")
    @ResponseBody
    public ResponseEntity<?> updateContact(
            @RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient authorizedClient,
            @PathVariable String resourceName,
            @RequestBody Person contact) throws GeneralSecurityException, IOException {
        if (!resourceName.startsWith("people/")) {
            resourceName = "people/" + resourceName;
        }
        contact.setResourceName(resourceName);
        Person updatedContact = googleContactsService.updateContact(authorizedClient, resourceName, contact);
        return ResponseEntity.ok(updatedContact);
    }

    @DeleteMapping("/{resourceName}")
    @ResponseBody
    public ResponseEntity<Void> deleteContact(
            @RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient authorizedClient,
            @PathVariable String resourceName) throws GeneralSecurityException, IOException {
        if (!resourceName.startsWith("people/")) {
            resourceName = "people/" + resourceName;
        }
        googleContactsService.deleteContact(authorizedClient, resourceName);
        return ResponseEntity.ok().build();
    }
}