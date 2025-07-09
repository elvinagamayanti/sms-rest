/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sms.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO untuk detail subtahap dengan tanggal perencanaan dan realisasi
 * 
 * @author pinaa
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubtahapDetailDto {
    private int subtahapNumber;
    private boolean completed;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalPerencanaan;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalRealisasi;

    private String status; // "BELUM_MULAI", "SEDANG_BERJALAN", "SELESAI", "TERLAMBAT"
    private Integer selisihHari; // selisih antara tanggal perencanaan dan realisasi
}