package com.property.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.property.common.IdCardMaskSerializer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 业主实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Owner implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;

    @NotBlank(message = "姓名不能为空")
    @Size(max = 50, message = "姓名长度不能超过50")
    private String name;

    @Pattern(regexp = "^$|^男$|^女$", message = "性别只能为男或女")
    private String gender;

    @Pattern(regexp = "^$|^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Pattern(regexp = "^$|^\\d{17}[\\dXx]$", message = "身份证号格式不正确")
    @JsonSerialize(using = IdCardMaskSerializer.class)
    private String idCard;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Shanghai")
    private LocalDate moveInDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime updatedAt;

    @Override
    public String toString() {
        String maskedIdCard = (idCard != null && idCard.length() > 6)
                ? idCard.substring(0, 3) + "***" + idCard.substring(idCard.length() - 4)
                : "***";
        return "Owner{id=" + id + ", name='" + name + "', gender='" + gender +
               "', phone='" + phone + "', idCard='" + maskedIdCard + "'}";
    }
}
