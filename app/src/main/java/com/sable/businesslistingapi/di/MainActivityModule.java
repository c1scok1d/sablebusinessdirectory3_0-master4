package com.sable.businesslistingapi.di;


import com.sable.businesslistingapi.MainActivity;
import com.sable.businesslistingapi.ui.apploading.AppLoadingActivity;
import com.sable.businesslistingapi.ui.apploading.AppLoadingFragment;
import com.sable.businesslistingapi.ui.blog.detail.BlogDetailActivity;
import com.sable.businesslistingapi.ui.blog.detail.BlogDetailFragment;
import com.sable.businesslistingapi.ui.blog.list.BlogListActivity;
import com.sable.businesslistingapi.ui.blog.list.BlogListFragment;
import com.sable.businesslistingapi.ui.blog.listbycityid.BlogListByCityIdActivity;
import com.sable.businesslistingapi.ui.blog.listbycityid.BlogListByCityIdFragment;
import com.sable.businesslistingapi.ui.category.categoryselection.CategorySelectionFragment;
import com.sable.businesslistingapi.ui.category.list.CategoryListActivity;
import com.sable.businesslistingapi.ui.category.list.CategoryListFragment;
import com.sable.businesslistingapi.ui.city.cityList.CityListActivity;
import com.sable.businesslistingapi.ui.city.cityList.CityListFragment;
import com.sable.businesslistingapi.ui.city.cityList.ItemUploadCityListFragment;
import com.sable.businesslistingapi.ui.city.detail.CityActivity;
import com.sable.businesslistingapi.ui.city.detail.CityFragment;
import com.sable.businesslistingapi.ui.city.menu.CityMenuFragment;
import com.sable.businesslistingapi.ui.city.selectedcity.SelectedCityActivity;
import com.sable.businesslistingapi.ui.city.selectedcity.SelectedCityFragment;
import com.sable.businesslistingapi.ui.imageupload.ImageUploadActivity;
import com.sable.businesslistingapi.ui.imageupload.ImageUploadFragment;
import com.sable.businesslistingapi.ui.imageupload.ItemEntryImageUploadFragment;
import com.sable.businesslistingapi.ui.imageupload.itemimagelist.ItemImageListActivity;
import com.sable.businesslistingapi.ui.imageupload.itemimagelist.ItemImageListFragment;
import com.sable.businesslistingapi.ui.item.collection.header.CollectionHeaderListActivity;
import com.sable.businesslistingapi.ui.item.collection.header.CollectionHeaderListFragment;
import com.sable.businesslistingapi.ui.comment.detail.CommentDetailActivity;
import com.sable.businesslistingapi.ui.comment.detail.CommentDetailFragment;
import com.sable.businesslistingapi.ui.comment.list.CommentListActivity;
import com.sable.businesslistingapi.ui.comment.list.CommentListFragment;
import com.sable.businesslistingapi.ui.contactus.ContactUsFragment;
import com.sable.businesslistingapi.ui.dashboard.DashBoardCityListFragment;
import com.sable.businesslistingapi.ui.dashboard.DashBoardSearchCategoryFragment;
import com.sable.businesslistingapi.ui.dashboard.DashBoardSearchFragment;
import com.sable.businesslistingapi.ui.dashboard.DashBoardSearchSubCategoryFragment;
import com.sable.businesslistingapi.ui.dashboard.DashboardSearchByCategoryActivity;
import com.sable.businesslistingapi.ui.forceupdate.ForceUpdateActivity;
import com.sable.businesslistingapi.ui.forceupdate.ForceUpdateFragment;
import com.sable.businesslistingapi.ui.gallery.GalleryActivity;
import com.sable.businesslistingapi.ui.gallery.GalleryFragment;
import com.sable.businesslistingapi.ui.gallery.detail.GalleryDetailActivity;
import com.sable.businesslistingapi.ui.gallery.detail.GalleryDetailFragment;
import com.sable.businesslistingapi.ui.item.detail.ItemActivity;
import com.sable.businesslistingapi.ui.item.detail.ItemFragment;
import com.sable.businesslistingapi.ui.item.map.MapActivity;
import com.sable.businesslistingapi.ui.item.map.MapFragment;
import com.sable.businesslistingapi.ui.item.promote.ItemPromoteActivity;
import com.sable.businesslistingapi.ui.item.promote.ItemPromoteFragment;
import com.sable.businesslistingapi.ui.item.promote.LoginUserPaidItemFragment;
import com.sable.businesslistingapi.ui.item.search.searchlist.SearchListActivity;
import com.sable.businesslistingapi.ui.item.search.searchlist.SearchListFragment;
import com.sable.businesslistingapi.ui.category.categoryfilter.CategoryFilterFragment;
import com.sable.businesslistingapi.ui.item.map.mapFilter.MapFilteringActivity;
import com.sable.businesslistingapi.ui.item.map.mapFilter.MapFilteringFragment;
import com.sable.businesslistingapi.ui.item.search.specialfilterbyattributes.FilteringActivity;
import com.sable.businesslistingapi.ui.item.search.specialfilterbyattributes.FilteringFragment;
import com.sable.businesslistingapi.ui.item.collection.itemCollection.ItemCollectionActivity;
import com.sable.businesslistingapi.ui.item.collection.itemCollection.ItemCollectionFragment;
import com.sable.businesslistingapi.ui.item.upload.ItemUploadActivity;
import com.sable.businesslistingapi.ui.item.upload.ItemUploadFragment;
import com.sable.businesslistingapi.ui.item.upload.SelectionActivity;
import com.sable.businesslistingapi.ui.item.upload.map.MapItemEntryActivity;
import com.sable.businesslistingapi.ui.item.uploaded.ItemUploadedListFragment;
import com.sable.businesslistingapi.ui.item.user_item.LoginUserItemFragment;
import com.sable.businesslistingapi.ui.item.user_item.LoginUserItemListActivity;
import com.sable.businesslistingapi.ui.language.LanguageFragment;
import com.sable.businesslistingapi.ui.notification.detail.NotificationActivity;
import com.sable.businesslistingapi.ui.notification.detail.NotificationFragment;
import com.sable.businesslistingapi.ui.notification.list.NotificationListActivity;
import com.sable.businesslistingapi.ui.notification.list.NotificationListFragment;
import com.sable.businesslistingapi.ui.notification.setting.NotificationSettingFragment;
import com.sable.businesslistingapi.ui.item.favourite.FavouriteListActivity;
import com.sable.businesslistingapi.ui.item.favourite.FavouriteListFragment;
import com.sable.businesslistingapi.ui.item.history.HistoryFragment;
import com.sable.businesslistingapi.ui.item.history.UserHistoryListActivity;
import com.sable.businesslistingapi.ui.item.rating.RatingListActivity;
import com.sable.businesslistingapi.ui.item.rating.RatingListFragment;
import com.sable.businesslistingapi.ui.item.readmore.ReadMoreActivity;
import com.sable.businesslistingapi.ui.item.readmore.ReadMoreFragment;
import com.sable.businesslistingapi.ui.privacypolicy.PrivacyPolicyActivity;
import com.sable.businesslistingapi.ui.privacypolicy.PrivacyPolicyFragment;
import com.sable.businesslistingapi.ui.setting.appinfo.AppInfoActivity;
import com.sable.businesslistingapi.ui.setting.appinfo.AppInfoFragment;
import com.sable.businesslistingapi.ui.notification.setting.NotificationSettingActivity;
import com.sable.businesslistingapi.ui.setting.SettingActivity;
import com.sable.businesslistingapi.ui.setting.SettingFragment;
import com.sable.businesslistingapi.ui.specification.SpecificationListActivity;
import com.sable.businesslistingapi.ui.specification.SpecificationListFragment;
import com.sable.businesslistingapi.ui.specification.addspecification.AddAndEditSpecificationActivity;
import com.sable.businesslistingapi.ui.specification.addspecification.AddAndEditSpecificationFragment;
import com.sable.businesslistingapi.ui.status.StatusSelectionFragment;
import com.sable.businesslistingapi.ui.stripe.StripeActivity;
import com.sable.businesslistingapi.ui.stripe.StripeFragment;
import com.sable.businesslistingapi.ui.subcategory.SubCategoryActivity;
import com.sable.businesslistingapi.ui.subcategory.SubCategoryFragment;
import com.sable.businesslistingapi.ui.subcategory.subcategoryselection.SubCategorySelectionFragment;
import com.sable.businesslistingapi.ui.terms.TermsAndConditionsActivity;
import com.sable.businesslistingapi.ui.terms.TermsAndConditionsFragment;
import com.sable.businesslistingapi.ui.user.PasswordChangeActivity;
import com.sable.businesslistingapi.ui.user.PasswordChangeFragment;
import com.sable.businesslistingapi.ui.user.ProfileEditActivity;
import com.sable.businesslistingapi.ui.user.ProfileEditFragment;
import com.sable.businesslistingapi.ui.user.ProfileFragment;
import com.sable.businesslistingapi.ui.user.phonelogin.PhoneLoginActivity;
import com.sable.businesslistingapi.ui.user.phonelogin.PhoneLoginFragment;
import com.sable.businesslistingapi.ui.user.verifyemail.VerifyEmailActivity;
import com.sable.businesslistingapi.ui.user.verifyemail.VerifyEmailFragment;
import com.sable.businesslistingapi.ui.user.UserForgotPasswordActivity;
import com.sable.businesslistingapi.ui.user.UserForgotPasswordFragment;
import com.sable.businesslistingapi.ui.user.UserLoginActivity;
import com.sable.businesslistingapi.ui.user.UserLoginFragment;
import com.sable.businesslistingapi.ui.user.UserRegisterActivity;
import com.sable.businesslistingapi.ui.user.UserRegisterFragment;
import com.sable.businesslistingapi.ui.user.verifyphone.VerifyMobileActivity;
import com.sable.businesslistingapi.ui.user.verifyphone.VerifyMobileFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Sable Business Directory on 09/01/2021
 * Contact Email : admin@sablebusinessdirectory.com
 */


