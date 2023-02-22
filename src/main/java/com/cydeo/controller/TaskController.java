package com.cydeo.controller;


import com.cydeo.dto.ResponseWrapper;
import com.cydeo.dto.TaskDTO;
import com.cydeo.enums.Status;
import com.cydeo.exception.TicketingProjectException;
import com.cydeo.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@RestController
@RequestMapping("/api/v1/task")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    @RolesAllowed("Manager")
    public ResponseEntity<ResponseWrapper> getTasks() {

        return ResponseEntity.ok(new ResponseWrapper(
                "All tasks",
                taskService.listAllTasks(),
                HttpStatus.OK)
        );
    }

    @GetMapping("/{taskId}")
    @RolesAllowed("Manager")
    public ResponseEntity<ResponseWrapper> getTaskById(@PathVariable("taskId") Long id) {
        return ResponseEntity.ok(new ResponseWrapper(
                "Task by id",
                taskService.findById(id),
                HttpStatus.OK)
        );
    }

    @PostMapping
    @RolesAllowed("Manager")
    public ResponseEntity<ResponseWrapper> createTask(@RequestBody TaskDTO taskDTO) {
        taskService.save(taskDTO);

        return ResponseEntity.ok(new ResponseWrapper(
                "Task created",
                HttpStatus.CREATED)
        );
    }

    @DeleteMapping("/{id}")
    @RolesAllowed("Manager")
    public ResponseEntity<ResponseWrapper> deleteTask(@PathVariable("id") Long id) {
        taskService.delete(id);
        return ResponseEntity.ok(new ResponseWrapper(
                "Task deleted",
                HttpStatus.OK)
        );


    }

    @PutMapping()
    @RolesAllowed("Manager")
    public ResponseEntity<ResponseWrapper> updateTask(@RequestBody TaskDTO taskDTO) {
        taskService.update(taskDTO);

        return ResponseEntity.ok(new ResponseWrapper(
                "Task updated",
                HttpStatus.OK)
        );
    }

    @GetMapping("/employee/pending-tasks")
    @RolesAllowed("Employee")
    public ResponseEntity<ResponseWrapper> employeePendingTasks() throws TicketingProjectException {

        List<TaskDTO> taskDTOList = taskService.listAllTasksByStatusIsNot(Status.COMPLETE);

        return ResponseEntity.ok(new ResponseWrapper(
                "Employee pending tasks",
                taskDTOList,
                HttpStatus.OK)
        );
    }

    @PutMapping("/employee/update")
    @RolesAllowed("Employee")
    public ResponseEntity<ResponseWrapper> employeeUpdateTasks(@RequestBody TaskDTO taskDTO) {

        taskService.update(taskDTO);

        return ResponseEntity.ok(new ResponseWrapper(
                "Employee's Task updated",
                HttpStatus.OK)
        );
    }

    @GetMapping("/employee/archive")
    @RolesAllowed("Employee")
    public ResponseEntity<ResponseWrapper> employeeArchivedTasks() throws TicketingProjectException {

        List<TaskDTO> taskDTOList =taskService.listAllTasksByStatus(Status.COMPLETE);
        return ResponseEntity.ok(new ResponseWrapper(
                "Employee's Archived tasks",
                taskDTOList,
                HttpStatus.OK)
        );
    }


}
