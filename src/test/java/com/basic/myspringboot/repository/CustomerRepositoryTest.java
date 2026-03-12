package com.basic.myspringboot.repository;

import com.basic.myspringboot.entity.Customer;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
//@Transactional
class CustomerRepositoryTest {
    @Autowired
    CustomerRepository customerRepository;

    //1. Customer 등록
    @Test
    @Rollback(value = false)  //Rollback 처리를 하지 마세요
    @Disabled
    void testCreate() {
        //Given(준비단계)
        Customer customer = new Customer();
        customer.setCustomerId("A002");
        customer.setCustomerName("스프링부트2");
        //When(실행단계)
        Customer addCustomer = customerRepository.save(customer);
        //Then(검증단계)
        assertThat(addCustomer).isNotNull();
        assertThat(addCustomer.getCustomerName()).isEqualTo("스프링부트2");
    }

    //2. Customer 조회
    @Test
    void testFindBy() {
        Optional<Customer> optionalCustomer = customerRepository.findById(1L);
        if(optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();
            assertThat(customer.getId()).isEqualTo(1L);
        }else{
            System.out.println("Customer Not Found");
        }
        //ifPresent(Consumer)
        //Consumer의 추상메서드는 void accept(T t)
        optionalCustomer.ifPresent(customer -> System.out.println(customer.getCustomerName()));

    }

    @Test
    @Disabled
    void testFindByNotFound() {
        //orElseGet(Supplier)
        //Supplier의 추상메서드는 T get()
        Customer existCustomer = customerRepository.findById(2L)
                .orElseGet(() -> new Customer());
        assertThat(existCustomer.getId()).isNull();
        //assertThat(existCustomer.getId()).isEqualTo(2L);

        //public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier)
        Customer notFoundCustomer = customerRepository.findById(2L)
                .orElseThrow(() -> new RuntimeException("Customer Not Found"));
    }

    @Test
        //@Rollback(value = false)
    void testUpdate() {
        //조회를 하고 setter 호출하면 업데이트 됨
        Customer customer = customerRepository.findByCustomerId("A002")
                .orElseThrow(() -> new RuntimeException("Customer Not Found"));
        customer.setCustomerName("스프링부트2");
        customerRepository.save(customer);
        assertThat(customer.getCustomerName()).isEqualTo("스프링부트2");
    }
}