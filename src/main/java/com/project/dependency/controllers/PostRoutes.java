package com.project.dependency.controllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.project.dependency.models.Dependency;
import com.project.dependency.service.PomReadService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;


@RestController
@CrossOrigin("http://localhost:5173/")
@RequestMapping("/api")
public class PostRoutes {

    @Autowired
    private PomReadService pomReadService;

    @PostMapping("/getLatestVersion")
    public List<Dependency> postMethodName(@RequestPart("file") MultipartFile file ) {

        if(file.isEmpty()){return null;}

        return pomReadService.parsePomDependencies(file);
            
    }
        
}
