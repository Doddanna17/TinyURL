package com.doddanna.tinyUrl.controllers;

import com.doddanna.tinyUrl.models.UrlMapping;
import com.doddanna.tinyUrl.services.UrlShorteningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.net.URI;
import java.util.Optional;

@Controller
public class TInyUrlController {
    @Autowired
    private UrlShorteningService urlShorteningService;

    @RequestMapping(method = RequestMethod.GET,path = "/{token}")
    public ResponseEntity<Void> redirect(@PathVariable("token") String token) {
        Optional<UrlMapping> originalUrl = urlShorteningService.getOriginalUrl(token);
        HttpStatus movedPermanently = HttpStatus.TEMPORARY_REDIRECT;
        HttpHeaders headers = new HttpHeaders();
        if(originalUrl.isEmpty()){
            movedPermanently=HttpStatus.BAD_REQUEST;
        }else{
            headers.setLocation(URI.create(originalUrl.get().getOriginalUrl()));
        }
        return new ResponseEntity<>(headers, movedPermanently);
    }
}
