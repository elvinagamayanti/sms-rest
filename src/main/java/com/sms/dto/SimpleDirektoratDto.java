package com.sms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimpleDirektoratDto {
    private Long id;
    private String name;
    private String code;

    // Simple reference fields
    private Long deputiId;
    private String deputiName;
    private String deputiCode;
}