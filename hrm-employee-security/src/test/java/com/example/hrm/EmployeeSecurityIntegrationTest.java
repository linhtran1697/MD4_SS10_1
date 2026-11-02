package com.example.hrm;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class EmployeeSecurityIntegrationTest {

    private static final String ENDPOINT = "/api/v1/employees";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SecurityProperties securityProperties;

    @Test
    void requestWithoutAuthenticationReturns401AndNoEmployees() throws Exception {
        mockMvc.perform(get(ENDPOINT).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string("WWW-Authenticate", startsWith("Basic")))
                .andExpect(content().string(not(containsString("fullName"))))
                .andExpect(content().string(not(containsString("salary"))));
    }

    @Test
    void wrongPasswordReturns401AndNoEmployees() throws Exception {
        mockMvc.perform(get(ENDPOINT)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(httpBasic("user", "wrong-password-for-demo")))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(not(containsString("fullName"))))
                .andExpect(content().string(not(containsString("salary"))));
    }

    @Test
    void wrongUsernameReturns401() throws Exception {
        mockMvc.perform(get(ENDPOINT)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(httpBasic("unknown-user", securityProperties.getUser().getPassword())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void generatedDefaultCredentialsReturnExactlyThreeEmployees() throws Exception {
        mockMvc.perform(get(ENDPOINT)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(httpBasic("user", securityProperties.getUser().getPassword())))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].fullName").value("Nguyễn Văn An"))
                .andExpect(jsonPath("$[0].salary").value(18000000))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].fullName").value("Trần Thị Bình"))
                .andExpect(jsonPath("$[1].salary").value(22000000))
                .andExpect(jsonPath("$[2].id").value(3))
                .andExpect(jsonPath("$[2].fullName").value("Lê Văn Cường"))
                .andExpect(jsonPath("$[2].salary").value(25000000));
    }
}
