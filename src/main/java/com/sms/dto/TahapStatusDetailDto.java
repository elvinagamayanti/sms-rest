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
 * Enhanced TahapStatusDto dengan detail tanggal
 * 
 * @author pinaa
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TahapStatusDetailDto {
    private Long kegiatanId;
    private List<TahapDetailDto> tahaps;
    private int overallCompletionPercentage;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate proyekMulai; // tanggal mulai keseluruhan proyek

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate proyekSelesai; // estimasi selesai keseluruhan proyek

    private String proyekStatus; // "ON_TRACK", "BEHIND_SCHEDULE", "AHEAD_OF_SCHEDULE"
}