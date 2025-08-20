package com.example.migration.model;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "patient_note")
@Data
public class PatientNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="created_date_time", nullable=false)
    private LocalDateTime createdDateTime;

    @Column(name="last_modified_date_time", nullable=false)
    private LocalDateTime lastModifiedDateTime;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="created_by_user_id")
    private CompanyUser createdByUser;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="last_modified_by_user_id")
    private CompanyUser lastModifiedByUser;

    @Column(length=4000)
    private String note;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="patient_id", nullable=false)
    private PatientProfile patient;

    @Column(name = "note_guid", unique = true)
    private String noteGuid;
}
