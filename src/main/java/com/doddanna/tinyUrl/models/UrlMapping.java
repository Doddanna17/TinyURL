package com.doddanna.tinyUrl.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "url_mappings",indexes = {
    @Index(columnList = "token",unique = true),
})
public class UrlMapping {

    private String token;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long mappingId;

    private String originalUrl;

    private String orgId;

    public UrlMapping() {
    }

    public UrlMapping(String token, String originalUrl, String orgId) {
        this.token = token;
        this.originalUrl = originalUrl;
        this.orgId = orgId;
    }

    // Getters and Setters
}
