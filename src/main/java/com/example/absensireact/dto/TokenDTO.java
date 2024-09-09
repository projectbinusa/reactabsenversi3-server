package com.example.absensireact.dto;


import com.example.absensireact.model.UserModel;

import javax.persistence.*;
import java.util.Date;

public class TokenDTO {
    private Long id;
    private String token;

    private UserModel user;
    private Date created;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
