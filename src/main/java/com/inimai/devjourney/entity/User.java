package com.inimai.devjourney.entity;

import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    
    @Id
    //generatedvalue is used to automatically generate the primary key value for the entity. The strategy attribute specifies the primary key generation strategy to be used. In this case, GenerationType.IDENTITY is used, which means that the database will automatically generate a unique value for the primary key when a new record is inserted into the table.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    //for email
    @Column(unique = true)
    private String email;

    public User(){

    }
    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id = id;
    }
    public String getUsername(){
        return username;
    }
    public void setUsername(String username){
        this.username = username;
    }
    public String getPassword(){
        return password;
    }
    public void setPassword(String password){
        this.password = password;
    }
    public String getEmail(){
        return email;
    }
    public void setEmail(String email){
        this.email = email;
    }
    
    @OneToMany(mappedBy = "user")
    private List<Journal> journals;
}
