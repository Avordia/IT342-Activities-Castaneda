package com.castaneda.oauth2login.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    @GetMapping("/")
    public String index() {
        return "index";
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