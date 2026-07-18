package com.inimai.devjourney.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class JournalRequest {
    @NotBlank(message = "title cannot be blank")
    @Size(min = 3, max = 100, message = "title must be between 3 and 100 characters")
    private String title;
    @NotBlank(message = "content cannot be blank")
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
