package com.property.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 停车位更新请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingUpdateRequest {

    @NotNull(message = "车位ID不能为空")
    private Integer id;

    @Size(max = 20, message = "车位编号长度不能超过20")
    private String spotNumber;

    @Size(max = 20, message = "车牌号长度不能超过20")
    private String licensePlate;

    private Integer ownerId;

    private String status;
}
