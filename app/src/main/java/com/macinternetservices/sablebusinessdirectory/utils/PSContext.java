package com.macinternetservices.sablebusinessdirectory.utils;

import android.app.Application;
import android.content.Context;

import javax.inject.Inject;

/**
 * Sable Business Directory on 09/01/2021
 * Contact Email : admin@sablebusinessdirectory.com
 */


public class PSContext {

    public Context context;

    @Inject
    PSContext(Application App) {
        this.context = App.getApplicationContext();
    }
}
