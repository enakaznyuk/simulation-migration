package com.example.migration.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotesPostResponse {

    private String comments;
    private String guid;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime modifiedDateTime;

    private String clientGuid;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private ZonedDateTime datetime;

    private String loggedUser;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime createdDateTime;
}
