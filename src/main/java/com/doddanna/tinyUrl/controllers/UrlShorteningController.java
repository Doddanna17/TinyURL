package com.doddanna.tinyUrl.controllers;
import com.doddanna.tinyUrl.models.UrlMapping;
import com.doddanna.tinyUrl.services.UrlShorteningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UrlShorteningController {

    @Autowired
    private UrlShorteningService urlShorteningService;

    @PostMapping("/shorten")
    public String shortenUrl(@RequestParam String url, @RequestParam String orgId) throws Exception {
        return urlShorteningService.shortenUrl(url, orgId);
    }

    @GetMapping("/{token}")
    public Optional<UrlMapping> getOriginalUrl(@PathVariable String token) {
        return urlShorteningService.getOriginalUrl(token);
    }
}
