package com.property.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.property.enums.DutyShift;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 值班实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Duty implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;

    @NotNull(message = "员工ID不能为空")
    private Integer employeeId;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Shanghai")
    @NotNull(message = "值班日期不能为空")
    private LocalDate dutyDate;

    @NotNull(message = "班次不能为空")
    private DutyShift shift;

    // 关联字段
    private String employeeName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime updatedAt;

    @Override
    public String toString() {
        return "Duty{id=" + id + ", employeeId=" + employeeId + ", dutyDate=" + dutyDate +
               ", shift='" + shift + "'}";
    }
}
