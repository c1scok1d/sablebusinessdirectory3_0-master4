package com.sable.businesslistingapi.di;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.sable.businesslistingapi.viewmodel.ItemPaidHistoryViewModel.ItemPaidHistoryViewModel;
import com.sable.businesslistingapi.viewmodel.aboutus.AboutUsViewModel;
import com.sable.businesslistingapi.viewmodel.blog.BlogViewModel;
import com.sable.businesslistingapi.viewmodel.city.CityViewModel;
import com.sable.businesslistingapi.viewmodel.city.FeaturedCitiesViewModel;
import com.sable.businesslistingapi.viewmodel.city.PopularCitiesViewModel;
import com.sable.businesslistingapi.viewmodel.city.RecentCitiesViewModel;
import com.sable.businesslistingapi.viewmodel.clearalldata.ClearAllDataViewModel;
import com.sable.businesslistingapi.viewmodel.comment.CommentDetailListViewModel;
import com.sable.businesslistingapi.viewmodel.comment.CommentListViewModel;
import com.sable.businesslistingapi.viewmodel.common.NotificationViewModel;
import com.sable.businesslistingapi.viewmodel.common.PSNewsViewModelFactory;
import com.sable.businesslistingapi.viewmodel.contactus.ContactUsViewModel;
import com.sable.businesslistingapi.viewmodel.homelist.HomeTrendingCategoryListViewModel;
import com.sable.businesslistingapi.viewmodel.image.ImageViewModel;
import com.sable.businesslistingapi.viewmodel.item.DisabledItemViewModel;
import com.sable.businesslistingapi.viewmodel.item.DiscountItemViewModel;
import com.sable.businesslistingapi.viewmodel.item.FavouriteViewModel;
import com.sable.businesslistingapi.viewmodel.item.FeaturedItemViewModel;
import com.sable.businesslistingapi.viewmodel.item.HistoryViewModel;
import com.sable.businesslistingapi.viewmodel.item.PendingItemViewModel;
import com.sable.businesslistingapi.viewmodel.item.PopularItemViewModel;
import com.sable.businesslistingapi.viewmodel.item.RecentItemViewModel;
import com.sable.businesslistingapi.viewmodel.item.RejectedItemViewModel;
import com.sable.businesslistingapi.viewmodel.item.SpecsViewModel;
import com.sable.businesslistingapi.viewmodel.item.TouchCountViewModel;
import com.sable.businesslistingapi.viewmodel.itemcategory.ItemCategoryViewModel;
import com.sable.businesslistingapi.viewmodel.itemcollection.ItemCollectionViewModel;
import com.sable.businesslistingapi.viewmodel.itemstatus.ItemStatusViewModel;
import com.sable.businesslistingapi.viewmodel.itemsubcategory.ItemSubCategoryViewModel;
import com.sable.businesslistingapi.viewmodel.notification.NotificationsViewModel;
import com.sable.businesslistingapi.viewmodel.apploading.AppLoadingViewModel;
import com.sable.businesslistingapi.viewmodel.paypal.PaypalViewModel;
import com.sable.businesslistingapi.viewmodel.rating.RatingViewModel;
import com.sable.businesslistingapi.viewmodel.user.UserViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

/**
 * Sable Business Directory on 09/01/2021
 * Contact Email : admin@sablebusinessdirectory.com
 */

@Module
abstract class ViewModelModule {

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(PSNewsViewModelFactory factory);

