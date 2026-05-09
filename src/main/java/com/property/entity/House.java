package com.property.entity;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 房屋实体类
 */
public class House implements Serializable {
    private Integer id;
    private String building;
    private String unit;
    private String roomNumber;
    private BigDecimal area;
    private String houseType;
    private Integer ownerId;
    private String status;

    // 关联字段（用于联查显示）
    private String ownerName;

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getBuilding() { return building; }
    public void setBuilding(String building) { this.building = building; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public BigDecimal getArea() { return area; }
    public void setArea(BigDecimal area) { this.area = area; }

    public String getHouseType() { return houseType; }
    public void setHouseType(String houseType) { this.houseType = houseType; }

    public Integer getOwnerId() { return ownerId; }
    public void setOwnerId(Integer ownerId) { this.ownerId = ownerId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    @Override
    public String toString() {
        return "House{id=" + id + ", building='" + building + "', unit='" + unit +
               "', roomNumber='" + roomNumber + "', area=" + area +
               "', houseType='" + houseType + "', status='" + status + "'}";
    }
}
