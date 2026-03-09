package com.basic.myspringboot.runner;

import com.basic.myspringboot.config.CustomerVO;
import com.basic.myspringboot.property.MyBootProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @Autowired
    private CustomerVO customerVO;

    //Logger 객체 생성
    private Logger logger = LoggerFactory.getLogger(MyRunner.class);

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("Logger 구현체 클래스명 = " + logger.getClass().getName());
        logger.debug("현재 활성화된 CustomerVO = " + customerVO);
        logger.debug("MyBootProperties getName() = " + properties.getName());

        logger.info("${myboot.name} = " + name);
        logger.info("${myboot.age} = " + age);

        logger.debug("${myboot.fullName}  = " + environment.getProperty("myboot.fullName"));
        logger.debug("VM 아규먼트 foo : " + args.containsOption("foo"));
        logger.debug("Program 아규먼트 bar : " + args.containsOption("bar"));

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
