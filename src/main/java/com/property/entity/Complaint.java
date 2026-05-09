package com.property.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 投诉实体类
 */
public class Complaint implements Serializable {
    private Integer id;
    private Integer ownerId;
    private String title;
    private String content;
    private Date createTime;
    private String status;

    // 关联字段
    private String ownerName;

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getOwnerId() { return ownerId; }
    public void setOwnerId(Integer ownerId) { this.ownerId = ownerId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    @Override
    public String toString() {
        return "Complaint{id=" + id + ", ownerId=" + ownerId + ", title='" + title +
               "', content='" + content + "', createTime=" + createTime + ", status='" + status + "'}";
    }
}