@Module
abstract class MainActivityModule {

    @ContributesAndroidInjector(modules = MainModule.class)
    abstract MainActivity contributeMainActivity();

    @ContributesAndroidInjector(modules = FavouriteListModule.class)
    abstract FavouriteListActivity contributeFavouriteListActivity();

    @ContributesAndroidInjector(modules = PrivacyPolicyActivityModule.class)
    abstract PrivacyPolicyActivity contributePrivacyPolicyActivity();

//    @ContributesAndroidInjector(modules = UserHistoryModule.class)
//    abstract UserHistoryListActivity contributeUserHistoryListActivity();

    @ContributesAndroidInjector(modules = UserRegisterModule.class)
    abstract UserRegisterActivity contributeUserRegisterActivity();

    @ContributesAndroidInjector(modules = UserFBRegisterModule.class)
    abstract VerifyEmailActivity contributeUserFBRegisterActivity();

    @ContributesAndroidInjector(modules = UserForgotPasswordModule.class)
    abstract UserForgotPasswordActivity contributeUserForgotPasswordActivity();

    @ContributesAndroidInjector(modules = UserLoginModule.class)
    abstract UserLoginActivity contributeUserLoginActivity();

    @ContributesAndroidInjector(modules = PasswordChangeModule.class)
    abstract PasswordChangeActivity contributePasswordChangeActivity();

