package com.sms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimpleUserDto {
    private Long id;
    private String name;
    private String nip;
    private String email;
    private Boolean isActive;

    // Simple reference fields - hanya ID dan nama
    private Long satkerId;
    private String satkerName;
    private Long direktoratId;
    private String direktoratName;
    private String deputiName;
}