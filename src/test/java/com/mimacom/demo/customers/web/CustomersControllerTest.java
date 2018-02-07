package com.mimacom.demo.customers.web;

import capital.scalable.restdocs.AutoDocumentation;
import capital.scalable.restdocs.jackson.JacksonResultHandlers;
import capital.scalable.restdocs.response.ResponseModifyingPreprocessors;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mimacom.demo.customers.domain.Customer;
import com.mimacom.demo.customers.domain.Gender;
import com.mimacom.demo.customers.repository.CustomersRepository;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.cli.CliDocumentation;
import org.springframework.restdocs.http.HttpDocumentation;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(CustomersController.class)
public class CustomersControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomersRepository customersRepository;

    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("target/generated-snippets");

    @Autowired
    private WebApplicationContext context;
    @Before
    public void setUp() throws Exception {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .alwaysDo(JacksonResultHandlers.prepareJackson(objectMapper))
                .alwaysDo(MockMvcRestDocumentation.document("{class-name}/{method-name}",
                        Preprocessors.preprocessRequest(),
                        Preprocessors.preprocessResponse(
                                ResponseModifyingPreprocessors.replaceBinaryContent(),
                                ResponseModifyingPreprocessors.limitJsonArrayLength(objectMapper),
                                Preprocessors.prettyPrint())))
                .apply(MockMvcRestDocumentation.documentationConfiguration(restDocumentation)
                        .uris()
                        .withScheme("http")
                        .withHost("localhost")
                        .withPort(8080)
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
                .build();
    }

    @Test
    public void testCreateOneCustomer() throws Exception {

        Customer customer = new Customer();
        customer.setAge(23);
        customer.setFirstName("Liam");
        customer.setGender(Gender.MALE);
        customer.setPhoneNumber("1119992");

        when(this.customersRepository.save(any(Customer.class))).thenReturn(customer);

        FieldDescriptor[] customerDescriptor = getCustomerFieldDescriptor();

        this.mockMvc.perform(post("/customers").content(this.objectMapper.writeValueAsString(customer))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Liam"))
                .andExpect(jsonPath("$.lastName").isEmpty())
                .andExpect(jsonPath("$.gender").value(Gender.MALE.name()))
                .andExpect(jsonPath("$.phoneNumber").value("1119992"))
                .andExpect(jsonPath("$.age").value(23))
        .andDo(document("shouldCreateCustomer",
                requestFields(customerDescriptor),
                responseFields(customerDescriptor)));

    }

    @Test
    public void testGetOneCustomer() throws Exception {

        Customer mockCustomer = new Customer();
        mockCustomer.setId(34L);
        when(this.customersRepository.findOne(34L)).thenReturn(mockCustomer);

        this.mockMvc.perform(RestDocumentationRequestBuilders.get("/customers/{customerId}", "34"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(34))
        .andDo(document("shouldGetOneCustomer",
                pathParameters(
                        parameterWithName("customerId").description("The id of the customer to retrieve")
                ),
                responseFields(this.getCustomerFieldDescriptor())));
    }

    private FieldDescriptor[] getCustomerFieldDescriptor() {
        return new FieldDescriptor[]{fieldWithPath("age").description("The age of the customer").type(Integer.class.getSimpleName()),
                    fieldWithPath("firstName").description("The first name of the customer").type(String.class.getSimpleName()),
                    fieldWithPath("gender").description("The gender of the customer (FEMALE or MALE)").type(Gender.class.getSimpleName()),
                    fieldWithPath("phoneNumber").description("The cell phone number of the customer").type(String.class.getSimpleName()),
                    fieldWithPath("id").description("The unique id of the customer").optional().type(Long.class.getSimpleName()),
                    fieldWithPath("lastName").description("The last name of the customer").optional().type(String.class.getSimpleName())};
    }
}