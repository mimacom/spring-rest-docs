package com.mimacom.demo.customers.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

/**
 * The customer object contains main details about a Customer
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Customer {

    /**
     * Unique identifier of the customer
     */
    @Id
    private Long id;

    /**
     * First name of the customer
     */
    @NotNull
    private String firstName;

    /**
     * Last name of the customer
     */
    private String lastName;

    /**
     * Age in years of the customer
     */
    @NotNull
    private Integer age;

    /**
     * Gender of the customer
     */
    private Gender gender;

    /**
     * Phone number of the customer
     */
    private String phoneNumber;

}


