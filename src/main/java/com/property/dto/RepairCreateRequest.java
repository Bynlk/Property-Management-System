package com.property.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 报修创建请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepairCreateRequest {

    @NotNull(message = "业主ID不能为空")
    private Integer ownerId;

    @NotBlank(message = "设备名称不能为空")
    @Size(max = 100, message = "设备名称长度不能超过100")
    private String deviceName;

    @Size(max = 1000, message = "故障描述长度不能超过1000")
    private String faultDescription;

    private Integer repairEmployeeId;

    private String status;
}
