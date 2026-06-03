package com.property.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.property.enums.ComplaintStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 投诉实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Complaint implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;

    @NotNull(message = "业主ID不能为空")
    private Integer ownerId;

    @NotBlank(message = "投诉标题不能为空")
    @Size(max = 100, message = "标题长度不能超过100")
    private String title;

    @Size(max = 1000, message = "投诉内容长度不能超过1000")
    private String content;

    private ComplaintStatus status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime resolvedAt;

    // 关联字段（JOIN 查询结果）
    private String ownerName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime updatedAt;

    @Override
    public String toString() {
        return "Complaint{id=" + id + ", ownerId=" + ownerId + ", title='" + title +
               "', status='" + (status != null ? status.getValue() : null) + "'}";
    }
}
