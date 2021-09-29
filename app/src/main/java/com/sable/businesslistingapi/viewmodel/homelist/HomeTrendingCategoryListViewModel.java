package com.sable.businesslistingapi.viewmodel.homelist;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.sable.businesslistingapi.repository.itemcategory.ItemCategoryRepository;
import com.sable.businesslistingapi.utils.AbsentLiveData;
import com.sable.businesslistingapi.utils.Utils;
import com.sable.businesslistingapi.viewmodel.common.PSViewModel;
import com.sable.businesslistingapi.viewobject.ItemCategory;
import com.sable.businesslistingapi.viewobject.common.Resource;
import com.sable.businesslistingapi.viewobject.holder.CategoryParameterHolder;

import java.util.List;

import javax.inject.Inject;

public class HomeTrendingCategoryListViewModel extends PSViewModel {


    //region Variables

    private final LiveData<Resource<List<ItemCategory>>> categoryListData;
    private MutableLiveData<HomeTrendingCategoryListViewModel.TmpDataHolder> categoryListObj = new MutableLiveData<>();

    private final LiveData<Resource<Boolean>> nextPageLoadingStateData;
    private MutableLiveData<HomeTrendingCategoryListViewModel.TmpDataHolder> nextPageLoadingStateObj = new MutableLiveData<>();

    public CategoryParameterHolder categoryParameterHolder = new CategoryParameterHolder();

    //endregion

    //region Constructors

    @Inject
    HomeTrendingCategoryListViewModel(ItemCategoryRepository repository) {

        Utils.psLog("ItemCategoryViewModel");

        categoryListData = Transformations.switchMap(categoryListObj, obj -> {
            if (obj == null) {
                return AbsentLiveData.create();
            }

            Utils.psLog("ItemCategoryViewModel : categories");
            return repository.getAllSearchCityCategory(obj.limit, obj.offset, obj.cityId);
        });

        nextPageLoadingStateData = Transformations.switchMap(nextPageLoadingStateObj, obj -> {
            if (obj == null) {
                return AbsentLiveData.create();
            }

            Utils.psLog("Category List.");
            return repository.getNextSearchCityCategory(obj.limit, obj.offset, obj.cityId);
        });

    }

    //endregion

    public void setCategoryListObj(String limit, String offset, String cityId) {
        if (!isLoading) {
            HomeTrendingCategoryListViewModel.TmpDataHolder tmpDataHolder = new HomeTrendingCategoryListViewModel.TmpDataHolder();
            tmpDataHolder.offset = offset;
            tmpDataHolder.limit = limit;
            tmpDataHolder.cityId = cityId;
            categoryListObj.setValue(tmpDataHolder);

            // start loading
            setLoadingState(true);
        }
    }

    public LiveData<Resource<List<ItemCategory>>> getCategoryListData() {
        return categoryListData;
    }

    //Get Latest Category Next Page
    public void setNextPageLoadingStateObj(String limit, String offset, String cityId) {

        if (!isLoading) {
            HomeTrendingCategoryListViewModel.TmpDataHolder tmpDataHolder = new HomeTrendingCategoryListViewModel.TmpDataHolder();
            tmpDataHolder.offset = offset;
            tmpDataHolder.limit = limit;
            tmpDataHolder.cityId = cityId;
            nextPageLoadingStateObj.setValue(tmpDataHolder);

            // start loading
            setLoadingState(true);
        }
    }

    public LiveData<Resource<Boolean>> getNextPageLoadingStateData() {
        return nextPageLoadingStateData;
    }


    class TmpDataHolder {
        public String limit = "";
        public String offset = "";
        public String cityId = "";
    }
}
