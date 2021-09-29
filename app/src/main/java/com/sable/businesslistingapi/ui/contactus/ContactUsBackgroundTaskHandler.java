package com.sable.businesslistingapi.ui.contactus;

import com.sable.businesslistingapi.repository.contactus.ContactUsRepository;
import com.sable.businesslistingapi.ui.common.BackgroundTaskHandler;

/**
 * Sable Business Directory on 09/01/2021
 * Contact Email : admin@sablebusinessdirectory.com
 * Website : http://www.sablebusinessdirectory.com
 */

public class ContactUsBackgroundTaskHandler extends BackgroundTaskHandler{

    private final ContactUsRepository repository;

    public ContactUsBackgroundTaskHandler(ContactUsRepository repository) {
        super();
        this.repository = repository;
    }


    public void postContactUs(String apiKey, String contactName, String contactEmail, String contactDesc, String contactPhone) {

        unregister();

        holdLiveData = repository.postContactUs(apiKey, contactName, contactEmail, contactDesc, contactPhone);
        loadingState.setValue(new LoadingState(true, null));

        //noinspection ConstantConditions
        holdLiveData.observeForever(this);
    }
}
