package com.macinternetservices.sablebusinessdirectory.viewobject;

import com.google.gson.annotations.SerializedName;

/**
 * Sable Business Directory on 11/17/17.
 * Contact Email : admin@sablebusinessdirectory.com
 */

public class ApiStatus {

    @SerializedName("status")
    public final String status;

    @SerializedName("message")
    public final String message;

    public ApiStatus(String status, String message) {
        this.status = status;
        this.message = message;
    }
}
