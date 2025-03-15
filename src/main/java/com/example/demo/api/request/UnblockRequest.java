package com.example.demo.api.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UnblockRequest {

    @NotBlank
    private String username;
}
