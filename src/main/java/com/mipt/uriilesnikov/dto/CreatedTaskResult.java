package com.mipt.uriilesnikov.dto;

import java.net.URI;

public record CreatedTaskResult(
        TaskDto task,
        URI location
) {
}
