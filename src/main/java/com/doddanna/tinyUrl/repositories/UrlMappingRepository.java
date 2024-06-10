package com.doddanna.tinyUrl.repositories;

import com.doddanna.tinyUrl.models.UrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlMappingRepository extends JpaRepository<UrlMapping, String> {
    public Optional<UrlMapping> findByToken(String token);
}
