package com.sms.dto;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO untuk progress report
 * 
 * @author pinaa
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressReportDto {
    private Long kegiatanId;
    private int totalSubtahap;
    private int subtahapSelesai;
    private int subtahapTerlambat;
    private double percentageComplete;
    private List<String> delayedItems;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate estimatedCompletion;

    private String overallStatus;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate reportGeneratedDate;

    private String summary; // ringkasan status proyek
}