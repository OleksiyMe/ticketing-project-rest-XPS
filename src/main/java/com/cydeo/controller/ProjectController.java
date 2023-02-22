package com.cydeo.controller;


import com.cydeo.dto.ProjectDTO;
import com.cydeo.dto.ResponseWrapper;
import com.cydeo.dto.UserDTO;
import com.cydeo.exception.TicketingProjectException;
import com.cydeo.service.ProjectService;
import com.cydeo.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;

@RestController
@RequestMapping("/api/v1/project")
public class ProjectController {

    private final ProjectService projectService;
    private final UserService userService;

    public ProjectController(ProjectService projectService, UserService userService) {
        this.projectService = projectService;

        this.userService = userService;
    }

    @GetMapping
    @RolesAllowed("Manager")
    public ResponseEntity<ResponseWrapper> getProjects() {

        return ResponseEntity.ok(
                new ResponseWrapper("See all projects", projectService.listAllProjects(),
                        HttpStatus.OK)
        );
    }

    @GetMapping("/{code}")
    @RolesAllowed("Manager")
    public ResponseEntity<ResponseWrapper> getProjectByCode(@PathVariable("code") String projectCode) {

        return ResponseEntity.ok(
                new ResponseWrapper("See project by code",
                        projectService.getByProjectCode(projectCode),
                        HttpStatus.OK)
        );
    }

    @PostMapping("")
    @RolesAllowed({"Admin", "Manager"})
    public ResponseEntity<ResponseWrapper> createProject(@RequestBody ProjectDTO projectDTO) {
        projectService.save(projectDTO);
        return ResponseEntity.ok(
                new ResponseWrapper("Project created",
                        HttpStatus.CREATED)
        );

    }

    @PutMapping
    @RolesAllowed("Manager")
    public ResponseEntity<ResponseWrapper> updateProject(@RequestBody ProjectDTO projectDTO) {
        projectService.update(projectDTO);
        return ResponseEntity.ok(
                new ResponseWrapper("Project updated",
                        HttpStatus.OK)
        );
    }

    @DeleteMapping("/{code}")
    @RolesAllowed("Manager")
    public ResponseEntity<ResponseWrapper> deleteProject(@PathVariable("code") String projectCode) {
        projectService.delete(projectCode);
        return ResponseEntity.ok(
                new ResponseWrapper("Project deleted",
                        HttpStatus.OK)
        );

    }

    @GetMapping("/manager/project-status")
    @RolesAllowed("Manager")
    public ResponseEntity<ResponseWrapper> getProjectByManager() throws TicketingProjectException {
        return ResponseEntity.ok(
                new ResponseWrapper("See all projects for manager",
                        projectService.listAllProjectDetails(),
                        HttpStatus.OK)
        );
    }

    @PutMapping("/manager/complete/{projectCode}")    //PUT -- I update project
    @RolesAllowed("Manager")
    public ResponseEntity<ResponseWrapper> managerCompleteProject(@PathVariable("projectCode") String projectCode) {

        projectService.complete(projectCode);
        return ResponseEntity.ok(
                new ResponseWrapper("Project completed",
                        HttpStatus.OK));
    }


}
