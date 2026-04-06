package com.mipt.uriilesnikov.validation;

import com.mipt.uriilesnikov.dto.TaskUpdateDto;
import com.mipt.uriilesnikov.model.Task;
import com.mipt.uriilesnikov.service.TaskService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerMapping;

import java.time.LocalDate;
import java.util.Map;

/**
 * Validates update DTO against existing task creation date.
 */
@Component
public class DueDateNotBeforeCreationValidator implements ConstraintValidator<DueDateNotBeforeCreation, TaskUpdateDto> {

    private final TaskService taskService;
    private final HttpServletRequest request;

    public DueDateNotBeforeCreationValidator(TaskService taskService, HttpServletRequest request) {
        this.taskService = taskService;
        this.request = request;
    }

    @Override
    public boolean isValid(TaskUpdateDto value, ConstraintValidatorContext context) {
        if (value == null || value.getDueDate() == null) {
            return true;
        }

        Long taskId = extractTaskIdFromPath();
        if (taskId == null) {
            return true;
        }

        Task existingTask = taskService.getTaskById(taskId);
        if (existingTask.getCreatedAt() == null) {
            return true;
        }

        LocalDate creationDate = existingTask.getCreatedAt().toLocalDate();
        boolean valid = !value.getDueDate().isBefore(creationDate);
        if (!valid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("dueDate must not be earlier than " + creationDate)
                    .addPropertyNode("dueDate")
                    .addConstraintViolation();
        }
        return valid;
    }

    @SuppressWarnings("unchecked")
    private Long extractTaskIdFromPath() {
        Object variables = request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (!(variables instanceof Map<?, ?> map)) {
            return null;
        }

        Object rawId = map.get("id");
        if (rawId == null) {
            rawId = map.get("taskId");
        }
        if (rawId == null) {
            return null;
        }

        try {
            return Long.valueOf(rawId.toString());
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
