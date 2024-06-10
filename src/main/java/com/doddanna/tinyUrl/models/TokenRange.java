package com.doddanna.tinyUrl.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "token_ranges")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenRange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "org_id", nullable = false)
    private String orgId;

    @Column(name = "start_range", nullable = false)
    private int startRange;

    @Column(name = "end_range", nullable = false)
    private int endRange;

    @Column(name = "allocated_at", nullable = false)
    private LocalDateTime allocatedAt;

    public TokenRange(String orgId, int startRange, int endRange) {
        this.orgId = orgId;
        this.startRange = startRange;
        this.endRange = endRange;
        this.allocatedAt = LocalDateTime.now();
    }

    // Getters and Setters
}