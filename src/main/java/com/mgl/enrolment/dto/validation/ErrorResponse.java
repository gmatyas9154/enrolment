package com.mgl.enrolment.dto.validation;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    @Singular
    private List<Fault> faults;

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Fault {
        @Getter
        private String fieldName;
        @Getter
        private  String message;
    }
}