    @ContributesAndroidInjector(modules = FilteringModule.class)
    abstract FilteringActivity filteringActivity();

    @ContributesAndroidInjector(modules = SubCategoryActivityModule.class)
    abstract SubCategoryActivity subCategoryActivity();

    @ContributesAndroidInjector(modules = CommentDetailModule.class)
    abstract CommentDetailActivity commentDetailActivity();

    @ContributesAndroidInjector(modules = NotificationModule.class)
    abstract NotificationListActivity notificationActivity();

    @ContributesAndroidInjector(modules = SearchActivityModule.class)
    abstract SearchListActivity contributeSearchListActivity();

    @ContributesAndroidInjector(modules = NotificationDetailModule.class)
    abstract NotificationActivity notificationDetailActivity();

    @ContributesAndroidInjector(modules = ItemActivityModule.class)
    abstract ItemActivity itemActivity();

    @ContributesAndroidInjector(modules = CommentListActivityModule.class)
    abstract CommentListActivity commentListActivity();

    @ContributesAndroidInjector(modules = LoginUserItemListActivityModule.class)
    abstract LoginUserItemListActivity loginUserItemListActivity();

    @ContributesAndroidInjector(modules = GalleryDetailActivityModule.class)
    abstract GalleryDetailActivity galleryDetailActivity();

