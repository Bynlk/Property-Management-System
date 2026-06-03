package com.property.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 状态变更审计日志实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatusChangeLog implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String entityType;
    private Integer entityId;
    private String oldStatus;
    private String newStatus;
    private String changedBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime changedAt;
}
