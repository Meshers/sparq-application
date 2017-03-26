package com.sparq.application.userinterface.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sparq.application.userinterface.model.EventItem;

/**
 * Created by sarahcs on 3/23/2017.
 */

public class DBHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "SPARQ_DB";

    // Event Table and column names
    private static final String TABLE_EVENT = "event";
    private static final String EVENT_ID = "id";
    private static final String EVENT_CODE = "event_code";
    private static final String EVENT_NAME = "name";
    private static final String EVENT_AGENDA = "agenda";
    private static final String EVENT_VENUE = "venue";
    private static final String EVENT_DATE = "date";
    private static final String EVENT_DURATION = "duration";

    // User Table and column names
    private static final String TABLE_USER = "user";
    private static final String USER_ID = "id";
    private static final String USER_NAME = "username";
    private static final String USER_PASSWORD = "password";
    private static final String USER_FIRST_NAME = "first_name";
    private static final String USER_LAST_NAME = "last_name";
    private static final String USER_EMAIL = "email";
    private static final String USER_DESCRIPTION = "description";
    private static final String USER_IMAGE = "image";

    // Questionare Table and column names
    private static final String TABLE_QUESTIONARE = "questionare";
    private static final String QUESTIONARE_ID = "id";
    private static final String QUESTIONARE_EVENT_ID = "event_id";
    private static final String QUESTIONARE_TYPE = "type";
    private static final String QUESTIONARE_NAME = "name";
    private static final String QUESTIONARE_DESCRIPTION = "description";
    private static final String QUESTIONARE_DATE = "date";
    private static final String QUESTIONARE_CREATOR_ID = "creator_id";
    private static final String QUESTIONARE_MAX_MARKS = "max_marks";
    private static final String QUESTIONARE_STATE = "state";
    private static final String QUESTIONARE_DURATION = "duration";

    // Question table and column names
    private static final String TABLE_QUESTION = "question";
    private static final String QUESTION_ID = "id";
    private static final String QUESTION_QUESTIONARE_ID = "questionare_id";
    private static final String QUESTION_FORMAT = "format";
    private static final String QUESTION_STRING = "question_string";
    private static final String QUESTION_TOTAL_MARKS = "total_marks";

    // Answer table and column names
    private static final String TABLE_ANSWER = "answer";
    private static final String ANSWER_ID = "id";
    private static final String ANSWER_QUESTION_ID = "question_id";
    private static final String ANSWER_STRING = "answer_string";
    private static final String ANSWER_OPTION = "option";

    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){

        String CREATE_EVENT_TABLE = "CREATE TABLE " + TABLE_EVENT + "("
                + EVENT_ID + " INTEGER PRIMARY KEY,"
                + EVENT_CODE + " TEXT,"
                + EVENT_NAME + " TEXT,"
                + EVENT_AGENDA + " TEXT,"
                + EVENT_VENUE + " TEXT,"
                + EVENT_DATE + " DATETIME,"
                + EVENT_DURATION + " LONG" + ")";
        db.execSQL(CREATE_EVENT_TABLE);

        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + USER_ID + " INTEGER PRIMARY KEY,"
                + USER_NAME + " TEXT,"
                + USER_PASSWORD + " TEXT,"
                + USER_FIRST_NAME + " TEXT,"
                + USER_LAST_NAME + " TEXT,"
                + USER_EMAIL + " TEXT,"
                + USER_DESCRIPTION + " TEXT,"
                + USER_IMAGE + " BLOB" + ")";
        db.execSQL(CREATE_USER_TABLE);

        String CREATE_QUESTIONARE_TABLE = "CREATE TABLE " + TABLE_QUESTIONARE + "("
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
                + " FOREIGN KEY (" + QUESTIONARE_EVENT_ID + ") REFERENCES " + TABLE_EVENT + "(" + EVENT_CODE + ")"
                + " FOREIGN KEY (" + QUESTIONARE_CREATOR_ID + ") REFERENCES " + TABLE_USER + "(" + USER_ID + "));";
        db.execSQL(CREATE_QUESTIONARE_TABLE);

        String CREATE_QUESTION_TABLE = "CREATE TABLE " + TABLE_QUESTION + "("
                + QUESTION_ID + " INTEGER PRIMARY KEY,"
                + QUESTION_STRING + " TEXT,"
                + QUESTION_FORMAT + " INTEGER,"
                + QUESTION_TOTAL_MARKS + " INTEGER,"
                + QUESTION_QUESTIONARE_ID + " INTEGER,"
                + " FOREIGN KEY (" + QUESTION_QUESTIONARE_ID + ") REFERENCES " + TABLE_QUESTIONARE + "(" + QUESTIONARE_ID + "));";
        db.execSQL(CREATE_QUESTION_TABLE);

        String CREATE_ANSWER_TABLE = "CREATE TABLE " + TABLE_ANSWER + "("
                + ANSWER_ID + " INTEGER PRIMARY KEY,"
                + ANSWER_STRING + " TEXT,"
                + ANSWER_OPTION + " TEXT,"
                + ANSWER_QUESTION_ID + " INTEGER,"
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

//    public void addEvent(EventItem event){
//        db.execSQL("DROP TABLE IF EXISTS ");
//    }

}
