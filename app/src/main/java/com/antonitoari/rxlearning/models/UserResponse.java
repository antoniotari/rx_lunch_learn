
package com.antonitoari.rxlearning.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserResponse {

    @SerializedName("properties") @Expose
    private UserProperties properties;

    public UserProperties getProperties() {
        return properties;
    }
}
