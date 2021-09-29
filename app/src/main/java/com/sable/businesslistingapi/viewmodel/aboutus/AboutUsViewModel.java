package com.sable.businesslistingapi.viewmodel.aboutus;

import com.sable.businesslistingapi.Config;
import com.sable.businesslistingapi.repository.aboutus.AboutUsRepository;
import com.sable.businesslistingapi.utils.AbsentLiveData;
import com.sable.businesslistingapi.viewmodel.common.PSViewModel;
import com.sable.businesslistingapi.viewobject.AboutUs;
import com.sable.businesslistingapi.viewobject.common.Resource;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

/**
 * Sable Business Directory on 09/01/2021
 */

public class AboutUsViewModel extends PSViewModel {


    //region Variables

    // Get AboutUs
    private final LiveData<Resource<AboutUs>> aboutUsLiveData;
    private MutableLiveData<String> aboutUsObj = new MutableLiveData<>();

    public String aboutId;
    //endregion


    //region Constructors

    @Inject
    AboutUsViewModel(AboutUsRepository repository) {

        aboutUsLiveData = Transformations.switchMap(aboutUsObj, newsId -> {
            if (newsId.isEmpty()) {
                return AbsentLiveData.create();
            }
            return repository.getAboutUs(Config.API_KEY);
        });

    }

    //endregion


    //region Public Methods

    public void setAboutUsObj(String aboutUsObj) {
        this.aboutUsObj.setValue(aboutUsObj);
    }

    public LiveData<Resource<AboutUs>> getAboutUsData() {
        return aboutUsLiveData;
    }

    //endregion

}
