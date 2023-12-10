/**
 * @author Afahri Kerr (AfahriAKerr91@gmail.com)
 * @version 2.0
 * @since 12/07/2023
 * Purpose: Create a database to store and retrieve the user data.
 */

package com.zybooks.weighttrackerapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/**
 * This class contains the logic to create and control the SQLite Database
 */
public class Database extends SQLiteOpenHelper {

    private static Database database;
    private static final Encryptor encryptor = Encryptor.getInstance();
    private static final String DATABASE_NAME = "WeightTracker.db";
    private static final int VERSION = 2;

    //Database is a singleton to make retrieval across app easier and uniform
    private Database(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    /**
     * This method creates a database or returns the old one if already created in context.
     * @param context Context used for the database
     * @return The database.
     */
    public static synchronized Database getInstance(Context context) {
        if (database == null) {
            database = new Database(context);
        }
        return database;
    }

    /**
     * Class to define the column header names of User table.
     */
    private static final class UserTable {
        private static final String TABLE = "users";
        private static final String USER = "username";
        private static final String PASS = "password";
        private static final String PHONE = "phoneNumber";
        private static final String GOAL = "goal";
        private static final String SALT = "salt";
    }

    /**
     * Class to define the column header names of Weight Table.
     */
    private static final class WeightTable {
        private static final String TABLE = "weights";
        private static final String USER = "user";
        private static final String DATE = "date";
        private static final String WEIGHT = "weight";
    }

    /**
     * Creates the tables within the database.
     * @param db SQLite database to create the tables.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + UserTable.TABLE + " (" +
                UserTable.USER + " TEXT PRIMARY KEY, " +
                UserTable.PASS + " TEXT, " +
                UserTable.PHONE + " TEXT, " +
                UserTable.GOAL + " REAL, " +
                UserTable.SALT + " TEXT)"
        );

        db.execSQL("CREATE TABLE " + WeightTable.TABLE + " (" +
                WeightTable.USER + " TEXT, " +
                WeightTable.DATE + " TEXT, " +
                WeightTable.WEIGHT + " REAL, " +
                "PRIMARY KEY (" + WeightTable.USER + ", " + WeightTable.DATE + ", " + WeightTable.WEIGHT + "), " +
                "FOREIGN KEY (" + WeightTable.USER + ") REFERENCES " + UserTable.TABLE + "(" + UserTable.USER + "))"
        );
    }

    /**
     * This method checks if the version number has been upgraded and recreates the tables.
     * @param db The database.
     * @param i Previous version number.
     * @param i1 Updated version number.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + UserTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + WeightTable.TABLE);
        onCreate(db);
    }

    /**
     * This method
     * @param user Username to be stored in the database.
     * @param pass Password to be stored in the database.
     * @return "True" if data successfully entered into database.
     */
    public boolean addUser(String user, String pass) throws NoSuchAlgorithmException,
            InvalidKeySpecException, InvalidAlgorithmParameterException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        SQLiteDatabase db = getWritableDatabase();

        //Generates random salt and key to encrypt the password before storage
        String salt = encryptor.getSalt();
        SecretKey key = Encryptor.generateKey(pass, salt);
        String encryptedPass = encryptor.encrypt(pass, key);

        ContentValues values = new ContentValues();
        values.put(UserTable.USER, user);
        values.put(UserTable.PASS, encryptedPass);
        values.put(UserTable.PHONE, "EMPTY");
        values.put(UserTable.GOAL, 0);
        values.put(UserTable.SALT, salt);

        long userId = db.insert(UserTable.TABLE, null, values);

        //Will be true if data added. False if not.
        return userId != -1;
    }

    /**
     * Method used to authenticate user credentials.
     * @param user Username
     * @param pass Password of user
     * @return "True" if user successfully authenticated.
     */
    public boolean verifyUser(String user, String pass) throws NoSuchAlgorithmException,
            InvalidKeySpecException, InvalidAlgorithmParameterException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException, InvalidKeyException {

        SQLiteDatabase db = getReadableDatabase();
        boolean verified = false;

        Cursor cursor = db.rawQuery("SELECT * FROM " + UserTable.TABLE + " WHERE " +
                UserTable.USER + " = ?", new String[]{user});

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();

            //Retrieves password salt and authenticates the password
            String salt = cursor.getString(4);
            SecretKey key = Encryptor.generateKey(pass, salt);
            String encryptedPass = encryptor.encrypt(pass, key);
            if (encryptedPass.equals(cursor.getString(1))) {
                verified = true;
            }
        }

