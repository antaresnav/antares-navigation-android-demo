package com.antaresnav.navigation.demo;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class StyleUrlRepository {

    private StyleUrlDao styleUrlDao;
    private LiveData<List<StyleUrl>> allStyleUrls;

    StyleUrlRepository(Application application) {
        styleUrlDao = StyleUrlRoomDatabase.getDatabase(application).styleUrlDao();
        allStyleUrls = styleUrlDao.loadAll();
    }

    LiveData<List<StyleUrl>> getAll() {
        return allStyleUrls;
    }

    void insert(StyleUrl styleUrl) {
        new InsertAsyncTask(styleUrlDao).execute(styleUrl);
    }

    void delete(StyleUrl styleUrl) {
        new DeleteAsyncTask(styleUrlDao).execute(styleUrl);
    }

    private static class InsertAsyncTask extends AsyncTask<StyleUrl, Void, Void> {

        private StyleUrlDao asyncTaskDao;

        InsertAsyncTask(StyleUrlDao styleUrlDao) {
            asyncTaskDao = styleUrlDao;
        }

        @Override
        protected Void doInBackground(final StyleUrl... params) {
            asyncTaskDao.insert(params[0]);
            return null;
        }
    }

    private static class DeleteAsyncTask extends AsyncTask<StyleUrl, Void, Void> {

        private StyleUrlDao asyncTaskDao;

        DeleteAsyncTask(StyleUrlDao styleUrlDao) {
            asyncTaskDao = styleUrlDao;
        }

        @Override
        protected Void doInBackground(final StyleUrl... params) {
            asyncTaskDao.delete(params[0]);
            return null;
        }
    }
}