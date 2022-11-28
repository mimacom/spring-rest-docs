package com.mimacom.demo.customers.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mimacom.demo.customers.domain.Customer;
import com.mimacom.demo.customers.domain.Gender;
import com.mimacom.demo.customers.repository.CustomersRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CustomersController.class)
@AutoConfigureRestDocs
public class CustomersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomersRepository customersRepository;

    @Test
    public void testCreateOneCustomer() throws Exception {

        Customer customer = new Customer();
        customer.setAge(23);
        customer.setFirstName("Liam");
        customer.setGender(Gender.MALE);
        customer.setPhoneNumber("1119992");

        when(this.customersRepository.save(any(Customer.class))).thenReturn(customer);

        FieldDescriptor[] customerDescriptor = getCustomerFieldDescriptor();

        this.mockMvc.perform(post("/customers/2").content(this.objectMapper.writeValueAsString(customer))
                .contentType(MediaType.APPLICATION_JSON))
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
        when(this.customersRepository.findById(34L)).thenReturn(Optional.of(mockCustomer));

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
