package com.macinternetservices.sablebusinessdirectory.utils;

import android.app.Application;
import android.content.Context;

import javax.inject.Inject;

/**
 * Sable Business Directory on 1/10/18.
 * Contact Email : admin@sablebusinessdirectory.com
 */


public class PSContext {

    public Context context;

    @Inject
    PSContext(Application App) {
        this.context = App.getApplicationContext();
    }
}
