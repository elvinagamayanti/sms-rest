/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sms.dto;

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
public class ProvinceDto {
    private Long id;
    @NotEmpty(message = "Provinsi tidak boleh kosong")
    private String name;
    @NotEmpty(message = "Kode tidak boleh kosong")
    private String code;
}
