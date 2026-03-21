package com.mipt.uriilesnikov.validation.validator;

import com.mipt.uriilesnikov.dto.TaskUpdateDto;
import com.mipt.uriilesnikov.model.Task;
import com.mipt.uriilesnikov.service.TaskService;
import com.mipt.uriilesnikov.validation.DueDateNotBeforeCreation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerMapping;

@Component
public class DueDateNotBeforeCreationValidator implements ConstraintValidator<DueDateNotBeforeCreation, TaskUpdateDto> {
    private final HttpServletRequest request;
    private final TaskService taskService;

    public DueDateNotBeforeCreationValidator(HttpServletRequest request, TaskService taskService) {
        this.request = request;
        this.taskService = taskService;
    }

    @Override
    public boolean isValid(TaskUpdateDto value, ConstraintValidatorContext context) {
        if (value == null || value.getDueDate() == null) {
            return true;
        }

        Object attr = request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (!(attr instanceof Map<?, ?> variables)) {
            return true;
        }

        Object idRaw = variables.get("id");
        if (idRaw == null) {
            return true;
        }

        try {
            Long taskId = Long.parseLong(idRaw.toString());
            Task task = taskService.getById(taskId);
            return !value.getDueDate().isBefore(task.getCreatedAt().toLocalDate());
        } catch (NumberFormatException ex) {
            return true;
        }
    }
}
