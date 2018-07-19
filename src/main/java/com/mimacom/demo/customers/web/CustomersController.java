package com.mimacom.demo.customers.web;

import com.mimacom.demo.customers.domain.Customer;
import com.mimacom.demo.customers.repository.CustomersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Customers API to maintain the customer objects
 */
@RestController
@RequestMapping("/customers")
public class CustomersController {

    private final CustomersRepository customersRepository;

    @Autowired
    public CustomersController(CustomersRepository customersRepository) {
        this.customersRepository = customersRepository;
    }

    /**
     * Creates a new customer
     * @param customer the details of the new customer
     * @param salesRepId the identifier of the sales rep assigned to the customer
     * @return the customer details after creating
     */
    @PostMapping("/{salesRepId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Customer create(@RequestBody @Valid Customer customer, @PathVariable Long salesRepId) {
        Assert.isNull(customer.getId(), "Id must be null to create a new customer");
        validate(salesRepId, customer);
        return this.customersRepository.save(customer);
    }

    /**
     * Updates an existing customer
     * @param customer the details of the existing customer
     * @return the customer updated
     */
    @PutMapping
    public Customer update(@RequestBody @Valid Customer customer) {
        Assert.notNull(customer.getId(), "Customer id cannot be null");
        return this.customersRepository.save(customer);
    }

    /**
     * Removes an existing customer
     * @param customerId the id of the customer
     */
    @DeleteMapping("/{customerId}")
    public void delete(@PathVariable Long customerId) {
        Assert.notNull(customerId, "Customer id cannot be null");
        this.customersRepository.deleteById(customerId);
    }

    /**
     * Retrieves an existing customer
     * @param customerId the id of the customer
     * @return the details of the customer
     */
    @GetMapping("/{customerId}")
    public Customer findOne(@PathVariable Long customerId) {
        Assert.notNull(customerId, "Customer id cannot be null");
        return this.customersRepository.findById(customerId).orElse(null);
    }

    /**
     * Retrieves all existing customers
     * @param pageable optional pagination configuration
     * @return the list of all the customers matching the pagination configuration
     */
    @GetMapping
    public Page<Customer> findAll(Pageable pageable) {
        return this.customersRepository.findAll(pageable);
    }


    private void validate(Long salesRepId, Customer customer) {

    }

}
