package com.property.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.util.Date;

/**
 * 业主实体类
 */
public class Owner implements Serializable {
    private Integer id;
    private String name;
    private String gender;
    private String phone;
    private String idCard;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Shanghai")
    private Date moveInDate;

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getIdCard() { return idCard; }
    public void setIdCard(String idCard) { this.idCard = idCard; }

    public Date getMoveInDate() { return moveInDate; }
    public void setMoveInDate(Date moveInDate) { this.moveInDate = moveInDate; }

    @Override
    public String toString() {
        return "Owner{id=" + id + ", name='" + name + "', gender='" + gender +
               "', phone='" + phone + "', idCard='" + idCard + "', moveInDate=" + moveInDate + "}";
    }
}
