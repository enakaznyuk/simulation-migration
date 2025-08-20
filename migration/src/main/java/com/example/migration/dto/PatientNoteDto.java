package com.example.migration.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PatientNoteDto {

    private LocalDateTime createdDateTime;
    private LocalDateTime lastModifiedDateTime;
    private Long createdByUserId;
    private Long lastModifiedByUserId;
    private String note;
    private Long patientId;
}
