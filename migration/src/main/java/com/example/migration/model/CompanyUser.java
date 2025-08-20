package com.example.migration.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "company_user")
@Data
public class CompanyUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String login;
}
