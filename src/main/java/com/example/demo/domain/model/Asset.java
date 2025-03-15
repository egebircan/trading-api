package com.example.demo.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Data;

@Data
@Entity
@Table(name = "ASSETS")
public class Asset {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long customerId;

    private String symbol;

    private BigDecimal size;

    private BigDecimal usableSize;
}
