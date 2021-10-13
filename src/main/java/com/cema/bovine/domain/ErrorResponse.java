package com.cema.bovine.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class ErrorResponse {

    private String message;
    private String details;
    private final List<Violation> violations = new ArrayList<>();


    @Data
    public static class Violation {

        private final String fieldName;
        private final String message;
    }
}
