package com.sms.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity for Direktorat
 * 
 * @author pinaa
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "direktorats")
public class Direktorat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deputi_id", referencedColumnName = "id", nullable = false)
    private Deputi deputi;

    @OneToMany(mappedBy = "direktorat", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<User> listUsers = new ArrayList<>();

    @Override
    public String toString() {
        return "[" + code + "] " + name;
    }

    public String getDeputiName() {
        return deputi != null ? deputi.getName() : "";
    }
}