        cursor.close();
        db.close();

        return verified;
    }

    /**
     * Method used to add a weight record to the database.
     * @param user Username of user.
     * @param date Date of weight entry.
     * @param weight Amount of weight recorded.
     * @return "True" if data successfully entered to database.
     */
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

    /**
     * Method to clear entire list of entries.
     * @param user Username of user
     * @return "True" if entries have been deleted
     */
    public boolean clearAll(String user) {
        SQLiteDatabase db = getWritableDatabase();
        int rowsDeleted = db.delete(WeightTable.TABLE, WeightTable.USER + " = ?", new String[]{user});
        db.close();

        return rowsDeleted > 0;
    }

    /**
     * Method to remove a single entry from the database.
     * @param user Username of user.
     * @param date Date of weight entry.
     * @param weight Amount of weight entry.
     * @return "True" if single entry deleted.
     */
    public boolean deleteData(String user, String date, String weight) {
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = WeightTable.USER + " = ? AND " + WeightTable.DATE + " = ? AND " + WeightTable.WEIGHT + " = ?";
        int rowsDeleted = db.delete(WeightTable.TABLE, whereClause, new String[]{user, date, weight});
        db.close();

        return rowsDeleted == 1;
    }

    /**
     * Method to update an entry in the database.
     * @param user Username of user.
     * @param date Former date to be changed.
     * @param weight Former weight to be changed.
     * @param newDate New date to be updated.
     * @param newWeight New Weight to be updated.
     * @return "True" if weight entry has been changed.
     */
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
        db.close();

        return  weightEdited;
    }

    /**
     * Method to add a phone number for a user.
     * @param user Username of user.
     * @param number Phone Number of user.
     * @return "True" if phone number successfully added to the database.
     */
    public boolean addPhoneNumber(String user, String number) {
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
        db.close();

        return numberAdded;
    }

    /**
     * Method to add a user's goal to the database.
     * @param user Username of user.
     * @param goal Goal of user to be added.
     * @return "True" if goal successfully added to the database.
     */
    public boolean setGoal(String user, int goal) {
        SQLiteDatabase db = getWritableDatabase();
        boolean goalAdded = false;

        ContentValues values = new ContentValues();
        values.put(UserTable.GOAL, goal);

        Cursor cursor = db.rawQuery("SELECT * FROM " + UserTable.TABLE + " WHERE " +
                UserTable.USER + " = ?", new String[]{user});

        //Goal has never been added so add goal
        if (cursor.getCount() == 1) {
            int rowUpdated = db.update(UserTable.TABLE, values, UserTable.USER + " = ?", new String[]{user});
            if (rowUpdated == 1) {
                goalAdded = true;
            }
        }

        cursor.close();
        db.close();

        return goalAdded;
    }

    /**
     * Method to retrieve the goal of a user
     * @param user Username of user.
     * @return The weight goal of the user.
     */
    public int getGoal(String user) {
        SQLiteDatabase db = getReadableDatabase();
        int goal;

        Cursor cursor = db.rawQuery("SELECT * FROM " + UserTable.TABLE + " WHERE " +
                UserTable.USER + " = ?", new String[]{user});

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            goal = cursor.getInt(3);
        } else {
            goal = 0;
        }

        cursor.close();
        db.close();

        return goal;
    }

    /**
     * Method to get the phone number of the user
     * @param user Username of user.
     * @return Phone number of the user.
     */
    public String getPhoneNumber(String user) {
        SQLiteDatabase db = getReadableDatabase();
        String number = "EMPTY";

        Cursor cursor = db.rawQuery("SELECT * FROM " + UserTable.TABLE + " WHERE " +
                UserTable.USER + " = ?", new String[]{user});

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            number = cursor.getString(2);
        }

        cursor.close();
        db.close();

        return number;
    }

    /**
     * Method to retrieve all the weights entered for a user.
     * @param user Username of user.
     * @return All weight entries for a user.
     */
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
        db.close();

        return weights;
    }

    /**
     * Method to retrieve all the dates of a user's weight entries.
     * @param user Username of user.
     * @return All dates of the user entries.
     */
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
        db.close();

        return dates;
    }
}
