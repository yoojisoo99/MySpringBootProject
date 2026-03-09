package com.basic.myspringboot.runner;

import com.basic.myspringboot.property.MyBootProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class MyRunner implements ApplicationRunner {
    @Value("${myboot.name}")
    private String name;

    @Value("${myboot.age}")
    private int age;

    @Autowired
    private Environment environment;

    @Autowired
    private MyBootProperties properties;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("MyBootProperties getName() = " + properties.getName());

        System.out.println("${myboot.name} = " + name);
        System.out.println("${myboot.age} = " + age);

        System.out.println("${myboot.fullName}  = " + environment.getProperty("myboot.fullName"));

        System.out.println("VM 아규먼트 foo : " + args.containsOption("foo"));
        System.out.println("Program 아규먼트 bar : " + args.containsOption("bar"));

        /*
            default void forEach(Consumer<? super T> action)
            Consumer 인터페이스의 void accept(T t)
         */
        //Anonymous Inner Class - 1회성으로 쓰고 말걸 길게 안쓰고 이런식으로 작성(안드로이드 개발에 많이 쓰임)
        args.getOptionNames()
                .forEach(new Consumer<String>() { //선언과 동시에 상속
                    @Override
                    public void accept(String s) {
                        System.out.println(s);
                    }
                });

        //Argument 목록 출력하기 (람다식)
        args.getOptionNames() //Set<String>
                .forEach(name -> System.out.println(name));

        //Methoer Reference (메서드 레퍼런스)
        args.getOptionNames()//Set<String>
                .forEach(System.out::println); //::=>알아서 argument에 자동으로 입력됨
    }
}
