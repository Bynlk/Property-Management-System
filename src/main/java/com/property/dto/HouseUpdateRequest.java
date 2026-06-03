package com.property.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 房屋更新请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HouseUpdateRequest {

    @NotNull(message = "房屋ID不能为空")
    private Integer id;

    @Size(max = 20, message = "楼栋号长度不能超过20")
    private String building;

    @Size(max = 10, message = "单元号长度不能超过10")
    private String unit;

    @Size(max = 20, message = "房间号长度不能超过20")
    private String roomNumber;

    private BigDecimal area;

    @Size(max = 20, message = "户型长度不能超过20")
    private String houseType;

    private Integer ownerId;

    private String status;
}
