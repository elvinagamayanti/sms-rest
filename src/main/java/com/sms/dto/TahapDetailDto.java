/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sms.dto;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO untuk detail tahap lengkap dengan semua subtahap
 * 
 * @author pinaa
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TahapDetailDto {
    private int tahapNumber;
    private int completionPercentage;
    private List<SubtahapDetailDto> subtahaps;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalMulaiTercepat; // tanggal perencanaan subtahap pertama

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalSelesaiTerakhir; // tanggal realisasi subtahap terakhir

    private String overallStatus; // status keseluruhan tahap
}