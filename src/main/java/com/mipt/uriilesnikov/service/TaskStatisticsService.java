package com.mipt.uriilesnikov.service;

import com.mipt.uriilesnikov.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Statistics Service.
 * Demonstrates the use of @Qualifier to inject a specific bean.
 */
@Service
public class TaskStatisticsService {

    private final TaskRepository primaryRepository;
    private final TaskRepository stubRepository;

    public TaskStatisticsService(TaskRepository primaryRepository,
                                 @Qualifier("stubTaskRepository") TaskRepository stubRepository) {
        this.primaryRepository = primaryRepository;
        this.stubRepository = stubRepository;
    }

    public String compareRepositories() {
        int primaryCount = primaryRepository.findAll().size();
        int stubCount = stubRepository.findAll().size();
        return "Primary Repo Tasks: " + primaryCount + ", Stub Repo Tasks: " + stubCount;
    }
}
