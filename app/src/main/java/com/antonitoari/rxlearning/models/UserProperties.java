
package com.antonitoari.rxlearning.models;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserProperties implements Serializable {
    @SerializedName ("url") @Expose private String url;
    @SerializedName ("name") @Expose private String name;

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }
}
