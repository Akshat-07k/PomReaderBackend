package com.project.dependency.controllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class GetRoutes {
    @GetMapping("/testing")
    public String getMethodName() {
        return "Yeah bro how is debugging going ";
    }
    
}
