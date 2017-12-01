package com.chadov.getalarm.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.chadov.getalarm.data.DbContract;
import com.chadov.getalarm.data.DbHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

/**
 * Created by ChadovTA on 16.10.2017.
 */

public class GeofenceRepositoryImpl implements GeofenceRepository {

    private DbHelper mDb;
    private List<Geofence> mGeofences;

    @Inject
    public Context context;

    @Inject
    public GeofenceRepositoryImpl(Context context) {
        mDb = new DbHelper(context);
        mGeofences = new ArrayList<>();

        Initialize();
    }

    private void Initialize() {
        addNew(new Geofence("Московская дача", 56.540358, 38.036717, 3));
        addNew(new Geofence("Лесная дача", 56.15638889, 45.21583333, 10));
    }


    public List<Geofence> getGeofences() {
        mGeofences.clear();
        readGeofences(mGeofences);
        return mGeofences;
    }

    public Geofence get(UUID id) {
        for (Geofence geo : mGeofences) {
            if (geo.getId().equals(id)) {
                return geo;
            }
        }
        return null;
    }

    private void readGeofences(List<Geofence> geofences) {
        // Создадим и откроем для чтения базу данных
        SQLiteDatabase db = mDb.getReadableDatabase();

        // Зададим условие для выборки - список столбцов
        String[] projection = {
                DbContract.LocationEntry._ID,
                DbContract.LocationEntry.COLUMN_NAME,
                DbContract.LocationEntry.COLUMN_LATITUDE,
                DbContract.LocationEntry.COLUMN_LONGITUDE,
                DbContract.LocationEntry.COLUMN_RADIUS,
                DbContract.LocationEntry.COLUMN_ACTIVE};

        // Делаем запрос
        Cursor cursor = db.query(
                DbContract.LocationEntry.TABLE_NAME,   // таблица
                projection,            // столбцы
                null,                  // столбцы для условия WHERE
                null,                  // значения для условия WHERE
                null,                  // Don't group the rows
                null,                  // Don't filter by row groups
                null);                   // порядок сортировки


        try {

            // Узнаем индекс каждого столбца
            int idColumnIndex = cursor.getColumnIndex(DbContract.LocationEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(DbContract.LocationEntry.COLUMN_NAME);
            int latitudeColumnIndex = cursor.getColumnIndex(DbContract.LocationEntry.COLUMN_LATITUDE);
            int longitudeColumnIndex = cursor.getColumnIndex(DbContract.LocationEntry.COLUMN_LONGITUDE);
            int radiusColumnIndex = cursor.getColumnIndex(DbContract.LocationEntry.COLUMN_RADIUS);
            int activeColumnIndex = cursor.getColumnIndex(DbContract.LocationEntry.COLUMN_ACTIVE);

            // Проходим через все ряды
            while (cursor.moveToNext()) {
                // Используем индекс для получения строки или числа
                int currentID = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                Double currentLat = cursor.getDouble(latitudeColumnIndex);
                Double currentLong = cursor.getDouble(longitudeColumnIndex);
                int currentRadius = cursor.getInt(radiusColumnIndex);

                geofences.add(new Geofence(currentName, currentLat, currentLong, currentRadius));
            }
        } finally {
            // Всегда закрываем курсор после чтения
            cursor.close();
        }
    }

    public void addNew(Geofence geofence) {
        mGeofences.add(geofence);
    }
}