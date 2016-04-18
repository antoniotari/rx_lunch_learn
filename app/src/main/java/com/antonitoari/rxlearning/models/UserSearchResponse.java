package com.antonitoari.rxlearning.models;

import java.io.Serializable;
import java.util.List;

public class UserSearchResponse implements Serializable{
    private List<UserResponse> entities;

    public List<UserResponse> getEntities() {
        return entities;
    }
}
