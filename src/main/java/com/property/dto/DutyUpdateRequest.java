package com.property.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 值班更新请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DutyUpdateRequest {

    @NotNull(message = "值班ID不能为空")
    private Integer id;

    private Integer employeeId;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Shanghai")
    private LocalDate dutyDate;

    private String shift;
}
