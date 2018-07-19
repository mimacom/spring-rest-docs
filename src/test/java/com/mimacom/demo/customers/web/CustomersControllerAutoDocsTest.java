package com.mimacom.demo.customers.web;

import capital.scalable.restdocs.AutoDocumentation;
import capital.scalable.restdocs.jackson.JacksonResultHandlers;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mimacom.demo.customers.domain.Customer;
import com.mimacom.demo.customers.domain.Gender;
import com.mimacom.demo.customers.repository.CustomersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.cli.CliDocumentation;
import org.springframework.restdocs.http.HttpDocumentation;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({SpringExtension.class, RestDocumentationExtension.class})
@WebMvcTest(CustomersController.class)
@EnableSpringDataWebSupport
class CustomersControllerAutoDocsTest {

    @MockBean
    private CustomersRepository customersRepository;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WebApplicationContext context;
    
    private MockMvc mockMvc;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .alwaysDo(JacksonResultHandlers.prepareJackson(objectMapper))
                .apply(MockMvcRestDocumentation.documentationConfiguration(restDocumentation)
                        .uris()
                        .withScheme("http")
                        .withHost("demo.mimacom.com")
                        .withPort(443)
                        .and().snippets()
                        .withDefaults(CliDocumentation.curlRequest(),
                                HttpDocumentation.httpRequest(),
                                HttpDocumentation.httpResponse(),
                                AutoDocumentation.requestFields(),
                                AutoDocumentation.responseFields(),
                                AutoDocumentation.pathParameters(),
                                AutoDocumentation.requestParameters(),
                                AutoDocumentation.description(),
                                AutoDocumentation.methodAndPath(),
                                AutoDocumentation.section()))
                .alwaysDo(document("{class-name}/{method-name}",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())))
                .build();
    }

    @Test
    void createCustomer() throws Exception {

        Customer customer = new Customer();
        customer.setAge(23);
        customer.setFirstName("Liam");
        customer.setGender(Gender.MALE);
        customer.setPhoneNumber("1119992");

        when(this.customersRepository.save(any(Customer.class))).then((Answer<Customer>) invocationOnMock -> {
            if (invocationOnMock.getArguments().length > 0 && invocationOnMock.getArguments()[0] instanceof Customer) {
                Customer customer1 = (Customer) invocationOnMock.getArguments()[0];
                customer1.setId(34L);
                return customer1;
            }
            return null;
        });

        this.mockMvc.perform(RestDocumentationRequestBuilders.post("/customers/{salesRepId}", 330).content(this.objectMapper.writeValueAsString(customer))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Liam"))
                .andExpect(jsonPath("$.lastName").isEmpty())
                .andExpect(jsonPath("$.gender").value(Gender.MALE.name()))
                .andExpect(jsonPath("$.phoneNumber").value("1119992"))
                .andExpect(jsonPath("$.age").value(23))
                .andExpect(jsonPath("$.id").value(34));

    }

    @Test
    void testUpdate() throws Exception {

        Customer customer = new Customer();
        customer.setFirstName("Test customer");
        customer.setId(344L);
        customer.setAge(65);
        customer.setPhoneNumber("99882883");

        this.mockMvc.perform(put("/customers")
                .content(this.objectMapper.writeValueAsString(customer))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
    }

    @Test
    void testFindOne() throws Exception {

        this.mockMvc.perform(get("/customers/30"))
                .andExpect(status().isOk());
    }

    @Test
    void testFindAll() throws Exception {

        when(this.customersRepository.findAll(PageRequest.of(1, 15))).thenReturn(new PageImpl<>(Collections.singletonList(new Customer())));

        this.mockMvc.perform(get("/customers?page=1&size=15"))
                .andExpect(status().isOk());
    }

}
