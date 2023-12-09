package com.zybooks.weighttrackerapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class Database extends SQLiteOpenHelper {

    private static Database database;
    private static final String DATABASE_NAME = "WeightTracker.db";
    private static final int VERSION = 1;

    //Database is a singleton to make retrieval across app easier and uniform
    private Database(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    //Creates a database if not already created
    public static synchronized Database getInstance(Context context) {
        if (database == null) {
            database = new Database(context);
        }
        return database;
    }

    private static final class UserTable {
        private static final String TABLE = "users";
        private static final String USER = "username";
        private static final String PASS = "password";
        private static final String PHONE = "phoneNumber";
    }

    private static final class WeightTable {
        private static final String TABLE = "weights";
        private static final String USER = "user";
        private static final String DATE = "date";
        private static final String WEIGHT = "weight";

    }

    private static final class GoalTable {
        private static final String TABLE = "goals";
        private static final String USER = "user";
        private static final String GOAL = "goal";
    }


    //Creates the 3 tables for my database
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + UserTable.TABLE + " (" +
                UserTable.USER + " TEXT PRIMARY KEY, " +
                UserTable.PASS + " TEXT, " +
                UserTable.PHONE + " TEXT)"
        );

        db.execSQL("CREATE TABLE " + WeightTable.TABLE + " (" +
                WeightTable.USER + " TEXT, " +
                WeightTable.DATE + " TEXT, " +
                WeightTable.WEIGHT + " REAL, " +
                "PRIMARY KEY (" + WeightTable.USER + ", " + WeightTable.DATE + ", " + WeightTable.WEIGHT + "), " +
                "FOREIGN KEY (" + WeightTable.USER + ") REFERENCES " + UserTable.TABLE + "(" + UserTable.USER + "))"
        );

        db.execSQL("CREATE TABLE " + GoalTable.TABLE + " (" +
                GoalTable.USER + " TEXT, " +
                GoalTable.GOAL + " REAL, " +
                "PRIMARY KEY (" + GoalTable.USER + ", " + GoalTable.GOAL + "), " +
                "FOREIGN KEY (" + GoalTable.USER + ") REFERENCES " + UserTable.TABLE + "(" + UserTable.USER + "))"
        );
    }

    //Will clear and recreate the tables on version upgrade
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + UserTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + WeightTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + GoalTable.TABLE);
        onCreate(db);
    }

    public boolean addUser(String user, String pass) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(UserTable.USER, user);
        values.put(UserTable.PASS, pass);
        values.put(UserTable.PHONE, "EMPTY");

        long userId = db.insert(UserTable.TABLE, null, values);

        //Will be true if data added. False if not.
        return userId != -1;
    }

    public boolean addWeight(String user, String date, int weight) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(WeightTable.USER, user);
        values.put(WeightTable.DATE, date);
        values.put(WeightTable.WEIGHT, weight);

        long weightId = db.insert(WeightTable.TABLE, null, values);

        //Will be true if data added. False if not.
        return weightId != -1;
    }

    public boolean deleteData(String user, String date, String weight) {
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = WeightTable.USER + " = ? AND " + WeightTable.DATE + " = ? AND " + WeightTable.WEIGHT + " = ?";
        int rowsDeleted = db.delete(WeightTable.TABLE, whereClause, new String[]{user, date, weight});

        return rowsDeleted == 1;
    }

    public boolean editWeight(String user, String date, String weight, String newDate, String newWeight) {
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = WeightTable.USER + "= ? AND " + WeightTable.DATE + " = ? AND " + WeightTable.WEIGHT + " = ?";
        boolean weightEdited = false;

        ContentValues values = new ContentValues();
        values.put(WeightTable.USER, user);
        values.put(WeightTable.DATE, newDate);
        values.put(WeightTable.WEIGHT, newWeight);

        Cursor cursor = db.rawQuery("SELECT * FROM " + WeightTable.TABLE + " WHERE " + whereClause, new String[]{user, date, weight});

        if (cursor.getCount() == 1) {
            int rowUpdated = db.update(WeightTable.TABLE, values, whereClause, new String[]{user, date, weight});
            if (rowUpdated == 1) {
                weightEdited = true;
            }
        }

        cursor.close();

        return  weightEdited;
    }

    public boolean verifyUser(String user, String pass) {
        SQLiteDatabase db = getReadableDatabase();
        boolean verified = false;

        Cursor cursor = db.rawQuery("SELECT * FROM " + UserTable.TABLE + " WHERE " +
                UserTable.USER + " = ? AND " +
                UserTable.PASS + " = ?", new String[]{user, pass});

        if (cursor.getCount() == 1) {
            verified = true;
        }

        cursor.close();

        return verified;
    }

    public boolean addNumber(String user, String number) {
        SQLiteDatabase db = getWritableDatabase();
        boolean numberAdded = false;

        ContentValues values = new ContentValues();
        values.put(UserTable.USER, user);
        values.put(UserTable.PHONE, number);

        Cursor cursor = db.rawQuery("SELECT * FROM " + UserTable.TABLE + " WHERE " +
                UserTable.USER + " = ?", new String[]{user});

        if (cursor.getCount() == 1) {
            int rowUpdated = db.update(UserTable.TABLE, values, UserTable.USER + " = ?", new String[]{user});
            if (rowUpdated == 1) {
                numberAdded = true;
            }
        }

        cursor.close();

        return numberAdded;
    }

    public boolean setGoal(String user, int goal) {
        SQLiteDatabase db = getWritableDatabase();
        boolean goalAdded = false;

        ContentValues values = new ContentValues();
        values.put(GoalTable.USER, user);
        values.put(GoalTable.GOAL, goal);

        Cursor cursor = db.rawQuery("SELECT * FROM " + GoalTable.TABLE + " WHERE " +
                GoalTable.USER + " = ?", new String[]{user});

        //Goal has never been added so add goal
        if (cursor.getCount() == 0) {
            long goalId = db.insert(GoalTable.TABLE, null, values);
            if (goalId != -1) {
                goalAdded = true;
            }
        //Update goal if one is already set
        } else {
            int rowUpdated = db.update(GoalTable.TABLE, values, GoalTable.USER + " = ?", new String[]{user});
            if (rowUpdated == 1) {
                goalAdded = true;
            }
        }

        cursor.close();

        return goalAdded;
    }

    public int getGoal(String user) {
        SQLiteDatabase db = getReadableDatabase();
        int goal;

        Cursor cursor = db.rawQuery("SELECT * FROM " + GoalTable.TABLE + " WHERE " +
                GoalTable.USER + " = ?", new String[]{user});

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            goal = cursor.getInt(1);
        } else {
            goal = 0;
        }

        cursor.close();

        return goal;
    }

    public String getNumber(String user) {
        SQLiteDatabase db = getReadableDatabase();
        String number = "EMPTY";

        Cursor cursor = db.rawQuery("SELECT * FROM " + UserTable.TABLE + " WHERE " +
                UserTable.USER + " = ?", new String[]{user});

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            number = cursor.getString(2);
        }

        cursor.close();

        return number;
    }

    public ArrayList<String> getWeights(String user) {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<String> weights = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + WeightTable.TABLE + " WHERE " +
                WeightTable.USER + " = ? Order BY " + WeightTable.DATE + " DESC", new String[]{user});

        if (cursor.moveToFirst()) {
            do {
                weights.add(String.valueOf(cursor.getInt(2)));
            } while (cursor.moveToNext());
        }

        cursor.close();

        return weights;
    }

    public ArrayList<String> getDates(String user) {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<String> dates = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + WeightTable.TABLE + " WHERE " +
                WeightTable.USER + " = ? Order BY " + WeightTable.DATE + " DESC", new String[]{user});

        if (cursor.moveToFirst()) {
            do {
                dates.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }

        cursor.close();

        return dates;
    }
}
