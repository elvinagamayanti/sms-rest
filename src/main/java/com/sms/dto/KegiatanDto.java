/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sms.dto;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.sms.entity.Program;
import com.sms.entity.Satker;
import com.sms.entity.User;

import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author pinaa
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KegiatanDto {
    private Long id;

    @NotEmpty(message = "Nama Kegiatan tidak boleh kosong")
    private String name;

    @NotEmpty(message = "Kode Kegiatan tidak boleh kosong")
    private String code;

    @DecimalMin(value = "0.0", inclusive = false, message = "Besar anggaran harus lebih besar dari 0.")
    private BigDecimal budget;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) // Relasi ke User
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "satker_id", nullable = false) // Relasi ke Satker
    private Satker satker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id", nullable = false) // Relasi ke Program
    private Program program;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date endDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date createdOn;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date updatedOn;

    public String getNamaSatker() {
        return "Badan Pusat Statistik " + satker.getName();
    }

    public String getNamaUser() {
        return user.getName();
    }

}
