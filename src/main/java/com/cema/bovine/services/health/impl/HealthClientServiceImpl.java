package com.cema.bovine.services.health.impl;

import com.cema.bovine.domain.ErrorResponse;
import com.cema.bovine.domain.health.Illness;
import com.cema.bovine.exceptions.ValidationException;
import com.cema.bovine.services.authorization.AuthorizationService;
import com.cema.bovine.services.health.HealthClientService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

@Service
public class HealthClientServiceImpl implements HealthClientService {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String PATH_VALIDATE_HEALTH = "illness/bovine/{tag}";

    private final RestTemplate restTemplate;
    private final String url;
    private final AuthorizationService authorizationService;
    private final ObjectMapper mapper = new ObjectMapper();

    public HealthClientServiceImpl(RestTemplate restTemplate, @Value("${back-end.health.url}") String url, AuthorizationService authorizationService) {
        this.restTemplate = restTemplate;
        this.url = url;
        this.authorizationService = authorizationService;
    }

    @SneakyThrows
    @Override
    public Illness getBovineIllness(String tag) {
        String authToken = authorizationService.getUserAuthToken();
        String searchUrl = url + PATH_VALIDATE_HEALTH;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(AUTHORIZATION_HEADER, authToken);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity entity = new HttpEntity("{}", httpHeaders);
        try {
            ResponseEntity<Illness> responseEntity = restTemplate.exchange(searchUrl, HttpMethod.GET, entity, Illness.class, tag);
            return responseEntity.getBody();
        } catch (RestClientResponseException httpClientErrorException) {
            if(httpClientErrorException.getRawStatusCode() == 404){
                return null;
            }
            String response = httpClientErrorException.getResponseBodyAsString();
            ErrorResponse errorResponse = mapper.readValue(response, ErrorResponse.class);
            throw new ValidationException(errorResponse.getMessage(), httpClientErrorException);
        }
    }
}
