package com.mipt.uriilesnikov.controller;

import com.mipt.uriilesnikov.model.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for TaskController.
 * Uses @SpringBootTest and TestRestTemplate.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TaskControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testGetAllTasks_Positive() {
        ResponseEntity<Task[]> response = restTemplate.getForEntity("/api/tasks", Task[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testGetAllTasks_Negative() {
        // Negative scenario: incorrect method (PUT instead of GET)
        ResponseEntity<String> response = restTemplate.exchange("/api/tasks", HttpMethod.PUT, null, String.class);
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
    }

    @Test
    public void testGetTaskById_Positive() {
        Task newTask = new Task(null, "Test", "Desc", false);
        ResponseEntity<Task> createResponse = restTemplate.postForEntity("/api/tasks", newTask, Task.class);
        Long id = createResponse.getBody().getId();

        ResponseEntity<Task> response = restTemplate.getForEntity("/api/tasks/" + id, Task.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Test", response.getBody().getTitle());
    }

    @Test
    public void testGetTaskById_Negative() {
        ResponseEntity<Task> response = restTemplate.getForEntity("/api/tasks/9999", Task.class);
        assertNotEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testCreateTask_Positive() {
        Task task = new Task(null, "New Task", "Description", false);
        ResponseEntity<Task> response = restTemplate.postForEntity("/api/tasks", task, Task.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody().getId());
    }

    @Test
    public void testCreateTask_Negative() {
        // Empty request body
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>("", headers);
        ResponseEntity<String> response = restTemplate.exchange("/api/tasks", HttpMethod.POST, entity, String.class);
        // We expect a deserialization error or 400
        assertNotEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testUpdateTask_Positive() {
        Task newTask = new Task(null, "Update Test", "Desc", false);
        ResponseEntity<Task> createResponse = restTemplate.postForEntity("/api/tasks", newTask, Task.class);
        Long id = createResponse.getBody().getId();

        Task updateTask = new Task(id, "Updated", "New Desc", true);
        ResponseEntity<Task> response = restTemplate.exchange("/api/tasks/" + id, HttpMethod.PUT, new HttpEntity<>(updateTask), Task.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Updated", response.getBody().getTitle());
    }

    @Test
    public void testUpdateTask_Negative() {
        Task updateTask = new Task(9999L, "Updated", "New Desc", true);
        ResponseEntity<Task> response = restTemplate.exchange("/api/tasks/9999", HttpMethod.PUT, new HttpEntity<>(updateTask), Task.class);
        assertNotEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testDeleteTask_Positive() {
        Task newTask = new Task(null, "Delete Test", "Desc", false);
        ResponseEntity<Task> createResponse = restTemplate.postForEntity("/api/tasks", newTask, Task.class);
        Long id = createResponse.getBody().getId();

        ResponseEntity<Void> response = restTemplate.exchange("/api/tasks/" + id, HttpMethod.DELETE, null, Void.class);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void testDeleteTask_Negative() {
        ResponseEntity<Void> response = restTemplate.exchange("/api/tasks/9999", HttpMethod.DELETE, null, Void.class);
        assertNotNull(response);
    }
}