    @ContributesAndroidInjector(modules = GalleryActivityModule.class)
    abstract GalleryActivity galleryActivity();

    @ContributesAndroidInjector(modules = SearchByCategoryActivityModule.class)
    abstract DashboardSearchByCategoryActivity searchByCategoryActivity();

    @ContributesAndroidInjector(modules = readMoreActivityModule.class)
    abstract ReadMoreActivity readMoreActivity();

    @ContributesAndroidInjector(modules = EditSettingModule.class)
    abstract SettingActivity editSettingActivity();

    @ContributesAndroidInjector(modules = LanguageChangeModule.class)
    abstract NotificationSettingActivity languageChangeActivity();

    @ContributesAndroidInjector(modules = ProfileEditModule.class)
    abstract ProfileEditActivity contributeProfileEditActivity();

    @ContributesAndroidInjector(modules = AppInfoModule.class)
    abstract AppInfoActivity AppInfoActivity();

    @ContributesAndroidInjector(modules = ProductCollectionModule.class)
    abstract CollectionHeaderListActivity productCollectionHeaderListActivity();

    @ContributesAndroidInjector(modules = CategoryListActivityAppInfoModule.class)
    abstract CategoryListActivity categoryListActivity();

    @ContributesAndroidInjector(modules = RatingListActivityModule.class)
    abstract RatingListActivity ratingListActivity();

    @ContributesAndroidInjector(modules = SelectedCityModule.class)
    abstract SelectedCityActivity selectedShopActivity();

    @ContributesAndroidInjector(modules = CollectionModule.class)
    abstract ItemCollectionActivity collectionActivity();

    @ContributesAndroidInjector(modules = SelectedShopListBlogModule.class)
    abstract BlogListActivity selectedShopListBlogActivity();

    @ContributesAndroidInjector(modules = BlogDetailModule.class)
    abstract BlogDetailActivity blogDetailActivity();

    @ContributesAndroidInjector(modules = MapActivityModule.class)
    abstract MapActivity mapActivity();

    @ContributesAndroidInjector(modules = CityActivityModule.class)
    abstract CityActivity cityActivity();

    @ContributesAndroidInjector(modules = forceUpdateModule.class)
    abstract ForceUpdateActivity forceUpdateActivity();

    @ContributesAndroidInjector(modules = blogListByShopIdActivityModule.class)
    abstract BlogListByCityIdActivity forceBlogListByShopIdActivity();

    @ContributesAndroidInjector(modules = CityListModule.class)
    abstract CityListActivity CityListActivity();

    @ContributesAndroidInjector(modules = MapFilteringModule.class)
    abstract MapFilteringActivity mapFilteringActivity();

    @ContributesAndroidInjector(modules = AppLoadingModule.class)
    abstract AppLoadingActivity appLoadingActivity();

    @ContributesAndroidInjector(modules = SelectionActivityModule.class)
    abstract SelectionActivity selectionActivity();

    @ContributesAndroidInjector(modules = ImageUploadModule.class)
    abstract ImageUploadActivity contributeImageUploadActivity();

    @ContributesAndroidInjector(modules = SpecificationListModule.class)
    abstract SpecificationListActivity contributeSpecificationListActivity();

    @ContributesAndroidInjector(modules = AddAndEditSpecificationModule.class)
    abstract AddAndEditSpecificationActivity attributeHeaderAddActivity();

    @ContributesAndroidInjector(modules =ItemImageListModule.class)
    abstract ItemImageListActivity itemImageListActivity();

    @ContributesAndroidInjector(modules = MapItemEntryModule.class)
    abstract MapItemEntryActivity contributeMapItemEntryActivity();

    @ContributesAndroidInjector(modules = ItemUploadActivityModule.class)
    abstract ItemUploadActivity itemUploadActivity();

    @ContributesAndroidInjector(modules = PhoneLoginActivityModule.class)
    abstract PhoneLoginActivity contributePhoneLoginActivity();

    @ContributesAndroidInjector(modules = VerifyMobileModule.class)
    abstract VerifyMobileActivity contributeVerifyMobileActivity();

    @ContributesAndroidInjector(modules = StripeeModule.class)
    abstract StripeActivity stripeActivity();

