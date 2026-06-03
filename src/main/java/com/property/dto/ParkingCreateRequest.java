package com.property.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 停车位创建请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingCreateRequest {

    @NotBlank(message = "车位编号不能为空")
    @Size(max = 20, message = "车位编号长度不能超过20")
    private String spotNumber;

    @Size(max = 20, message = "车牌号长度不能超过20")
    private String licensePlate;

    private Integer ownerId;

    private String status;
}
