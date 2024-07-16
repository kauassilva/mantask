package com.api.mantask.controller.dto;

import java.util.List;

public record TaskListDto(List<TaskItemDto> taskItems,
                          int page,
                          int pageSize,
                          int totalPages,
                          long totalElements) {
}