    @ContributesAndroidInjector(modules = ItemPromoteEntryActivityModule.class)
    abstract ItemPromoteActivity contributeItemPromoteEntryActivity();

    @ContributesAndroidInjector(modules = LoginUserHistoryListActivityModule.class)
    abstract UserHistoryListActivity userHistoryListActivity();

    @ContributesAndroidInjector(modules = TermsandConditionsActivityModule.class)
    abstract TermsAndConditionsActivity termsAndConditionsActivity();
}


@Module
abstract class MainModule {

    @ContributesAndroidInjector
    abstract ContactUsFragment contributeContactUsFragment();

    @ContributesAndroidInjector
    abstract UserLoginFragment contributeUserLoginFragment();

    @ContributesAndroidInjector
    abstract UserForgotPasswordFragment contributeUserForgotPasswordFragment();

    @ContributesAndroidInjector
    abstract UserRegisterFragment contributeUserRegisterFragment();

    @ContributesAndroidInjector
    abstract PrivacyPolicyFragment contributePrivacyPolicyFragment();

    @ContributesAndroidInjector
    abstract VerifyEmailFragment contributeUserFBRegisterFragment();

    @ContributesAndroidInjector
    abstract NotificationSettingFragment contributeNotificationSettingFragment();

    @ContributesAndroidInjector
    abstract ProfileFragment contributeProfileFragment();

    @ContributesAndroidInjector
    abstract LanguageFragment contributeLanguageFragment();

    @ContributesAndroidInjector
    abstract FavouriteListFragment contributeFavouriteListFragment();

    @ContributesAndroidInjector
    abstract SettingFragment contributEditSettingFragment();

    @ContributesAndroidInjector
    abstract HistoryFragment historyFragment();

    @ContributesAndroidInjector
    abstract NotificationListFragment contributeNotificationFragment();


    @ContributesAndroidInjector
    abstract AppInfoFragment contributeAppInfoFragment();

    @ContributesAndroidInjector
    abstract DashBoardCityListFragment contributeShopListFragment();

    @ContributesAndroidInjector
    abstract CityListFragment contributeCityListFragment();

    @ContributesAndroidInjector
    abstract ItemUploadedListFragment contributeItemUploadedFragment();

    @ContributesAndroidInjector
    abstract PhoneLoginFragment contributePhoneLoginFragment();

    @ContributesAndroidInjector
    abstract VerifyMobileFragment contributeVerifyMobileFragment();

    @ContributesAndroidInjector
    abstract LoginUserPaidItemFragment contributeLoginUserPaidItemFragment();
}

@Module
abstract class ImageUploadModule {
    @ContributesAndroidInjector
    abstract ImageUploadFragment contributeImageUploadFragment();

    @ContributesAndroidInjector
    abstract ItemEntryImageUploadFragment contributeItemEntryImageUploadFragment();
}

@Module
abstract class SelectionActivityModule {

    @ContributesAndroidInjector
    abstract CategorySelectionFragment contributeCategoryExpFragment();

    @ContributesAndroidInjector
    abstract SubCategorySelectionFragment contributeSubCategoryExpFragment();

    @ContributesAndroidInjector
    abstract StatusSelectionFragment contributeStatusSelectionExpFragment();

    @ContributesAndroidInjector
    abstract ItemUploadCityListFragment contributeItemUploadCityListFragment();
}

@Module
abstract class ProfileEditModule {
    @ContributesAndroidInjector
    abstract ProfileEditFragment contributeProfileEditFragment();
}

@Module
abstract class UserFBRegisterModule {
    @ContributesAndroidInjector
    abstract VerifyEmailFragment contributeUserFBRegisterFragment();
}

@Module
abstract class ItemActivityModule {
    @ContributesAndroidInjector
    abstract ItemFragment contributeItemFragment();
}

@Module
abstract class FavouriteListModule {
    @ContributesAndroidInjector
    abstract FavouriteListFragment contributeFavouriteFragment();
}

@Module
abstract class PrivacyPolicyActivityModule {
    @ContributesAndroidInjector
    abstract PrivacyPolicyFragment contributePrivacyPolicyFragment();
}

@Module
abstract class UserRegisterModule {
    @ContributesAndroidInjector
    abstract UserRegisterFragment contributeUserRegisterFragment();
}

