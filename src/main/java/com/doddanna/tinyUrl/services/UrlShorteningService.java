package com.doddanna.tinyUrl.services;
import com.doddanna.tinyUrl.models.UrlMapping;
import com.doddanna.tinyUrl.repositories.UrlMappingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UrlShorteningService {

    @Autowired
    private UrlMappingRepository urlMappingRepository;

    @Autowired
    private TokenGenerationService tokenGenerationService;

    @Transactional
    public String shortenUrl(String originalUrl, String orgId) throws Exception {
        // Generate a unique token
        String token = tokenGenerationService.getNewToken(orgId);

        // Save the mapping
        UrlMapping urlMapping = new UrlMapping(token, originalUrl, orgId);
        urlMappingRepository.save(urlMapping);

        return token;
    }

    public Optional<UrlMapping> getOriginalUrl(String token) {
        return urlMappingRepository.findByToken(token);
    }
}
