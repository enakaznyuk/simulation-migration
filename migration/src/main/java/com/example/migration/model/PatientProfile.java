package com.example.migration.model;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "patient_profile")
@Data
public class PatientProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="first_name", length=255)
    private String firstName;

    @Column(name="last_name", length=255)
    private String lastName;

    @Column(name="old_client_guid", length=255)
    private String oldClientGuid;

    @Column(name="status_id", nullable=false)
    private Short statusId;
}
