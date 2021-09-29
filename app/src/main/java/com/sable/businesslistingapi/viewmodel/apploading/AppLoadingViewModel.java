package com.sable.businesslistingapi.viewmodel.apploading;

import com.sable.businesslistingapi.repository.apploading.AppLoadingRepository;
import com.sable.businesslistingapi.utils.AbsentLiveData;
import com.sable.businesslistingapi.utils.Utils;
import com.sable.businesslistingapi.viewmodel.common.PSViewModel;
import com.sable.businesslistingapi.viewobject.PSAppInfo;
import com.sable.businesslistingapi.viewobject.common.Resource;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

public class AppLoadingViewModel extends PSViewModel {

    private final LiveData<Resource<PSAppInfo>> deleteHistoryData;
    private MutableLiveData<TmpDataHolder> deleteHistoryObj = new MutableLiveData<>();
    public PSAppInfo psAppInfo;
    public String stripePublishableKey;
    public String currencyShortForm;
    @Inject
    public AppLoadingViewModel(AppLoadingRepository repository) {
        deleteHistoryData = Transformations.switchMap(deleteHistoryObj, obj -> {
            if (obj == null) {
                return AbsentLiveData.create();
            }
            Utils.psLog("AppLoadingViewModel");
            return repository.deleteTheSpecificObjects(obj.startDate, obj.endDate,obj.user_id);
        });
    }

    public void setDeleteHistoryObj(String startDate, String endDate,String user_id) {

        TmpDataHolder tmpDataHolder = new TmpDataHolder(startDate, endDate,user_id);

        this.deleteHistoryObj.setValue(tmpDataHolder);
    }

    public LiveData<Resource<PSAppInfo>> getDeleteHistoryData() {
        return deleteHistoryData;
    }

    class TmpDataHolder {
        String startDate, endDate,user_id;

        public TmpDataHolder(String startDate, String endDate,String user_id) {
            this.startDate = startDate;
            this.endDate = endDate;
            this.user_id = user_id;
        }
    }

}
