package com.example.migration.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;


import java.time.LocalDateTime;

@Data
public class CustomRequest {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy")
    private LocalDateTime lastModifiedDateTime;
    private Long lastModifiedByUserId;
    private String oldClientGuid;
    private Long patientId;
    private Short statusId;
    private String noteGuid;

    public CustomRequest(LocalDateTime lastModifiedDateTime, Long lastModifiedByUserId, String noteGuid, Long patientId, String oldClientGuid, Short statusId) {
        this.lastModifiedDateTime = lastModifiedDateTime;
        this.lastModifiedByUserId = lastModifiedByUserId;
        this.oldClientGuid = oldClientGuid;
        this.patientId = patientId;
        this.statusId = statusId;
        this.noteGuid = noteGuid;
    }
}
