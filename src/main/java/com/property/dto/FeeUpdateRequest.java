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
 * 费用更新请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeUpdateRequest {

    @NotNull(message = "费用ID不能为空")
    private Integer id;

    private Integer ownerId;

    private Integer houseId;

    private String feeType;

    private BigDecimal amount;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Shanghai")
    private LocalDate shouldPayDate;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Shanghai")
    private LocalDate paidDate;

    private String status;
}
