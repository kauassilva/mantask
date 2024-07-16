package com.api.mantask.controller;

import com.api.mantask.controller.dto.CreateTaskDto;
import com.api.mantask.entity.Role;
import com.api.mantask.entity.Task;
import com.api.mantask.entity.User;
import com.api.mantask.repository.TaskRepository;
import com.api.mantask.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskController(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    @PostMapping
    public ResponseEntity<Void> createTask(@RequestBody CreateTaskDto dto, JwtAuthenticationToken token) {
        Optional<User> user = userRepository.findById(UUID.fromString(token.getName()));
        Task task = new Task();
        task.setUser(user.get());
        task.setTitle(dto.title());
        taskRepository.save(task);
        return ResponseEntity.ok().build();
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable("id") Long taskId, JwtAuthenticationToken token) {
        Optional<User> user = userRepository.findById(UUID.fromString(token.getName()));

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        boolean isAdmin = user.get()
                .getRoles()
                .stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase(Role.Enum.ADMIN.name()));

        if (isAdmin || task.getUser().getUserId().equals(UUID.fromString(token.getName())))
            taskRepository.deleteById(taskId);
        else
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        return ResponseEntity.ok().build();
    }

}
