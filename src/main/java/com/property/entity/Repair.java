package com.property.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.property.enums.RepairStatus;
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
 * 报修实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Repair implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;

    @NotNull(message = "业主ID不能为空")
    private Integer ownerId;

    @NotBlank(message = "设备名称不能为空")
    @Size(max = 100, message = "设备名称长度不能超过100")
    private String deviceName;

    @Size(max = 1000, message = "故障描述长度不能超过1000")
    private String faultDescription;

    private Integer repairEmployeeId;

    private RepairStatus status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime completedAt;

    // 关联字段
    private String ownerName;
    private String repairEmployeeName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime updatedAt;

    @Override
    public String toString() {
        return "Repair{id=" + id + ", ownerId=" + ownerId + ", deviceName='" + deviceName +
               "', faultDescription='" + faultDescription + "', repairEmployeeId=" + repairEmployeeId +
               "', status='" + status + "'}";
    }
}
