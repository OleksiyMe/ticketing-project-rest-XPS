package com.cydeo.controller;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.dto.RoleDTO;
import com.cydeo.dto.TestResponseDTO;
import com.cydeo.dto.UserDTO;
import com.cydeo.enums.Gender;
import com.cydeo.enums.Status;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class ProjectControllerTest {

    @Autowired
    private MockMvc mvc;
    static UserDTO manager;
    static ProjectDTO project;
    static String token;

    @BeforeAll   //creating sample data
    static void setUp() {
//        token = "Bearer " + "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJKZlNWW" +
//                "G15MFJ6dW5MdzFwcGc1N3pYdWxTQzM2T1VNeFNDLTl6TW9HejEwIn0.eyJleHAiOjE2Nz" +
//                "E0MDk2MzUsImlhdCI6MTY3MTM5MTYzNSwianRpIjoiMjIwYWU1ZjQtOGVlZi00ZTEzLWFj" +
//                "NDYtOTQ0NmRjMzQ5OTliIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL2F1dGgvcmVh" +
//                "bG1zL2N5ZGVvLWRldiIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiJmMDkzYTdmOS0wNzcxLTQz" +
//                "MTAtOGZiNS1kOTAzMDY2N2E3MTciLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJ0aWNrZXRpbmctY" +
//                "XBwIiwic2Vzc2lvbl9zdGF0ZSI6ImRlZTdlNjc4LTcwZDAtNDljMy1hZjZkLTQ0MDE5N2FiNzd" +
//                "jMiIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiaHR0cDovL2xvY2FsaG9zdDo4MDgxIl" +
//                "0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3Jpem" +
//                "F0aW9uIiwiZGVmYXVsdC1yb2xlcy1teS1kZXYtcmVyYWxtIl19LCJyZXNvdXJjZV9hY2Nlc3MiOn" +
//                "sidGlja2V0aW5nLWFwcCI6eyJyb2xlcyI6WyJNYW5hZ2VyIl19LCJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6Im9wZW5pZCBwcm9maWxlIGVtYWlsIiwic2lkIjoiZGVlN2U2NzgtNzBkMC00OWMzLWFmNmQtNDQwMTk3YWI3N2MyIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsInByZWZlcnJlZF91c2VybmFtZSI6Im96enkifQ.qjrdP7P9g2WrQ1f1YMnVJ3aXL6OvOSj4IvZo-4Cxk9k4IxbkG9jaZzQHXySx0JSAJW5Sot4wWQDJs2Alq4911oj0RkP1q_hNeT5Lds14w0gpMXJ4DbjwtYL-zTNVPh1ZI33L-6Cfad7R0GVhqEptHv59LUfnm8qTeM5zR7azFbCO-oHiOvT7qe6oeI40UJvDQJZngtzDVFWPhG2FyZsUEgsVtBC017pLSVlNwjJIrOtxd_TYykJ7SDg0SvCZJRx5wcIhOts1SlUdiLbk9D1QaDIHAzGQ-J8L2solGfjIl4l8YsNT480VKJAKpuIUglrj8VOkBapGvVkemxeC3D8tIw";

        token = "Bearer " + getToken();

        manager = new UserDTO(2L,
                "",
                "",
                "ozzy",
                "abc1",
                "",
                true,
                "",
                new RoleDTO(2L, "Manager"),
                Gender.MALE);

        project = new ProjectDTO(
                "API Project",
                "PR001",
                manager,
                LocalDate.now(),
                LocalDate.now().plusDays(5),
                "Some details",
                Status.OPEN
        );
    }

    @Test
    void givenNoToken_getProjects() throws Exception {

        mvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/progect"))
                .andExpect(status().is4xxClientError()); //checking for 4xx error
    }

    @Test
    void givenToken_getProjects() throws Exception {

        mvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/project")
                                .header("Authorization", token)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                //We use jsonPath (https://github.com/json-path/JsonPath) to look into JSON
                .andExpect(jsonPath("$.data[0].projectCode").exists())
                .andExpect(jsonPath("$.data[0].assignedManager.userName").exists())
                .andExpect(jsonPath("$.data[0].assignedManager.userName").isNotEmpty())
                .andExpect(jsonPath("$.data[0].assignedManager.userName").isString())
                .andExpect(jsonPath("$.data[0].assignedManager.userName").value("ozzy"));
    }

    @Test
    void givenToken_createProject() throws Exception {

        mvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/project")
                                .header("Authorization", token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(toJsonString(project)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Project created"));


    }

    private String toJsonString(final Object obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        //do not use timestamp, dates only
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        // 2022,12,12 - usually. But we need 2022/12/12
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper.writeValueAsString(obj);
    }

    @Test
    void givenToken_updateProject() throws Exception {

        project.setProjectName("API Project2");
        mvc.perform(MockMvcRequestBuilders.put("/api/v1/project")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(toJsonString(project)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Project updated"));
    }

    @Test
    void givenToken_deleteProject() throws Exception {

        mvc.perform(MockMvcRequestBuilders.delete("/api/v1/project/" + project.getProjectCode())
                    .header("Authorization", token)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Project deleted"));
    }


    private static String getToken() {

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();

        map.add("grant_type", "password");
        map.add("client_id", "ticketing-app");
        map.add("client_secret", "Fe1he73gh1pqAAaiSKthSDMzYb8y96OS");
        map.add("username", "ozzy");
        map.add("password", "abc1");
        map.add("scope", "openid");

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

        ResponseEntity<TestResponseDTO> response =
                restTemplate.exchange("http://localhost:8080/auth/realms/cydeo-dev/protocol/openid-connect/token",
                        HttpMethod.POST,
                        entity,
                        TestResponseDTO.class);

        if (response.getBody() != null) {
            return response.getBody().getAccess_token();
        }

        return "";

    }

}
















