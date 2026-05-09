package com.property.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 欠费实体类
 */
public class Fee implements Serializable {
    private Integer id;
    private Integer ownerId;
    private Integer houseId;
    private String feeType;
    private BigDecimal amount;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Shanghai")
    private Date shouldPayDate;
    private String status;

    // 关联字段
    private String ownerName;
    private String houseInfo;

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

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public String getHouseInfo() { return houseInfo; }
    public void setHouseInfo(String houseInfo) { this.houseInfo = houseInfo; }

    @Override
    public String toString() {
        return "Fee{id=" + id + ", ownerId=" + ownerId + ", houseId=" + houseId +
               ", feeType='" + feeType + "', amount=" + amount + ", shouldPayDate=" + shouldPayDate +
               ", status='" + status + "'}";
    }
}
