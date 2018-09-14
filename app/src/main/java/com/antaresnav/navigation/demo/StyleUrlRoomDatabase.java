package com.antaresnav.navigation.demo;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;

import java.util.concurrent.Executors;

@Database(entities = {StyleUrl.class}, version = 1)
public abstract class StyleUrlRoomDatabase extends RoomDatabase {

    private static StyleUrlRoomDatabase INSTANCE;

    public static StyleUrlRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (StyleUrlRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            StyleUrlRoomDatabase.class, "style_url_database")
                            .addCallback(new Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    Executors.newSingleThreadScheduledExecutor().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            INSTANCE.styleUrlDao().insertAll(populate());
                                        }
                                    });
                                }

                                private StyleUrl[] populate() {
                                    return new StyleUrl[] {
                                            new StyleUrl("OSM Daylight", "https://terkep.geox.hu/timon/TIMON_basic_default_mbgl_style_antaresmod_STATIC.json"),
                                            new StyleUrl("OSM Night", "https://terkep.geox.hu/timon/TIMON_basic_default_mbgl_style_NIGHT_antaresmod_STATIC.json"),
                                            new StyleUrl("OSM Bright", "https://terkep.geox.hu/timon/openmaptiles_bright_antaresmod.json"),
                                            new StyleUrl("DigitalGlobe Satellite", "https://terkep.geox.hu/timon/TIMON_DigitalGlobe.json"),
                                            new StyleUrl("OpenWeather", "https://terkep.geox.hu/timon/TIMON_OSM_basic_default_DAY_overlay_OWM_CloudsPrecip.json")
                                    };
                                }
                            })
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract StyleUrlDao styleUrlDao();
}
