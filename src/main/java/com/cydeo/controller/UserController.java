package com.cydeo.controller;


import com.cydeo.dto.ResponseWrapper;
import com.cydeo.dto.UserDTO;
import com.cydeo.exception.TicketingProjectException;
import com.cydeo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;

@RestController
@RequestMapping("/api/v1/user")
@Tag(name="UserController", description = "User API")
public class UserController {


    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    @RolesAllowed({"Manager", "Admin"})
    @Operation(summary = "Get Users")
    public ResponseEntity<ResponseWrapper> getUsers() {

        return ResponseEntity.ok(
                new ResponseWrapper("Here are all users",
                        userService.listAllUsers(),
                        HttpStatus.OK
                )
        );
    }

    @GetMapping("/{username}")
    @RolesAllowed("Admin")
    @Operation(summary = "Get User by username")
    public ResponseEntity<ResponseWrapper> getUserByUserName(@PathVariable("username") String userName) {

        return ResponseEntity.ok(
                new ResponseWrapper("User by username",
                        userService.findByUserName(userName),
                        HttpStatus.OK
                )
        );
    }

    @PostMapping("")
    @RolesAllowed("Admin")
    @Operation(summary = "Create User")
    public ResponseEntity<ResponseWrapper> createUser(@RequestBody UserDTO userDTO) {

        userService.save(userDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseWrapper("User created", HttpStatus.CREATED));


    }

    @PutMapping("")
    @RolesAllowed("Admin")
    @Operation(summary = "Update User")
    public ResponseEntity<ResponseWrapper> updateUser(@RequestBody UserDTO userDTO) {

        userService.update(userDTO);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseWrapper("User created", HttpStatus.OK));
   }

    @DeleteMapping("/{username}")
    @RolesAllowed("Admin")
    @Operation(summary = "Delete User")
    public ResponseEntity<ResponseWrapper> deleteUser(
            @PathVariable("username") String username)
            throws TicketingProjectException {

        userService.delete(username);
        return ResponseEntity.ok(
                new ResponseWrapper("User is successfully deleted",
                        HttpStatus.OK));
    }


}
