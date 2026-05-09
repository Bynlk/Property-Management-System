package com.property.entity;

import java.io.Serializable;

/**
 * 报修实体类
 */
public class Repair implements Serializable {
    private Integer id;
    private Integer ownerId;
    private String deviceName;
    private String faultDescription;
    private String repairPerson;
    private String status;

    // 关联字段
    private String ownerName;

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getOwnerId() { return ownerId; }
    public void setOwnerId(Integer ownerId) { this.ownerId = ownerId; }

    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }

    public String getFaultDescription() { return faultDescription; }
    public void setFaultDescription(String faultDescription) { this.faultDescription = faultDescription; }

    public String getRepairPerson() { return repairPerson; }
    public void setRepairPerson(String repairPerson) { this.repairPerson = repairPerson; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
}
