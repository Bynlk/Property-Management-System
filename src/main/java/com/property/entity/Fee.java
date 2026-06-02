package com.property.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 欠费实体类
 */
public class Fee implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;

    @NotNull(message = "业主ID不能为空")
    private Integer ownerId;

    private Integer houseId;

    @NotBlank(message = "费用类型不能为空")
    private String feeType;

    @NotNull(message = "金额不能为空")
    private BigDecimal amount;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Shanghai")
    private Date shouldPayDate;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Shanghai")
    private Date paidDate;

    @Pattern(regexp = "^$|^未缴$|^已缴$", message = "状态只能为未缴或已缴")
    private String status;

    // 关联字段
    private String ownerName;
    private String houseInfo;

    private Date createdAt;
    private Date updatedAt;

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getOwnerId() { return ownerId; }
    public void setOwnerId(Integer ownerId) { this.ownerId = ownerId; }

    public Integer getHouseId() { return houseId; }
    public void setHouseId(Integer houseId) { this.houseId = houseId; }

    public String getFeeType() { return feeType; }
    public void setFeeType(String feeType) { this.feeType = feeType; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public Date getShouldPayDate() { return shouldPayDate; }
    public void setShouldPayDate(Date shouldPayDate) { this.shouldPayDate = shouldPayDate; }

    public Date getPaidDate() { return paidDate; }
    public void setPaidDate(Date paidDate) { this.paidDate = paidDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public String getHouseInfo() { return houseInfo; }
    public void setHouseInfo(String houseInfo) { this.houseInfo = houseInfo; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "Fee{id=" + id + ", ownerId=" + ownerId + ", houseId=" + houseId +
               ", feeType='" + feeType + "', amount=" + amount + ", shouldPayDate=" + shouldPayDate +
               ", paidDate=" + paidDate + ", status='" + status + "'}";
    }
}