@Module
abstract class UserForgotPasswordModule {
    @ContributesAndroidInjector
    abstract UserForgotPasswordFragment contributeUserForgotPasswordFragment();
}

@Module
abstract class UserLoginModule {
    @ContributesAndroidInjector
    abstract UserLoginFragment contributeUserLoginFragment();
}

@Module
abstract class PasswordChangeModule {
    @ContributesAndroidInjector
    abstract PasswordChangeFragment contributePasswordChangeFragment();
}

@Module
abstract class CommentDetailModule {
    @ContributesAndroidInjector
    abstract CommentDetailFragment commentDetailFragment();
}


@Module
abstract class NotificationModule {
    @ContributesAndroidInjector
    abstract NotificationListFragment notificationFragment();


}


@Module
abstract class NotificationDetailModule {
    @ContributesAndroidInjector
    abstract NotificationFragment notificationDetailFragment();
}

@Module
abstract class UserHistoryModule {
    @ContributesAndroidInjector
    abstract HistoryFragment contributeHistoryFragment();
}

@Module
abstract class AppInfoModule {
    @ContributesAndroidInjector
    abstract AppInfoFragment contributeAppInfoFragment();
}

@Module
abstract class ProductCollectionModule {
    @ContributesAndroidInjector
    abstract CollectionHeaderListFragment contributeProductCollectionHeaderListFragment();
}

@Module
abstract class CategoryListActivityAppInfoModule {
    @ContributesAndroidInjector
    abstract CategoryListFragment contributeCategoryFragment();

}

@Module
abstract class RatingListActivityModule {
    @ContributesAndroidInjector
    abstract RatingListFragment contributeRatingListFragment();
}

@Module
abstract class readMoreActivityModule {
    @ContributesAndroidInjector
    abstract ReadMoreFragment contributeReadMoreFragment();
}

@Module
abstract class EditSettingModule {
    @ContributesAndroidInjector
    abstract SettingFragment EditSettingFragment();
}

@Module
abstract class LanguageChangeModule {
    @ContributesAndroidInjector
    abstract NotificationSettingFragment notificationSettingFragment();
}

@Module
abstract class EditProfileModule {
    @ContributesAndroidInjector
    abstract ProfileFragment ProfileFragment();
}

@Module
abstract class SubCategoryActivityModule {
    @ContributesAndroidInjector
    abstract SubCategoryFragment contributeSubCategoryFragment();

}

@Module
abstract class FilteringModule {

    @ContributesAndroidInjector
    abstract CategoryFilterFragment contributeTypeFilterFragment();

    @ContributesAndroidInjector
    abstract FilteringFragment contributeSpecialFilteringFragment();

}

@Module
abstract class LoginUserHistoryListActivityModule {

    @ContributesAndroidInjector
    abstract HistoryFragment contributeHistoryFragment();

    @ContributesAndroidInjector
    abstract  LoginUserPaidItemFragment contributeLoginUserPaidItemFragment();

}

@Module
abstract class SearchActivityModule {
    @ContributesAndroidInjector
    abstract SearchListFragment contributefeaturedProductFragment();

    @ContributesAndroidInjector
    abstract CategoryListFragment contributeCategoryFragment();

    @ContributesAndroidInjector
    abstract CategoryFilterFragment contributeTypeFilterFragment();

    @ContributesAndroidInjector
    abstract ItemCollectionFragment contributeCollectionFragment();
}

@Module
abstract class CommentListActivityModule {
    @ContributesAndroidInjector
    abstract CommentListFragment contributeCommentListFragment();
}

@Module
abstract class LoginUserItemListActivityModule {

    @ContributesAndroidInjector
    abstract LoginUserItemFragment contributeLoginUserItemFragment();
}

@Module
abstract class GalleryDetailActivityModule {
    @ContributesAndroidInjector
    abstract GalleryDetailFragment contributeGalleryDetailFragment();
}

@Module
abstract class GalleryActivityModule {
    @ContributesAndroidInjector
    abstract GalleryFragment contributeGalleryFragment();
}

@Module
abstract class SearchByCategoryActivityModule {

    @ContributesAndroidInjector
    abstract DashBoardSearchCategoryFragment contributeDashBoardSearchCategoryFragment();

    @ContributesAndroidInjector
    abstract DashBoardSearchSubCategoryFragment contributeDashBoardSearchSubCategoryFragment();
}

