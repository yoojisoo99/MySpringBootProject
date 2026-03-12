package com.basic.myspringboot.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "customers")
@Getter @Setter
@DynamicUpdate
public class Customer {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String customerId;

    @Column(nullable = false)
    private String customerName;

}