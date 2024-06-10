package com.doddanna.tinyUrl.repositories;

import com.doddanna.tinyUrl.models.TokenRange;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRangeRepository extends JpaRepository<TokenRange, Long> {
    TokenRange findTopByOrderByIdDesc();
}
