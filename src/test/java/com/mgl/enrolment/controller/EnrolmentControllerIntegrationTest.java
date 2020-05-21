package com.mgl.enrolment.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.mgl.enrolment.EnrolmentApplication;
import com.mgl.enrolment.dto.CheckResultDTO;
import com.mgl.enrolment.dto.EnrolmentDTO;
import com.mgl.enrolment.utils.TestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = EnrolmentApplication.class
)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class EnrolmentControllerIntegrationTest {

    private static final String ENROLMENT_URL = "/api/enrolment";
    private static final String ENROLMENT_ID_URL = "/api/enrolment/{enrolmentId}";
    private static final String ENROLMENT_CHECK_URL = "/api/enrolment/{enrolmentId}/check";

    private static final String VALID_DATE_PATTERN = "dd.MM.yyyy";
    private static final String INVALID_DATE_PATTERN = "dd/MM/yyyy";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    private static final Configuration configuration = Configuration.builder()
            .jsonProvider(new JacksonJsonNodeJsonProvider())
            .mappingProvider(new JacksonMappingProvider())
            .build();

    @Test
    public void givenValidEnrolment_thenCreated() throws Exception {

        // Create enrolment
        EnrolmentDTO enrolment = TestUtils.buildEnrolmentValid();
        MvcResult mvcResult = mockMvc.perform(post(ENROLMENT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(enrolment)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is(EnrolmentDTO.StatusDTO.INITIALIZED.name())))
                .andExpect(jsonPath("$.identityDocument.documentId", is(enrolment.getIdentityDocument().getDocumentId())))
                .andReturn();

        Integer id = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.id");

        // Retrieve all
        mockMvc.perform(get(ENROLMENT_URL)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status", is(EnrolmentDTO.StatusDTO.INITIALIZED.name())))
                .andExpect(jsonPath("$[0].identityDocument.documentId", is(enrolment.getIdentityDocument().getDocumentId())));

        // Retrieve single
        mockMvc.perform(get(ENROLMENT_ID_URL, id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(EnrolmentDTO.StatusDTO.INITIALIZED.name())))
                .andExpect(jsonPath("$.identityDocument.documentId", is(enrolment.getIdentityDocument().getDocumentId())));
    }

    @Test
    public void givenInvalidEnrolment_thenNotCreated() throws Exception {

        // Attempt to create enrolment with invalid date format
        EnrolmentDTO enrolment = TestUtils.buildEnrolmentValid();
        String validEnrolment = objectMapper.writeValueAsString(enrolment);
        String wrongFormatDate = LocalDate.now().minusYears(1).format(DateTimeFormatter.ofPattern(INVALID_DATE_PATTERN));
        JsonNode wrongFormatDateJson = JsonPath.using(configuration)
                .parse(validEnrolment)
                .set("$.identityDocument.expirationDate", wrongFormatDate).json();

        mockMvc.perform(post(ENROLMENT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(wrongFormatDateJson.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.faults[0].fieldName", is("identityDocument.expirationDate")))
                .andExpect(jsonPath("$.faults[0].message", startsWith("Invalid format for value:")));

        // attempt with missing required param
        JsonNode missingIdJson = JsonPath.using(configuration)
                .parse(validEnrolment)
                .set("$.identityDocument.documentId", null).json();
        mockMvc.perform(post(ENROLMENT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(missingIdJson.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.faults[0].fieldName", is("identityDocument.documentId")))
                .andExpect(jsonPath("$.faults[0].message", is("Document id is mandatory")));
    }

    @Test
    public void givenEnrolment_thenCheckResultAsExpected() throws Exception {
        // create enrolment with check result expected ok
        EnrolmentDTO enrolment = TestUtils.buildEnrolmentValid();
        String allCheckValidID = "CJ-111007";
        enrolment.getIdentityDocument().setDocumentId(allCheckValidID);
        MvcResult mvcResult = mockMvc.perform(post(ENROLMENT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(enrolment)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is(EnrolmentDTO.StatusDTO.INITIALIZED.name())))
                .andExpect(jsonPath("$.identityDocument.documentId", is(enrolment.getIdentityDocument().getDocumentId())))
                .andReturn();

        Integer validEnrolmentId = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.id");

        // trigger check
        mockMvc.perform(post(ENROLMENT_CHECK_URL, validEnrolmentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.validIdDocument", is(Boolean.TRUE)))
                .andExpect(jsonPath("$.creditScore", is(7)))
                .andExpect(jsonPath("$.creditRisk", is(CheckResultDTO.CreditRisk.NO_RISK.name())))
                .andExpect(jsonPath("$.existingClient", is(Boolean.FALSE)));

        // create enrolment with check result expected to all fail
        EnrolmentDTO enrolmentFail = TestUtils.allChecksInvalid();
        String allCheckFailId = "CJ-111198";
        enrolmentFail.getIdentityDocument().setDocumentId(allCheckFailId);
        MvcResult mvcResultFail = mockMvc.perform(post(ENROLMENT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(enrolmentFail)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is(EnrolmentDTO.StatusDTO.INITIALIZED.name())))
                .andExpect(jsonPath("$.identityDocument.documentId", is(enrolmentFail.getIdentityDocument().getDocumentId())))
                .andReturn();

        Integer invalidEnrolmentId = JsonPath.read(mvcResultFail.getResponse().getContentAsString(), "$.id");

        // trigger check
        mockMvc.perform(post(ENROLMENT_CHECK_URL, invalidEnrolmentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.validIdDocument", is(Boolean.FALSE)))
                .andExpect(jsonPath("$.creditScore", is(198)))
                .andExpect(jsonPath("$.creditRisk", is(CheckResultDTO.CreditRisk.HIGH_RISK.name())))
                .andExpect(jsonPath("$.existingClient", is(Boolean.TRUE)));

    }
}
