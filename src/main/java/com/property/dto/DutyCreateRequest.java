package com.property.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 值班创建请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DutyCreateRequest {

    @NotNull(message = "员工ID不能为空")
    private Integer employeeId;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Shanghai")
    @NotNull(message = "值班日期不能为空")
    private LocalDate dutyDate;

    @NotNull(message = "班次不能为空")
    private String shift;
}
