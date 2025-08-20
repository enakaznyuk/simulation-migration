package com.example.migration.client;

import com.example.migration.configs.HeadersConfig;
import com.example.migration.dto.ClientsPost;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FirstRequestClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final HeadersConfig headersConfig;

    @Autowired
    private final ObjectMapper mapper;

    @Value("${api.urlWithoutBody}")
    private String urlWithoutBody;

    @Value("${api.urlWithBody}")
    private String urlWithBody;

    public List<ClientsPost> getAllClients() throws JsonProcessingException {

        HttpHeaders headers = new HttpHeaders();
        headers.setAll(headersConfig.getHeaders());

        ResponseEntity<String> response = restTemplate.exchange(
                urlWithoutBody,
                HttpMethod.POST,
                new HttpEntity<>(headers),
                String.class
        );

        String jsonResponse = response.getBody();
        return mapper.readValue(jsonResponse, new TypeReference<>() {});
    }
}
