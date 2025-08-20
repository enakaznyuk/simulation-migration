package com.example.migration.dto;

import lombok.Data;

@Data
public class NotesPostRequest {

    private String agency;

    private String dateFrom;
    private String dateTo;

    private String clientGuid;
}
