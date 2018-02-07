package com.mimacom.demo.customers.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mimacom.demo.customers.domain.Customer;
import com.mimacom.demo.customers.domain.Gender;
import com.mimacom.demo.customers.repository.CustomersRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(CustomersController.class)
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

        this.mockMvc.perform(post("/customers").content(this.objectMapper.writeValueAsString(customer))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isCreated())
        .andExpect(jsonPath("$.firstName").value("Liam"))
        .andExpect(jsonPath("$.lastName").isEmpty())
        .andExpect(jsonPath("$.gender").value(Gender.MALE.name()))
        .andExpect(jsonPath("$.phoneNumber").value("1119992"))
        .andExpect(jsonPath("$.age").value(23));

    }

}