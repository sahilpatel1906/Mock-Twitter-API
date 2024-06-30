package com.cooksys.team2socialmedia.controllers;

import com.cooksys.team2socialmedia.services.ValidateService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/validate")
public class ValidateController {

    private final ValidateService validateService;

    @GetMapping("/tag/exists/{label}")
    public Boolean labelExists(@PathVariable String label) {
        return validateService.checkLabelExists(label);
    }

    @GetMapping("username/exists/@{username}")
    public Boolean usernameExists(@PathVariable String username) {
        return validateService.checkUsernameExists(username);
    }

    @GetMapping("username/available/@{username}")
    public Boolean usernameAvailable(@PathVariable String username) {
        return validateService.checkUsernameAvailable(username);
    }
}
