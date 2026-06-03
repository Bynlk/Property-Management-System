package com.property.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.property.enums.FeeStatus;
import com.property.enums.FeeType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 欠费实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Fee implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;

    @NotNull(message = "业主ID不能为空")
    private Integer ownerId;

    private Integer houseId;

    @NotNull(message = "费用类型不能为空")
    private FeeType feeType;

    @NotNull(message = "金额不能为空")
    private BigDecimal amount;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Shanghai")
    private LocalDate shouldPayDate;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Shanghai")
    private LocalDate paidDate;

    private FeeStatus status;

    // 关联字段
    private String ownerName;
    private String houseInfo;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime updatedAt;

    @Override
    public String toString() {
        return "Fee{id=" + id + ", ownerId=" + ownerId + ", houseId=" + houseId +
               ", feeType='" + feeType + "', amount=" + amount + ", shouldPayDate=" + shouldPayDate +
               ", paidDate=" + paidDate + ", status='" + status + "'}";
    }
}
