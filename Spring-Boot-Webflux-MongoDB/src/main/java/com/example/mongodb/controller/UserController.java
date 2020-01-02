package com.example.mongodb.controller;

import com.example.mongodb.entity.User;
import com.example.mongodb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Created by dengzhiming on 2019/7/10
 */
@RestController
public class UserController {
    @Autowired
    private UserService service;

    @GetMapping("/user")
    public Flux<User> getUsers() {
        return this.service.selectAll();
    }

    @GetMapping("/user/{id}")
    public Mono<ResponseEntity<User>> getUser(@PathVariable String id) {
        return this.service.selectById(id)
                .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/user")
    public Mono<User> createUser(User user) {
        return this.service.create(user);
    }

    @PutMapping("/user/{id}")
    public Mono<ResponseEntity<User>> updateUser(@PathVariable String id, User user) {
        return this.service.updateById(id, user)
                .map(u -> new ResponseEntity<>(u, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/user/{id}")
    public Mono<ResponseEntity<Void>> deleteUser(@PathVariable String id) {
        return this.service.deleteById(id)
                .then(Mono.just(new ResponseEntity<Void>(HttpStatus.OK)))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
