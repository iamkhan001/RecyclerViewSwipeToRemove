package com.nstudio.shaadimatches.ui;

import android.app.Application;

import com.nstudio.shaadimatches.models.ProfileModel;
import com.nstudio.shaadimatches.utils.MainRepository;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

class MainViewModel extends AndroidViewModel {

    private MutableLiveData<ArrayList<ProfileModel>> mProfiles;
    private MainRepository repository;

    MainViewModel(@NonNull Application application) {
        super(application);
        mProfiles = new MutableLiveData<>();
        repository = new MainRepository(mProfiles);
    }


    MutableLiveData<ArrayList<ProfileModel>> getProfiles() {
        return mProfiles;
    }

    void loadProfileList(){
        repository.loadProfiles(getApplication());
    }



}
