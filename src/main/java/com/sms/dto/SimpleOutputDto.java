package com.sms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimpleOutputDto {
    private Long id;
    private String name;
    private String code;
    private String year;

    // Simple reference fields
    private Long programId;
    private String programName;
    private String programCode;
}