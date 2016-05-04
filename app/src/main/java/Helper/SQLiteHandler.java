package Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ModelClasses.Student;

/**
 * Created by Daniyal Nawaz on 2/15/2016.
 */
public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "easeapplocaldb";
    private static final String TABLE_STUDENT = "student";

    private static final String KEY_REGID = "regid";
    private static final String KEY_FIRSTNAME = "firstname";
    private static final String KEY_LASTNAME = "lastname";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_CONTACT = "contact";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_ACCOUNTSTATUS = "account_status";
    //private static final String KEY_GCMID = "gcmid";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_STUDENT_TABLE = "CREATE TABLE " + TABLE_STUDENT + "("
                + KEY_REGID + " TEXT PRIMARY KEY," + KEY_FIRSTNAME + " TEXT,"
                + KEY_LASTNAME + " TEXT,"
                + KEY_EMAIL + " TEXT UNIQUE,"
                + KEY_CONTACT + " TEXT,"
                + KEY_PASSWORD + " TEXT"
                + KEY_ACCOUNTSTATUS + " TEXT"+")";
        db.execSQL(CREATE_STUDENT_TABLE);

        Log.d(TAG, "Database tables created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENT);
        onCreate(db);
    }

    public void addStudent(Student student) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_REGID, student.getRegid());
        values.put(KEY_FIRSTNAME, student.getFirstname());
        values.put(KEY_LASTNAME, student.getLastname());
        values.put(KEY_EMAIL, student.getEmail());
        values.put(KEY_CONTACT, student.getContact());
        values.put(KEY_PASSWORD, student.getPassword());
        values.put(KEY_ACCOUNTSTATUS, student.getAccountStatus());

        long id = db.insert(TABLE_STUDENT, null, values);
        db.close();

        Log.d(TAG, "New Student inserted into sqlite: " + id);
    }

    public HashMap<String, String> getStudentDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_STUDENT;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("regid", cursor.getString(0));
            user.put("firstname", cursor.getString(1));
            user.put("lastname", cursor.getString(2));
            user.put("email", cursor.getString(3));
            user.put("contact", cursor.getString(4));
            user.put("password", cursor.getString(5));
            user.put("account_status", cursor.getString(6));
        }
        cursor.close();
        db.close();

        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    // Getting All Contacts
    public List<Student> getStudent() {
        List<Student> student = new ArrayList<Student>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_STUDENT;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Student std = new Student();
                std.setRegid(cursor.getString(0));
                std.setFirstname(cursor.getString(1));
                std.setLastname(cursor.getString(2));
                std.setEmail(cursor.getString(3));
                std.setContact(cursor.getString(4));
                std.setPassword(cursor.getString(5));
                std.setAccountStatus(cursor.getString(6));

                Log.i(std.getRegid(),std.getAccountStatus());
                // Adding contact to list
                student.add(std);

                Log.i(student.size()+"","Size");
            } while (cursor.moveToNext());
        }

        // return contact list
        return student;
    }

//    public String getStudentRegId() {
//        String selectQuery = "SELECT  * FROM " + TABLE_STUDENT;
//        String regID=null;
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.rawQuery(selectQuery, null);
//
//        cursor.moveToFirst();
//        if (cursor.getCount() > 0) {
//            regID = cursor.getString(0);
//        }
//        cursor.close();
//        db.close();
//
//        Log.d(TAG, "Fetching user from Sqlite: " + regID);
//
//        return regID;
//    }

    /**
     * Re crate database Delete all tables and create them again
     * */
    public void deleteStudents() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_STUDENT, null, null);
        db.close();

        Log.d(TAG, "Deleted all students info from sqlite");
    }

}
