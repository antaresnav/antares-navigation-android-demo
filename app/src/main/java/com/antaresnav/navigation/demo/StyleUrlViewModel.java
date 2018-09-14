package com.antaresnav.navigation.demo;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

public class StyleUrlViewModel extends AndroidViewModel {

    private StyleUrlRepository repository;
    private LiveData<List<StyleUrl>> allStyleUrls;

    public StyleUrlViewModel(Application application) {
        super(application);
        repository = new StyleUrlRepository(application);
        allStyleUrls = repository.getAll();
    }

    LiveData<List<StyleUrl>> getAll() { return allStyleUrls; }

    void insert(StyleUrl styleUrl) {
        repository.insert(styleUrl);
    }

    void delete(StyleUrl styleUrl) {
        repository.delete(styleUrl);
    }
}
