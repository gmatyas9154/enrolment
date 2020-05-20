package com.mgl.enrolment.faults;

import com.fasterxml.jackson.annotation.JsonInclude;
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
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Fault {
        @Getter
        private String fieldName;
        @Getter
        private  String message;
    }
}


