package com.example.migration.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class PatientProfileDto {

    @JsonIgnore
    private Long id;

    private String firstName;
    private String lastName;
    private String oldClientGuid;
    private Short statusId;
}
