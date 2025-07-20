package com.sms.entity;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity untuk Tahap 3 dengan tanggal perencanaan dan realisasi per subtahap
 * 
 * @author pinaa
 */

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@JsonIgnoreProperties({ "kegiatan" })
@Table(name = "tahap_3")
public class Tahap3 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_kegiatan", nullable = false)
    private Kegiatan kegiatan;

    // Subtahap 1
    private boolean subtahap_1;
    private LocalDate subtahap_1_tanggal_perencanaan;
    private LocalDate subtahap_1_tanggal_realisasi;

    // Subtahap 2
    private boolean subtahap_2;
    private LocalDate subtahap_2_tanggal_perencanaan;
    private LocalDate subtahap_2_tanggal_realisasi;

    // Subtahap 3
    private boolean subtahap_3;
    private LocalDate subtahap_3_tanggal_perencanaan;
    private LocalDate subtahap_3_tanggal_realisasi;

    // Subtahap 4
    private boolean subtahap_4;
    private LocalDate subtahap_4_tanggal_perencanaan;
    private LocalDate subtahap_4_tanggal_realisasi;

    // Subtahap 5
    private boolean subtahap_5;
    private LocalDate subtahap_5_tanggal_perencanaan;
    private LocalDate subtahap_5_tanggal_realisasi;

    // Subtahap 6
    private boolean subtahap_6;
    private LocalDate subtahap_6_tanggal_perencanaan;
    private LocalDate subtahap_6_tanggal_realisasi;

    // Subtahap 7
    private boolean subtahap_7;
    private LocalDate subtahap_7_tanggal_perencanaan;
    private LocalDate subtahap_7_tanggal_realisasi;

    public int getCompletionPercentage() {
        int completed = 0;
        int total = 7;

        if (subtahap_1)
            completed++;
        if (subtahap_2)
            completed++;
        if (subtahap_3)
            completed++;
        if (subtahap_4)
            completed++;
        if (subtahap_5)
            completed++;
        if (subtahap_6)
            completed++;
        if (subtahap_7)
            completed++;

        return (completed * 100) / total;
    }
}