package com.example.rdvmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class RDVDAO {
    private SQLiteDatabase database;
    private RDVDBHelper dbHelper;

    public RDVDAO(Context context) {
        dbHelper = new RDVDBHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long addRDV(RDV rdv) {
        ContentValues values = new ContentValues();
        values.put(RDVDBHelper.COLUMN_TITLE, rdv.getTitle());
        values.put(RDVDBHelper.COLUMN_DATE, rdv.getDate());
        values.put(RDVDBHelper.COLUMN_TIME, rdv.getTime());
        values.put(RDVDBHelper.COLUMN_CONTACT, rdv.getContact());
        values.put(RDVDBHelper.COLUMN_ADDRESS, rdv.getAddress());
        values.put(RDVDBHelper.COLUMN_PHONE_NUMBER, rdv.getPhoneNumber());
        values.put(RDVDBHelper.COLUMN_DESCRIPTION, rdv.getDescription());
        values.put(RDVDBHelper.COLUMN_IS_DONE, rdv.isDone() ? 1 : 0);

        long id = database.insert(RDVDBHelper.TABLE_NAME, null, values);
        return id;
    }


    public List<RDV> getAllRDVs() {
        List<RDV> rdvList = new ArrayList<>();

        Cursor cursor = database.query(RDVDBHelper.TABLE_NAME,
                null, null, null, null, null, RDVDBHelper.COLUMN_DATE + " ASC, " +
                        RDVDBHelper.COLUMN_TIME + " ASC");

        while (cursor.moveToNext()) {
            RDV rdv = new RDV();
            rdv.setId(cursor.getInt(cursor.getColumnIndexOrThrow(RDVDBHelper.COLUMN_ID)));
            rdv.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(RDVDBHelper.COLUMN_TITLE)));
            rdv.setDate(cursor.getString(cursor.getColumnIndexOrThrow(RDVDBHelper.COLUMN_DATE)));
            rdv.setTime(cursor.getString(cursor.getColumnIndexOrThrow(RDVDBHelper.COLUMN_TIME)));
            rdv.setContact(cursor.getString(cursor.getColumnIndexOrThrow(RDVDBHelper.COLUMN_CONTACT)));
            rdv.setAddress(cursor.getString(cursor.getColumnIndexOrThrow(RDVDBHelper.COLUMN_ADDRESS)));
            rdv.setPhoneNumber(cursor.getString(cursor.getColumnIndexOrThrow(RDVDBHelper.COLUMN_PHONE_NUMBER)));
            rdv.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(RDVDBHelper.COLUMN_DESCRIPTION)));
            rdv.setDone(cursor.getInt(cursor.getColumnIndexOrThrow(RDVDBHelper.COLUMN_IS_DONE)) == 1);

            rdvList.add(rdv);
        }

        cursor.close();

        return rdvList;
    }


    public RDV getRDVById(long id) {
        String selection = RDVDBHelper.COLUMN_ID + "=?";
        String[] selectionArgs = {String.valueOf(id)};

        Cursor cursor = database.query(RDVDBHelper.TABLE_NAME, null, selection, selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            RDV rdv = new RDV();
            rdv.setId(cursor.getInt(cursor.getColumnIndexOrThrow(RDVDBHelper.COLUMN_ID)));
            rdv.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(RDVDBHelper.COLUMN_TITLE)));
            rdv.setDate(cursor.getString(cursor.getColumnIndexOrThrow(RDVDBHelper.COLUMN_DATE)));
            rdv.setTime(cursor.getString(cursor.getColumnIndexOrThrow(RDVDBHelper.COLUMN_TIME)));
            rdv.setContact(cursor.getString(cursor.getColumnIndexOrThrow(RDVDBHelper.COLUMN_CONTACT)));
            rdv.setAddress(cursor.getString(cursor.getColumnIndexOrThrow(RDVDBHelper.COLUMN_ADDRESS)));
            rdv.setPhoneNumber(cursor.getString(cursor.getColumnIndexOrThrow(RDVDBHelper.COLUMN_PHONE_NUMBER)));
            rdv.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(RDVDBHelper.COLUMN_DESCRIPTION)));
            rdv.setDone(cursor.getInt(cursor.getColumnIndexOrThrow(RDVDBHelper.COLUMN_IS_DONE)) == 1);

            cursor.close();
            return rdv;
        } else {
            return null;
        }
    }

    public int updateRDV(RDV rdv) {
        ContentValues values = new ContentValues();
        values.put(RDVDBHelper.COLUMN_TITLE, rdv.getTitle());
        values.put(RDVDBHelper.COLUMN_DATE, rdv.getDate());
        values.put(RDVDBHelper.COLUMN_TIME, rdv.getTime());
        values.put(RDVDBHelper.COLUMN_CONTACT, rdv.getContact());
        values.put(RDVDBHelper.COLUMN_ADDRESS, rdv.getAddress());
        values.put(RDVDBHelper.COLUMN_PHONE_NUMBER, rdv.getPhoneNumber());
        values.put(RDVDBHelper.COLUMN_DESCRIPTION, rdv.getDescription());
        values.put(RDVDBHelper.COLUMN_IS_DONE, rdv.isDone() ? 1 : 0);

        return database.update(RDVDBHelper.TABLE_NAME, values,
                RDVDBHelper.COLUMN_ID + " = ?", new String[]{String.valueOf(rdv.getId())});
    }

    public int deleteRDV(RDV rdv) {
        return database.delete(RDVDBHelper.TABLE_NAME,
                RDVDBHelper.COLUMN_ID + " = ?", new String[]{String.valueOf(rdv.getId())});
    }

    public List<RDV> getRDVsByDate(String date) {
        List<RDV> rdvs = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                RDVDBHelper.COLUMN_ID,
                RDVDBHelper.COLUMN_TITLE,
                RDVDBHelper.COLUMN_DATE,
                RDVDBHelper.COLUMN_TIME,
                RDVDBHelper.COLUMN_ADDRESS,
                RDVDBHelper.COLUMN_PHONE_NUMBER,
                RDVDBHelper.COLUMN_DESCRIPTION
        };

        String selection = RDVDBHelper.COLUMN_DATE + "=?";
        String[] selectionArgs = {date};

        Cursor cursor = db.query(
                RDVDBHelper.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndexOrThrow(RDVDBHelper.COLUMN_ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(RDVDBHelper.COLUMN_TITLE));
            String dateStr = cursor.getString(cursor.getColumnIndexOrThrow(RDVDBHelper.COLUMN_DATE));
            String time = cursor.getString(cursor.getColumnIndexOrThrow(RDVDBHelper.COLUMN_TIME));
            String location = cursor.getString(cursor.getColumnIndexOrThrow(RDVDBHelper.COLUMN_ADDRESS));
            String phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(RDVDBHelper.COLUMN_PHONE_NUMBER));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(RDVDBHelper.COLUMN_DESCRIPTION));

            RDV rdv = new RDV(id, title, dateStr, time, location, phoneNumber, description, true);
            rdvs.add(rdv);
        }

        cursor.close();

        return rdvs;
    }


}
