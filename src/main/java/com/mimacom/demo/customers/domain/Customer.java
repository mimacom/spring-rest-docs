package com.mimacom.demo.customers.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

/**
 * The customer object contains main details about a Customer
 */
@Entity
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}


