package com.property.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.property.enums.HouseStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 房屋实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class House implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;

    @NotBlank(message = "楼栋号不能为空")
    @Size(max = 20, message = "楼栋号长度不能超过20")
    private String building;

    @Size(max = 10, message = "单元号长度不能超过10")
    private String unit;

    @NotBlank(message = "房间号不能为空")
    @Size(max = 20, message = "房间号长度不能超过20")
    private String roomNumber;

    private BigDecimal area;

    @Size(max = 20, message = "户型长度不能超过20")
    private String houseType;

    private Integer ownerId;

    private HouseStatus status;

    // 关联字段（用于联查显示）
    private String ownerName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime updatedAt;

    @Override
    public String toString() {
        return "House{id=" + id + ", building='" + building + "', unit='" + unit +
               "', roomNumber='" + roomNumber + "', area=" + area +
               "', houseType='" + houseType + "', status='" + status + "'}";
    }
}
