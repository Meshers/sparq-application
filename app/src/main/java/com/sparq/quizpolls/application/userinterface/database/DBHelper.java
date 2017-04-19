package com.sparq.quizpolls.application.userinterface.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sarahcs on 3/23/2017.
 */

public class DBHelper extends SQLiteOpenHelper {

    // Database Version
    private static int DATABASE_VERSION = 0;

    // Database Name
    private static String DATABASE_NAME = "";

    // Event Table and column names
    public static final String TABLE_EVENT = "event";
    public static final String EVENT_ID = "id";
    public static final String EVENT_NAME = "name";
    public static final String EVENT_AGENDA = "agenda";
    public static final String EVENT_VENUE = "venue";
    public static final String EVENT_DATE = "date";
    public static final String EVENT_DURATION = "duration";

    // User Table and column names
    public static final String TABLE_USER = "user";
    public static final String USER_ID = "id";
    public static final String USER_NAME = "username";
    public static final String USER_PASSWORD = "password";
    public static final String USER_FIRST_NAME = "first_name";
    public static final String USER_LAST_NAME = "last_name";
    public static final String USER_EMAIL = "email";
    public static final String USER_DESCRIPTION = "description";
    public static final String USER_IMAGE = "image";

    // Questionare Table and column names
    public static final String TABLE_QUESTIONARE = "questionare";
    public static final String QUESTIONARE_ID = "id";
    public static final String QUESTIONARE_EVENT_ID = "event_id";
    public static final String QUESTIONARE_TYPE = "type";
    public static final String QUESTIONARE_NAME = "name";
    public static final String QUESTIONARE_DESCRIPTION = "description";
    public static final String QUESTIONARE_DATE = "date";
    public static final String QUESTIONARE_CREATOR_ID = "creator_id";
    public static final String QUESTIONARE_MAX_MARKS = "max_marks";
    public static final String QUESTIONARE_STATE = "state";
    public static final String QUESTIONARE_DURATION = "duration";

    // Question table and column names
    public static final String TABLE_QUESTION = "question";
    public static final String QUESTION_ID = "id";
    public static final String QUESTION_QUESTIONARE_ID = "questionare_id";
    public static final String QUESTION_FORMAT = "format";
    public static final String QUESTION_STRING = "question_string";
    public static final String QUESTION_TOTAL_MARKS = "total_marks";
    public static final String QUESTION_VOTES = "votes";

    // Answer table and column names
    public static final String TABLE_ANSWER = "answer";
    public static final String ANSWER_ID = "id";
    public static final String ANSWER_QUESTION_ID = "question_id";
    public static final String ANSWER_STRING = "answer_string";
    public static final String ANSWER_OPTION = "option";
    public static final String ANSWER_VOTES = "votes";

    public DBHelper(Context context, String DatabaseName, int DatabaseVersion){
        super(context, DatabaseName, null, DatabaseVersion);
        DATABASE_NAME = DatabaseName;
        DATABASE_VERSION = DatabaseVersion;
    }

    @Override
    public void onCreate(SQLiteDatabase db){

        String CREATE_EVENT_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_EVENT + "("
                + EVENT_ID + " INTEGER PRIMARY KEY,"
                + EVENT_NAME + " TEXT,"
                + EVENT_AGENDA + " TEXT,"
                + EVENT_VENUE + " TEXT,"
                + EVENT_DATE + " DATETIME,"
                + EVENT_DURATION + " LONG" + ")";
        db.execSQL(CREATE_EVENT_TABLE);

        String CREATE_USER_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_USER + "("
                + USER_ID + " INTEGER PRIMARY KEY,"
                + USER_NAME + " TEXT,"
                + USER_PASSWORD + " TEXT,"
                + USER_FIRST_NAME + " TEXT,"
                + USER_LAST_NAME + " TEXT,"
                + USER_EMAIL + " TEXT,"
                + USER_DESCRIPTION + " TEXT,"
                + USER_IMAGE + " BLOB" + ")";
        db.execSQL(CREATE_USER_TABLE);

        String CREATE_QUESTIONARE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_QUESTIONARE + "("
                + QUESTIONARE_ID + " INTEGER PRIMARY KEY,"
                + QUESTIONARE_NAME + " TEXT,"
                + QUESTIONARE_DESCRIPTION + " TEXT,"
                + QUESTIONARE_DATE + " DATETIME,"
                + QUESTIONARE_TYPE + " INTEGER,"
                + QUESTIONARE_DURATION + " LONG,"
                + QUESTIONARE_MAX_MARKS + " LONG,"
                + QUESTIONARE_STATE+ " INTEGER,"
                + QUESTIONARE_EVENT_ID + " INTEGER,"
                + QUESTIONARE_CREATOR_ID + " INTEGER,"
                + " FOREIGN KEY (" + QUESTIONARE_EVENT_ID + ") REFERENCES " + TABLE_EVENT + "(" + EVENT_ID + ")"
                + " FOREIGN KEY (" + QUESTIONARE_CREATOR_ID + ") REFERENCES " + TABLE_USER + "(" + USER_ID + "));";
        db.execSQL(CREATE_QUESTIONARE_TABLE);

        String CREATE_QUESTION_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_QUESTION + "("
                + QUESTION_ID + " INTEGER PRIMARY KEY,"
                + QUESTION_STRING + " TEXT,"
                + QUESTION_FORMAT + " INTEGER,"
                + QUESTION_TOTAL_MARKS + " INTEGER,"
                + QUESTION_QUESTIONARE_ID + " INTEGER,"
                + QUESTION_VOTES + " INTEGER,"
                + " FOREIGN KEY (" + QUESTION_QUESTIONARE_ID + ") REFERENCES " + TABLE_QUESTIONARE + "(" + QUESTIONARE_ID + "));";
        db.execSQL(CREATE_QUESTION_TABLE);

        String CREATE_ANSWER_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_ANSWER + "("
                + ANSWER_ID + " INTEGER PRIMARY KEY,"
                + ANSWER_STRING + " TEXT,"
                + ANSWER_OPTION + " TEXT,"
                + ANSWER_QUESTION_ID + " INTEGER,"
                + ANSWER_VOTES + " INTEGER,"
                + " FOREIGN KEY (" + ANSWER_QUESTION_ID + ") REFERENCES " + TABLE_QUESTION + "(" + QUESTION_ID + "));";
        db.execSQL(CREATE_ANSWER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTIONARE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ANSWER);

        // Create tables again
        onCreate(db);
    }

}
