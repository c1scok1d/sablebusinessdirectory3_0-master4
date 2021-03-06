package com.sable.businesslistingapi.viewobject;

import com.google.gson.annotations.SerializedName;

/**
 * Sable Business Directory on 09/01/2021
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
