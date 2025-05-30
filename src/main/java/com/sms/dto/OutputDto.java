/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sms.dto;

import com.sms.entity.Program;

import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class OutputDto {
    private Long id;
    @NotEmpty(message = "Kode tidak boleh kosong")
    private String code;
    @NotEmpty(message = "Nama Output tidak boleh kosong")
    private String name;
    @NotEmpty(message = "Tahun tidak boleh kosong")
    private String year;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "program_id")
    private Program program;
}
