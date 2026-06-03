package com.property.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 费用创建请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeCreateRequest {

    @NotNull(message = "业主ID不能为空")
    private Integer ownerId;

    private Integer houseId;

    @NotNull(message = "费用类型不能为空")
    private String feeType;

    @NotNull(message = "金额不能为空")
    private BigDecimal amount;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Shanghai")
    private LocalDate shouldPayDate;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Shanghai")
    private LocalDate paidDate;

    private String status;
}