    @Binds
    @IntoMap
    @ViewModelKey(UserViewModel.class)
    abstract ViewModel bindUserViewModel(UserViewModel userViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(AboutUsViewModel.class)
    abstract ViewModel bindAboutUsViewModel(AboutUsViewModel aboutUsViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ImageViewModel.class)
    abstract ViewModel bindImageViewModel(ImageViewModel imageViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(RatingViewModel.class)
    abstract ViewModel bindRatingViewModel(RatingViewModel ratingViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(DisabledItemViewModel.class)
    abstract ViewModel bindDisabledViewModel(DisabledItemViewModel disabledItemViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(RejectedItemViewModel.class)
    abstract ViewModel bindRejectedViewModel(RejectedItemViewModel rejectedItemViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(PendingItemViewModel.class)
    abstract ViewModel bindPendingViewModel(PendingItemViewModel pendingItemViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(NotificationViewModel.class)
    abstract ViewModel bindNotificationViewModel(NotificationViewModel notificationViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ContactUsViewModel.class)
    abstract ViewModel bindContactUsViewModel(ContactUsViewModel contactUsViewModel);

  /*  @Binds
    @IntoMap
    @ViewModelKey(ProductViewModel.class)
    abstract ViewModel bindProductViewModel(ProductViewModel productViewModel);*/

    @Binds
    @IntoMap
    @ViewModelKey(CommentListViewModel.class)
    abstract ViewModel bindCommentViewModel(CommentListViewModel commentListViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(CommentDetailListViewModel.class)
    abstract ViewModel bindCommentDetailViewModel(CommentDetailListViewModel commentDetailListViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(FavouriteViewModel.class)
    abstract ViewModel bindFavouriteViewModel(FavouriteViewModel favouriteViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(TouchCountViewModel.class)
    abstract ViewModel bindTouchCountViewModel(TouchCountViewModel touchCountViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(SpecsViewModel.class)
    abstract ViewModel bindProductSpecsViewModel(SpecsViewModel specsViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(HistoryViewModel.class)
    abstract ViewModel bindHistoryProductViewModel(HistoryViewModel historyViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ItemCategoryViewModel.class)
    abstract ViewModel bindCityCategoryViewModel(ItemCategoryViewModel itemCategoryViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(NotificationsViewModel.class)
    abstract ViewModel bindNotificationListViewModel(NotificationsViewModel notificationListViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(HomeTrendingCategoryListViewModel.class)
    abstract ViewModel bindHomeTrendingCategoryListViewModel(HomeTrendingCategoryListViewModel transactionListViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(BlogViewModel.class)
    abstract ViewModel bindNewsFeedViewModel(BlogViewModel blogViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(AppLoadingViewModel.class)
    abstract ViewModel bindPSAppInfoViewModel(AppLoadingViewModel psAppInfoViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ClearAllDataViewModel.class)
    abstract ViewModel bindClearAllDataViewModel(ClearAllDataViewModel clearAllDataViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(CityViewModel.class)
    abstract ViewModel bindCityViewModel(CityViewModel cityViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(com.sable.businesslistingapi.viewmodel.item.ItemViewModel.class)
    abstract ViewModel bindItemViewModel(com.sable.businesslistingapi.viewmodel.item.ItemViewModel itemViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(DiscountItemViewModel.class)
    abstract ViewModel bindDiscountItemViewModel(DiscountItemViewModel discountItemViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(FeaturedItemViewModel.class)
    abstract ViewModel bindFeaturedItemViewModel(FeaturedItemViewModel featuredItemViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(PopularItemViewModel.class)
    abstract ViewModel bindPopularItemViewModel(PopularItemViewModel popularItemViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(RecentItemViewModel.class)
    abstract ViewModel bindRecentItemViewModel(RecentItemViewModel recentItemViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(PopularCitiesViewModel.class)
    abstract ViewModel bindPopularCitiesViewModel(PopularCitiesViewModel popularCitiesViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(FeaturedCitiesViewModel.class)
    abstract ViewModel bindFeaturedCitiesViewModel(FeaturedCitiesViewModel featuredCitiesViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(RecentCitiesViewModel.class)
    abstract ViewModel bindRecentCitiesViewModel(RecentCitiesViewModel recentCitiesViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ItemCollectionViewModel.class)
    abstract ViewModel bindItemCollectionViewModel(ItemCollectionViewModel itemCollectionViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ItemSubCategoryViewModel.class)
    abstract ViewModel bindItemSubCategoryViewModel(ItemSubCategoryViewModel itemSubCategoryViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ItemStatusViewModel.class)
    abstract ViewModel bindItemStatusViewModel(ItemStatusViewModel itemStatusViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(PaypalViewModel.class)
    abstract ViewModel bindPaypalViewModel(PaypalViewModel paypalViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ItemPaidHistoryViewModel.class)
    abstract ViewModel bindItemPaidHistoryViewModel(ItemPaidHistoryViewModel itemPaidHistoryViewModel);
}


