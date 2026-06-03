package com.property.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 投诉更新请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintUpdateRequest {

    @NotNull(message = "投诉ID不能为空")
    private Integer id;

    private Integer ownerId;

    @Size(max = 100, message = "标题长度不能超过100")
    private String title;

    @Size(max = 1000, message = "投诉内容长度不能超过1000")
    private String content;

    private String status;
}
