/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sms.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

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
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users")
// @JsonIgnoreProperties({ "roles", "listKegiatans", "password" })
public class User {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String nip;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "users_roles", joinColumns = {
            @JoinColumn(name = "USER_ID", referencedColumnName = "ID") }, inverseJoinColumns = {
                    @JoinColumn(name = "ROLE_ID", referencedColumnName = "ID") })
    private List<Role> roles = new ArrayList<>();

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "satker_id")
    private Satker satker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "direktorat_id", referencedColumnName = "id", nullable = true)
    private Direktorat direktorat;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Kegiatan> listKegiatans = new ArrayList<>();

    public String getNamaSatker() {
        return "Badan Pusat Statistik " + satker.getName();
    }

    public String getNamaDirektorat() {
        return direktorat != null ? direktorat.getName() : "";
    }

    public String getNamaDeputi() {
        return direktorat != null && direktorat.getDeputi() != null ? direktorat.getDeputi().getName() : "";
    }

    public String getStatusText() {
        return isActive ? "Aktif" : "Non-Aktif";
    }

    // Method untuk mengaktifkan user
    public void activate() {
        this.isActive = true;
    }

    // Method untuk menonaktifkan user
    public void deactivate() {
        this.isActive = false;
    }

    @Override
    public String toString() {
        return name;
    }
}