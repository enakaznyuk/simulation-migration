package com.example.migration.client;

import com.example.migration.configs.HeadersConfig;
import com.example.migration.dto.NotesPostRequest;
import com.example.migration.dto.NotesPostResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SecondRequestClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final HeadersConfig headersConfig;

    @Autowired
    private final ObjectMapper mapper;

    @Value("${api.urlWithoutBody}")
    private String urlWithoutBody;

    @Value("${api.urlWithBody}")
    private String urlWithBody;

    public List<NotesPostResponse> sendRequestWithBody(NotesPostRequest guids) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setAll(headersConfig.getHeaders());
        headers.setContentType(MediaType.APPLICATION_JSON);

        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        guids.setDateFrom(yesterday.format(formatter));
        guids.setDateTo(today.format(formatter));

        HttpEntity<NotesPostRequest> requestEntity = new HttpEntity<>(guids, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                urlWithBody,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        String jsonResponse = response.getBody();
        return mapper.readValue(jsonResponse, new TypeReference<>() {});
    }
}
