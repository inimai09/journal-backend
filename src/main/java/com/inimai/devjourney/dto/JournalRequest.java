package com.inimai.devjourney.dto;

public class JournalRequest {
    private String title;
    private String content;

    public JournalRequest(){

    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
}
