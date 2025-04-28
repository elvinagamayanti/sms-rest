/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sms.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
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
@Table(name = "tahap_8")
public class Tahap8 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_kegiatan", nullable = false)
    private Kegiatan kegiatan;

    private boolean subtahap_1;
    private boolean subtahap_2;
    private boolean subtahap_3;
    private boolean subtahap_4;

    private String uploadFileName;
    private String uploadFilePath;

    @Column(name = "upload_timestamp")
    private LocalDateTime uploadTimestamp;

    public int getCompletionPercentage() {
        int completed = 0;
        int total = 4;

        if (subtahap_1)
            completed++;
        if (subtahap_2)
            completed++;
        if (subtahap_3)
            completed++;
        if (subtahap_4)
            completed++;

        return (completed * 100) / total;
    }
}