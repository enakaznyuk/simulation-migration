package com.example.simulation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RequestBodyDto {

    private String agency;

    private String dateFrom;
    private String dateTo;

    private String clientGuid;
}
