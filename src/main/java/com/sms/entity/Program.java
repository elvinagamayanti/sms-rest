/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sms.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
@Table(name = "programs")
// @JsonIgnoreProperties({ "listKegiatans" })
public class Program {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String year;

    @OneToMany(mappedBy = "program", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Kegiatan> listKegiatans = new ArrayList<>();

    @OneToMany(mappedBy = "program", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Output> listOutputs = new ArrayList<>();

    @Override
    public String toString() {
        return "[" + code + "] " + name;
    }
}