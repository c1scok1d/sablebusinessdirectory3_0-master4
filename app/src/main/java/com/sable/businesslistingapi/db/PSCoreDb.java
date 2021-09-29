package com.sable.businesslistingapi.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.sable.businesslistingapi.db.common.Converters;
import com.sable.businesslistingapi.viewobject.AboutUs;
import com.sable.businesslistingapi.viewobject.Blog;
import com.sable.businesslistingapi.viewobject.City;
import com.sable.businesslistingapi.viewobject.CityMap;
import com.sable.businesslistingapi.viewobject.Comment;
import com.sable.businesslistingapi.viewobject.CommentDetail;
import com.sable.businesslistingapi.viewobject.DeletedObject;
import com.sable.businesslistingapi.viewobject.Image;
import com.sable.businesslistingapi.viewobject.Item;
import com.sable.businesslistingapi.viewobject.ItemCategory;
import com.sable.businesslistingapi.viewobject.ItemCollection;
import com.sable.businesslistingapi.viewobject.ItemCollectionHeader;
import com.sable.businesslistingapi.viewobject.ItemFavourite;
import com.sable.businesslistingapi.viewobject.ItemHistory;
import com.sable.businesslistingapi.viewobject.ItemMap;
import com.sable.businesslistingapi.viewobject.ItemPaidHistory;
import com.sable.businesslistingapi.viewobject.ItemSpecs;
import com.sable.businesslistingapi.viewobject.ItemStatus;
import com.sable.businesslistingapi.viewobject.ItemSubCategory;
import com.sable.businesslistingapi.viewobject.Noti;
import com.sable.businesslistingapi.viewobject.PSAppInfo;
import com.sable.businesslistingapi.viewobject.PSAppVersion;
import com.sable.businesslistingapi.viewobject.Rating;
import com.sable.businesslistingapi.viewobject.User;
import com.sable.businesslistingapi.viewobject.UserLogin;


/**
 * Sable Business Directory on 09/01/2021
 * Contact Email : admin@sablebusinessdirectory.com
 */

@Database(entities = {
        Image.class,
        User.class,
        UserLogin.class,
        AboutUs.class,
        ItemFavourite.class,
        Comment.class,
        CommentDetail.class,
        Noti.class,
        ItemHistory.class,
        Blog.class,
        Rating.class,
        PSAppInfo.class,
        PSAppVersion.class,
        DeletedObject.class,
        City.class,
        CityMap.class,
        Item.class,
        ItemMap.class,
        ItemCategory.class,
        ItemCollectionHeader.class,
        ItemCollection.class,
        ItemSubCategory.class,
        ItemSpecs.class,
        ItemStatus.class,
        ItemPaidHistory.class

}, version = 1, exportSchema = false)
//3.0 = 1



@TypeConverters({Converters.class})

public abstract class PSCoreDb extends RoomDatabase {

    abstract public UserDao userDao();

    abstract public HistoryDao historyDao();

    abstract public SpecsDao specsDao();

    abstract public AboutUsDao aboutUsDao();

    abstract public ImageDao imageDao();

    abstract public RatingDao ratingDao();

    abstract public CommentDao commentDao();

    abstract public CommentDetailDao commentDetailDao();

    abstract public NotificationDao notificationDao();

    abstract public BlogDao blogDao();

    abstract public PSAppInfoDao psAppInfoDao();

    abstract public PSAppVersionDao psAppVersionDao();

    abstract public DeletedObjectDao deletedObjectDao();

    abstract public CityDao cityDao();

    abstract public CityMapDao cityMapDao();

    abstract public ItemDao itemDao();

    abstract public ItemMapDao itemMapDao();

    abstract public ItemCategoryDao itemCategoryDao();

    abstract public ItemCollectionHeaderDao itemCollectionHeaderDao();

    abstract public ItemSubCategoryDao itemSubCategoryDao();

    abstract public ItemStatusDao itemStatusDao();

    abstract public ItemPaidHistoryDao itemPaidHistoryDao();
//    /**
//     * Migrate from:
//     * version 1 - using Room
//     * to
//     * version 2 - using Room where the {@link } has an extra field: addedDateStr
//     */
//    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
//        @Override
//        public void migrate(@NonNull SupportSQLiteDatabase database) {
//            database.execSQL("ALTER TABLE news "
//                    + " ADD COLUMN addedDateStr INTEGER NOT NULL DEFAULT 0");
//        }
//    };

    /* More migration write here */
}