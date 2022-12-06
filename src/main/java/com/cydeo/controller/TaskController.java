package com.cydeo.controller;


import com.cydeo.dto.ResponseWrapper;
import com.cydeo.dto.TaskDTO;
import com.cydeo.enums.Status;
import com.cydeo.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/task")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<ResponseWrapper> getTasks() {

        return ResponseEntity.ok(new ResponseWrapper(
                "All tasks",
                taskService.listAllTasks(),
                HttpStatus.OK)
        );
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<ResponseWrapper> getTaskById(@PathVariable("taskId") Long id) {
        return ResponseEntity.ok(new ResponseWrapper(
                "Task by id",
                taskService.findById(id),
                HttpStatus.OK)
        );
    }

    @PostMapping
    public ResponseEntity<ResponseWrapper> createTask(@RequestBody TaskDTO taskDTO) {
        taskService.save(taskDTO);

        return ResponseEntity.ok(new ResponseWrapper(
                "Task created",
                HttpStatus.CREATED)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseWrapper> deleteTask(@PathVariable("id") Long id) {
        taskService.delete(id);
        return ResponseEntity.ok(new ResponseWrapper(
                "Task deleted",
                HttpStatus.OK)
        );


    }

    @PutMapping()
    public ResponseEntity<ResponseWrapper> updateTask(@RequestBody TaskDTO taskDTO) {
        taskService.update(taskDTO);

        return ResponseEntity.ok(new ResponseWrapper(
                "Task updated",
                HttpStatus.OK)
        );
    }

    @GetMapping("/employee/pending-tasks")
    public ResponseEntity<ResponseWrapper> employeePendingTasks() {

        List<TaskDTO> taskDTOList = taskService.listAllTasksByStatusIsNot(Status.COMPLETE);

        return ResponseEntity.ok(new ResponseWrapper(
                "Employee pending tasks",
                taskDTOList,
                HttpStatus.OK)
        );
    }

    @PutMapping("/employee/update")
    public ResponseEntity<ResponseWrapper> employeeUpdateTasks(@RequestBody TaskDTO taskDTO) {

        taskService.update(taskDTO);

        return ResponseEntity.ok(new ResponseWrapper(
                "Employee's Task updated",
                HttpStatus.OK)
        );
    }

    @GetMapping("/employee/archive")
    public ResponseEntity<ResponseWrapper> employeeArchivedTasks() {

        List<TaskDTO> taskDTOList =taskService.listAllTasksByStatus(Status.COMPLETE);
        return ResponseEntity.ok(new ResponseWrapper(
                "Employee's Archived tasks",
                taskDTOList,
                HttpStatus.OK)
        );
    }


}
