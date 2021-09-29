package com.sable.businesslistingapi.di;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.room.Room;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sable.businesslistingapi.Config;
import com.sable.businesslistingapi.api.PSApiService;
import com.sable.businesslistingapi.db.AboutUsDao;
import com.sable.businesslistingapi.db.BlogDao;
import com.sable.businesslistingapi.db.CityDao;
import com.sable.businesslistingapi.db.CityMapDao;
import com.sable.businesslistingapi.db.CommentDao;
import com.sable.businesslistingapi.db.CommentDetailDao;
import com.sable.businesslistingapi.db.DeletedObjectDao;
import com.sable.businesslistingapi.db.HistoryDao;
import com.sable.businesslistingapi.db.ImageDao;
import com.sable.businesslistingapi.db.ItemCategoryDao;
import com.sable.businesslistingapi.db.ItemCollectionHeaderDao;
import com.sable.businesslistingapi.db.ItemDao;
import com.sable.businesslistingapi.db.ItemMapDao;
import com.sable.businesslistingapi.db.ItemPaidHistoryDao;
import com.sable.businesslistingapi.db.ItemStatusDao;
import com.sable.businesslistingapi.db.ItemSubCategoryDao;
import com.sable.businesslistingapi.db.NotificationDao;
import com.sable.businesslistingapi.db.PSAppInfoDao;
import com.sable.businesslistingapi.db.PSAppVersionDao;
import com.sable.businesslistingapi.db.PSCoreDb;
import com.sable.businesslistingapi.db.RatingDao;
import com.sable.businesslistingapi.db.UserDao;
import com.sable.businesslistingapi.utils.AppLanguage;
import com.sable.businesslistingapi.utils.Connectivity;
import com.sable.businesslistingapi.utils.LiveDataCallAdapterFactory;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Sable Business Directory on 09/01/2021
 * Contact Email : admin@sablebusinessdirectory.com
 */

@Module(includes = ViewModelModule.class)
class AppModule {

    @Singleton
    @Provides
    PSApiService providePSApiService() {

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .writeTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .build();

        return new Retrofit.Builder()
                .baseUrl(Config.APP_API_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .build()
                .create(PSApiService.class);

    }

    @Singleton
    @Provides
    PSCoreDb provideDb(Application app) {
        return Room.databaseBuilder(app, PSCoreDb.class, "psmulticity.db")
                //.addMigrations(MIGRATION_1_2)
                .fallbackToDestructiveMigration()
                .build();
    }

    @Singleton
    @Provides
    Connectivity provideConnectivity(Application app) {
        return new Connectivity(app);
    }

    @Singleton
    @Provides
    SharedPreferences provideSharedPreferences(Application app) {
        return PreferenceManager.getDefaultSharedPreferences(app.getApplicationContext());
    }

    @Singleton
    @Provides
    UserDao provideUserDao(PSCoreDb db) {
        return db.userDao();
    }

    @Singleton
    @Provides
    AppLanguage provideCurrentLanguage(SharedPreferences sharedPreferences) {
        return new AppLanguage(sharedPreferences);
    }

    @Singleton
    @Provides
    AboutUsDao provideAboutUsDao(PSCoreDb db) {
        return db.aboutUsDao();
    }

    @Singleton
    @Provides
    ImageDao provideImageDao(PSCoreDb db) {
        return db.imageDao();
    }

    @Singleton
    @Provides
    HistoryDao provideHistoryDao(PSCoreDb db) {
        return db.historyDao();
    }

    @Singleton
    @Provides
    RatingDao provideRatingDao(PSCoreDb db) {
        return db.ratingDao();
    }

    @Singleton
    @Provides
    CommentDao provideCommentDao(PSCoreDb db) {
        return db.commentDao();
    }

    @Singleton
    @Provides
    CommentDetailDao provideCommentDetailDao(PSCoreDb db) {
        return db.commentDetailDao();
    }

    @Singleton
    @Provides
    NotificationDao provideNotificationDao(PSCoreDb db){return db.notificationDao();}

    @Singleton
    @Provides
    BlogDao provideNewsFeedDao(PSCoreDb db){return db.blogDao();}

    @Singleton
    @Provides
    PSAppInfoDao providePSAppInfoDao(PSCoreDb db){return db.psAppInfoDao();}

    @Singleton
    @Provides
    PSAppVersionDao providePSAppVersionDao(PSCoreDb db){return db.psAppVersionDao();}

    @Singleton
    @Provides
    DeletedObjectDao provideDeletedObjectDao(PSCoreDb db){return db.deletedObjectDao();}

    @Singleton
    @Provides
    CityDao provideCityDao(PSCoreDb db){return db.cityDao();}

    @Singleton
    @Provides
    CityMapDao provideCityMapDao(PSCoreDb db){return db.cityMapDao();}

    @Singleton
    @Provides
    ItemDao provideItemDao(PSCoreDb db){return db.itemDao();}

    @Singleton
    @Provides
    ItemMapDao provideItemMapDao(PSCoreDb db){return db.itemMapDao();}

    @Singleton
    @Provides
    ItemCategoryDao provideCityCategoryDao(PSCoreDb db){return db.itemCategoryDao();}

    @Singleton
    @Provides
    ItemCollectionHeaderDao provideItemCollectionHeaderDao(PSCoreDb db){return db.itemCollectionHeaderDao();}

    @Singleton
    @Provides
    ItemSubCategoryDao provideItemSubCategoryDao(PSCoreDb db){return db.itemSubCategoryDao();}

    @Singleton
    @Provides
    ItemStatusDao provideItemStatusDao(PSCoreDb db){return db.itemStatusDao();}

    @Singleton
    @Provides
    ItemPaidHistoryDao provideItemPaidHistoryDao(PSCoreDb db){return db.itemPaidHistoryDao();}
}
