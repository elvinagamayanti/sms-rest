package com.sms.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimpleSatkerDto {
    private Long id;
    private String name;
    private String code;
    private String address;
    private String number;
    private String email;
    private Boolean isProvince;
    private Date createdOn;
    private Date updatedOn;

    // Simple reference fields
    private String provinceCode;
    private String provinceName;
}
