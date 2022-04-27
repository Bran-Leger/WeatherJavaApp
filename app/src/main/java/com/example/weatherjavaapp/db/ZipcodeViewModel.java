package com.example.weatherjavaapp.db;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class ZipcodeViewModel extends AndroidViewModel {
    private LiveData<List<Zipcode>> zipcodes;
//    private Zipcode zipcode;

    public ZipcodeViewModel(Application application) {
        super(application);
        zipcodes = ZipcodeDatabase.getDatabase(getApplication()).zipcodeDAO().getAll();
//        zipcode = ZipcodeDatabase.getDatabase(getApplication()).zipcodeDAO().getCurrent();
    }

    public LiveData<List<Zipcode>> getAllZipcodes() {
        return zipcodes;
    }

//    public Zipcode getCurrent() {return zipcode;}
}