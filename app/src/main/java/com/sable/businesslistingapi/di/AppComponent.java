package com.sable.businesslistingapi.di;

import android.app.Application;

import com.sable.businesslistingapi.PSApp;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;

/**
 * Sable Business Directory on 09/01/2021
 * Contact Email : admin@sablebusinessdirectory.com
 */

@Singleton
@Component(modules = {
        AndroidInjectionModule.class,
        AppModule.class,
        MainActivityModule.class
})
public interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance Builder application(Application application);
        AppComponent build();
    }
    void inject(PSApp MShop);

}
