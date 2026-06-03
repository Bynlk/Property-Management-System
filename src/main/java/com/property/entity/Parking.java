package com.property.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.property.enums.ParkingStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 停车位实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Parking implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;

    @NotBlank(message = "车位编号不能为空")
    @Size(max = 20, message = "车位编号长度不能超过20")
    private String spotNumber;

    @Size(max = 20, message = "车牌号长度不能超过20")
    private String licensePlate;

    private Integer ownerId;

    private ParkingStatus status;

    // 关联字段
    private String ownerName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime updatedAt;

    @Override
    public String toString() {
        return "Parking{id=" + id + ", spotNumber='" + spotNumber + "', licensePlate='" + licensePlate +
               "', ownerId=" + ownerId + ", status='" + status + "'}";
    }
}
