package com.agritrace.backend.dto.portal;

import java.time.LocalDateTime;

// 仅包含公开可展示的信息，不暴露管理员ID、私有文件路径等字段。
public class PublicNotice {
    private Long id;
    private String title;
    private String content;
    private String noticeType;
    private boolean top;
    private LocalDateTime publishedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getNoticeType() { return noticeType; }
    public void setNoticeType(String noticeType) { this.noticeType = noticeType; }
    public boolean isTop() { return top; }
    public void setTop(boolean top) { this.top = top; }
    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }
}