@Module
abstract class SelectedCityModule {

    @ContributesAndroidInjector
    abstract SearchListFragment contributefeaturedProductFragment();

    @ContributesAndroidInjector
    abstract CityFragment contributeAboutUsFragmentFragment();

    @ContributesAndroidInjector
    abstract CategoryListFragment categoryListFragment();

    @ContributesAndroidInjector
    abstract SelectedCityFragment contributeSelectedCityFragment();

    @ContributesAndroidInjector
    abstract CategoryFilterFragment contributeTypeFilterFragment();

    @ContributesAndroidInjector
    abstract CollectionHeaderListFragment contributeProductCollectionHeaderListFragment();

    @ContributesAndroidInjector
    abstract CityMenuFragment contributeCityMenuFragment();

    @ContributesAndroidInjector
    abstract DashBoardSearchFragment contributeDashBoardSearchFragment();
}

@Module
abstract class CollectionModule {

    @ContributesAndroidInjector
    abstract ItemCollectionFragment contributeCollectionFragment();

}

@Module
abstract class StripeModule {

    @ContributesAndroidInjector
    abstract ItemCollectionFragment contributeCollectionFragment();

}


@Module
abstract class SelectedShopListBlogModule {

    @ContributesAndroidInjector
    abstract BlogListFragment contributeSelectedShopListBlogFragment();

}

@Module
abstract class BlogDetailModule {

    @ContributesAndroidInjector
    abstract BlogDetailFragment contributeBlogDetailFragment();
}

@Module
abstract class MapActivityModule {

    @ContributesAndroidInjector
    abstract MapFragment contributeMapFragment();
}

@Module
abstract class CityActivityModule {

    @ContributesAndroidInjector
    abstract CityFragment contributeCityFragment();
}

@Module
abstract class forceUpdateModule {

    @ContributesAndroidInjector
    abstract ForceUpdateFragment contributeForceUpdateFragment();
}

@Module
abstract class blogListByShopIdActivityModule {

    @ContributesAndroidInjector
    abstract BlogListByCityIdFragment contributeBlogListByShopIdFragment();
}

@Module
abstract class CityListModule {

    @ContributesAndroidInjector
    abstract CityListFragment contributeCityListFragment();
}

@Module
abstract class MapFilteringModule {

    @ContributesAndroidInjector
    abstract MapFilteringFragment contributeMapFilteringFragment();
}

@Module
abstract class AppLoadingModule {

    @ContributesAndroidInjector
    abstract AppLoadingFragment contributeAppLoadingFragment();
}

@Module
abstract class SpecificationListModule {

    @ContributesAndroidInjector
    abstract SpecificationListFragment contributeSpecificationListFragment();
}

@Module
abstract class AddAndEditSpecificationModule {

    @ContributesAndroidInjector
    abstract AddAndEditSpecificationFragment contributeAddAndEditSpecificationFragment();
}

@Module
abstract class ItemImageListModule {
    @ContributesAndroidInjector
    abstract ItemImageListFragment contributeItemImageListFragment();
}

@Module
abstract class MapItemEntryModule {
    @ContributesAndroidInjector
    abstract com.sable.businesslistingapi.ui.item.upload.map.MapFragment contributeUploadMapFragment();

}
@Module
abstract class ItemUploadActivityModule {

    @ContributesAndroidInjector
    abstract ItemUploadFragment contributeItemUploadFragment();
}

@Module
abstract class PhoneLoginActivityModule {
    @ContributesAndroidInjector
    abstract PhoneLoginFragment cameraPhoneLoginFragment();
}

@Module
abstract class VerifyMobileModule {
    @ContributesAndroidInjector
    abstract VerifyMobileFragment contributeVerifyMobileFragment();
}
@Module
abstract class StripeeModule {

    @ContributesAndroidInjector
    abstract StripeFragment contributeStripeFragment();

}

@Module
abstract class ItemPromoteEntryActivityModule {
    @ContributesAndroidInjector
    abstract ItemPromoteFragment contributeItemPromoteFragment();
}

@Module
abstract class TermsandConditionsActivityModule {
    @ContributesAndroidInjector
    abstract TermsAndConditionsFragment contributeTermsAndConditionsFragment();
}