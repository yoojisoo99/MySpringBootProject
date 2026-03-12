package com.basic.myspringboot.controller;

import com.basic.myspringboot.entity.User;
import com.basic.myspringboot.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;

    //static/index.html => UserController의 leaf() 호출 => templates/leaf.html
    @GetMapping("/thymeleaf")
    public String leaf(Model model) {
        model.addAttribute("name","스프링부트");
        return "leaf";
    }

    /*
    public ModelAndView(String viewName,
                String modelName,
                Object modelObject)
     */
    @GetMapping("/index")
    public ModelAndView index() {
        List<User> userList = userRepository.findAll();
        return new ModelAndView("index","users",userList);
    }

//    @GetMapping("/index")
//    public String index(Model model) {
//        model.addAttribute("users", userRepository.findAll());
//        return "index";
//    }

    //등록 페이지를 Load하는 메서드
    @GetMapping("/signup")
    public String showSignUpForm(@ModelAttribute("userForm") User user) {
        return "add-user";
    }
    //입력항목을 검증하고 등록처리를 하는 메서드
    @PostMapping("/adduser")
    public String addUser(@Valid @ModelAttribute("userForm") User user,
                          BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "add-user";
        }
        userRepository.save(user);
//        model.addAttribute("users", userRepository.findAll());
//        return "index";
        return "redirect:/index";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") long id, Model model) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        model.addAttribute("userForm", user);
        return "update-user";
    }

    @PostMapping("/update/{id}")
    public String updateUser(@PathVariable("id") long id,
                             @Valid @ModelAttribute("userForm") User user,
                             BindingResult result) {
        if (result.hasErrors()) {
            user.setId(id);
            return "update-user";
        }
        userRepository.save(user);
        return "redirect:/index";
    }

}