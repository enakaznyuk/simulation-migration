package com.example.migration.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class HeadersConfig {

    @Value("${headers.customHeader}")
    private String customHeader;

    @Value("${headers.anotherHeader}")
    private String anotherHeader;

    public Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Custom-Header", customHeader);
        headers.put("X-Another-Header", anotherHeader);
        return headers;
    }
}
