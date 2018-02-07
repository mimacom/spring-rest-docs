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

@RestController
@RequestMapping("/customers")
public class CustomersController {

    private final CustomersRepository customersRepository;

    @Autowired
    public CustomersController(CustomersRepository customersRepository) {
        this.customersRepository = customersRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Customer create(@RequestBody @Valid Customer customer) {
        Assert.isNull(customer.getId(), "Id must be null to create a new customer");
        return this.customersRepository.save(customer);
    }

    @PutMapping
    public Customer update(@RequestBody @Valid Customer customer) {
        Assert.notNull(customer.getId(), "Customer id cannot be null");
        return this.customersRepository.save(customer);
    }

    @DeleteMapping("/{customerId}")
    public void delete(@PathVariable Long customerId) {
        Assert.notNull(customerId, "Customer id cannot be null");
        this.customersRepository.delete(customerId);
    }

    @GetMapping("/{customerId}")
    public Customer findOne(@PathVariable Long customerId) {
        Assert.notNull(customerId, "Customer id cannot be null");
        return this.customersRepository.findOne(customerId);
    }

    @GetMapping
    public Page<Customer> findAll(Pageable pageable) {
        return this.customersRepository.findAll(pageable);
    }


}
