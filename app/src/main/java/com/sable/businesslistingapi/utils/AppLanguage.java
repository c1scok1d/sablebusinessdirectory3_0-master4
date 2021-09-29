package com.sable.businesslistingapi.utils;

import android.content.SharedPreferences;

import com.sable.businesslistingapi.Config;

import javax.inject.Inject;

/**
 * Sable Business Directory on 09/01/2021
 * Contact Email : admin@sablebusinessdirectory.com
 */

//@Singleton
public class AppLanguage {

    private String currentLanguage;
    private SharedPreferences sharedPreferences;

    @Inject
    public AppLanguage(SharedPreferences sharedPreferences) {
        if(sharedPreferences != null) {
            this.sharedPreferences = sharedPreferences;
            try {
                this.currentLanguage = sharedPreferences.getString("Language", Config.DEFAULT_LANGUAGE);
            } catch (Exception e) {
                this.currentLanguage = Config.DEFAULT_LANGUAGE;
            }
        }
    }

    public AppLanguage() {

    }

    public String getLanguage(boolean isLatest) {

        if(isLatest) {
            try {
                this.currentLanguage = sharedPreferences.getString("Language", Config.DEFAULT_LANGUAGE);
            } catch (Exception e) {
                this.currentLanguage = Config.DEFAULT_LANGUAGE;
            }
        }
        return currentLanguage;
    }
}
