package com.sms.dto;

import java.math.BigDecimal;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimpleKegiatanDto {
    private Long id;
    private String name;
    private String code;
    private BigDecimal budget;
    private Date startDate;
    private Date endDate;
    private Date createdOn;
    private Date updatedOn;

    // Simple reference fields
    private Long userId;
    private String userName;
    private Long satkerId;
    private String satkerName;
    private Long programId;
    private String programName;
    private Long outputId;
    private String outputName;
    private Long direktoratPjId;
    private String direktoratPjName;
}