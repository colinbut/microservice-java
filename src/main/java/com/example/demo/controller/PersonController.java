package com.example.demo.controller;

import com.example.demo.model.Person;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/person")
public class PersonController {

    @GetMapping
    public ResponseEntity<List<Person>> getPersons(){
        return ResponseEntity.ok().build();
    }
}
