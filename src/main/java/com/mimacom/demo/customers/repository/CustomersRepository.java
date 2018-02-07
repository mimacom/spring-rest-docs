package com.mimacom.demo.customers.repository;

import com.mimacom.demo.customers.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomersRepository extends JpaRepository<Customer, Long> {
}
