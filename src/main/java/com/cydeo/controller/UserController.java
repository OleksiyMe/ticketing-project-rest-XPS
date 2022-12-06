package com.cydeo.controller;


import com.cydeo.dto.ResponseWrapper;
import com.cydeo.dto.UserDTO;
import com.cydeo.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {


    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    public ResponseEntity<ResponseWrapper> getUsers() {

        return ResponseEntity.ok(
                new ResponseWrapper("Here are all users",
                        userService.listAllUsers(),
                        HttpStatus.OK
                )
        );
    }

    @GetMapping("/{username}")
    public ResponseEntity<ResponseWrapper> getUserByUserName(@PathVariable("username") String userName) {

        return ResponseEntity.ok(
                new ResponseWrapper("User by username",
                        userService.findByUserName(userName),
                        HttpStatus.OK
                )
        );
    }

    @PostMapping("")
    public ResponseEntity<ResponseWrapper> createUser(@RequestBody UserDTO userDTO) {

        userService.save(userDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseWrapper("User created", HttpStatus.CREATED));


    }

    @PutMapping("")
    public ResponseEntity<ResponseWrapper> updateUser(@RequestBody UserDTO userDTO) {

        userService.update(userDTO);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseWrapper("User created", HttpStatus.OK));
   }

    @DeleteMapping("/{username}")
    public ResponseEntity<ResponseWrapper> deleteUser(@PathVariable("username") String username) {

        userService.delete(username);
        return ResponseEntity.ok(
                new ResponseWrapper("User is successfully deleted",
                        HttpStatus.OK));
    }


}
