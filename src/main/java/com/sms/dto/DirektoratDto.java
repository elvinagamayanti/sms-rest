package com.sms.dto;

import com.sms.entity.Deputi;

import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Direktorat
 * 
 * @author pinaa
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DirektoratDto {
    private Long id;

    @NotEmpty(message = "Kode direktorat tidak boleh kosong")
    private String code;

    @NotEmpty(message = "Nama direktorat tidak boleh kosong")
    private String name;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "deputi_id")
    private Deputi deputi;

    @Override
    public String toString() {
        return "[" + code + "] " + name;
    }

    public String getDeputiName() {
        return deputi != null ? deputi.getName() : "";
    }
}