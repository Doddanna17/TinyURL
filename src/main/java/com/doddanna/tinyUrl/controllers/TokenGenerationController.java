package com.doddanna.tinyUrl.controllers;

import com.doddanna.tinyUrl.services.TokenGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tokens")
public class TokenGenerationController {

    private final TokenGenerationService tokenGenerationService;

    @Autowired
    public TokenGenerationController(TokenGenerationService tokenGenerationService) {
        this.tokenGenerationService = tokenGenerationService;
    }

    @PostMapping("/allocate")
    public String allocateToken(@RequestParam String orgId) {
        try {
            return tokenGenerationService.getNewToken(orgId);
        } catch (Exception e) {
            throw new RuntimeException("Error allocating token", e);
        }
    }
}
