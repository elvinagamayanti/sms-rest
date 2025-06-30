package com.sms.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Deputi
 * 
 * @author pinaa
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeputiDto {
    private Long id;

    @NotEmpty(message = "Kode deputi tidak boleh kosong")
    private String code;

    @NotEmpty(message = "Nama deputi tidak boleh kosong")
    private String name;

    @Override
    public String toString() {
        return "[" + code + "] " + name;
    }
}