package com.sparq.quizpolls.application.userinterface.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

/**
 * Created by aphasingnirvana on 26/3/17.
 */

public class ApplicationContentProvider extends ContentProvider {
    static final String PROVIDER_NAME = "com.sparq.quizpolls.application.userinterface.database.ApplicationContentProvider";

    static final String TABLE_EVENT_URL = "content://" + PROVIDER_NAME + "/" + DBHelper.TABLE_EVENT;
    static final Uri TABLE_EVENT_URI = Uri.parse(TABLE_EVENT_URL);

    static final String TABLE_USER_URL = "content://" + PROVIDER_NAME + "/" + DBHelper.TABLE_USER;
    static final Uri TABLE_USER_URI = Uri.parse(TABLE_USER_URL);

    static final String TABLE_QUESTIONARE_URL = "content://" + PROVIDER_NAME + "/" + DBHelper.TABLE_QUESTIONARE;
    static final Uri TABLE_QUESTIONARE_URI = Uri.parse(TABLE_QUESTIONARE_URL);

    static final String TABLE_QUESTION_URL = "content://" + PROVIDER_NAME + "/" + DBHelper.TABLE_QUESTION;
    static final Uri TABLE_QUESTION_URI = Uri.parse(TABLE_QUESTION_URL);

    static final String TABLE_ANSWER_URL = "content://" + PROVIDER_NAME + "/" + DBHelper.TABLE_ANSWER;
    static final Uri TABLE_ANSWER_URI = Uri.parse(TABLE_ANSWER_URL);

    static final UriMatcher uriMatcher;

    static final int EVENT = 1;
    static final int EVENT_ID = 2;

    static final int USER = 8;
    static final int USER_ID = 9;


    static final int QUESTIONARE = 17;
    static final int QUESTIONARE_ID = 18;
    static final int EVENT_CREATOR_QUESTIONARE = 19;

    static final int QUESTION = 21;
    static final int QUESTION_ID = 22;
    static final int QUESTIONARE_QUESTION = 23;

    static final int ANSWER = 24;
    static final int ANSWER_ID = 25;
    static final int QUESTION_ANSWER = 26;

    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(PROVIDER_NAME, DBHelper.TABLE_EVENT, EVENT);
        uriMatcher.addURI(PROVIDER_NAME, DBHelper.TABLE_EVENT + "/#", EVENT_ID);

        uriMatcher.addURI(PROVIDER_NAME, DBHelper.TABLE_USER, USER);
        uriMatcher.addURI(PROVIDER_NAME, DBHelper.TABLE_USER + "/#", USER_ID);

        uriMatcher.addURI(PROVIDER_NAME, DBHelper.TABLE_QUESTIONARE, QUESTIONARE);
        uriMatcher.addURI(PROVIDER_NAME, DBHelper.TABLE_QUESTIONARE + "/#", QUESTIONARE_ID);
        uriMatcher.addURI(PROVIDER_NAME, DBHelper.QUESTIONARE_EVENT_ID + "/#/" + DBHelper.QUESTIONARE_CREATOR_ID
                + "/#/" + DBHelper.TABLE_QUESTIONARE, EVENT_CREATOR_QUESTIONARE);


        uriMatcher.addURI(PROVIDER_NAME, DBHelper.TABLE_QUESTION, QUESTION);
        uriMatcher.addURI(PROVIDER_NAME, DBHelper.TABLE_QUESTION + "/#", QUESTION_ID);
        uriMatcher.addURI(PROVIDER_NAME, DBHelper.QUESTIONARE_ID + "/#/" + DBHelper.TABLE_QUESTION, QUESTIONARE_QUESTION);

        uriMatcher.addURI(PROVIDER_NAME, DBHelper.TABLE_ANSWER, ANSWER);
        uriMatcher.addURI(PROVIDER_NAME, DBHelper.TABLE_ANSWER + "/#", ANSWER_ID);
        uriMatcher.addURI(PROVIDER_NAME, DBHelper.ANSWER_QUESTION_ID + "/#/" + DBHelper.TABLE_ANSWER, QUESTION_ANSWER);
    }

    /**
     * Database specific constant declarations
     */

    private SQLiteDatabase db;
    static final String DATABASE_NAME = "SPARQ_DB";

    static final String EVENT_TABLE_NAME = DBHelper.TABLE_EVENT;
    static final String USER_TABLE_NAME = DBHelper.TABLE_USER;
    static final String QUESTIONARE_TABLE_NAME = DBHelper.TABLE_QUESTIONARE;
    static final String QUESTION_TABLE_NAME = DBHelper.TABLE_QUESTION;
    static final String ANSWER_TABLE_NAME = DBHelper.TABLE_ANSWER;

    static final int DATABASE_VERSION = 1;

    private DBHelper dbHelper = null;

    /**
     * Helper class that actually creates and manages
     * the provider's underlying data repository.
     */

    @Override
    public boolean onCreate() {
        Context context = getContext();
        dbHelper = new DBHelper(context, DATABASE_NAME, DATABASE_VERSION);

        /**
         * Create a write able database which will trigger its
         * creation if it doesn't already exist.
         */

        db = dbHelper.getWritableDatabase();

        return (db == null)? false:true;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        long rowID = 0; //Number of rows inserted
        Uri _uri = null; //URI to signify the insertion

        switch(uriMatcher.match(uri)){
            case EVENT:
                rowID = db.insert(EVENT_TABLE_NAME, "", values);

                if(rowID > 0){
                    _uri = ContentUris.withAppendedId(TABLE_EVENT_URI, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }
                else{
                    throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
                }

                break;

            case USER:
                rowID = db.insert(USER_TABLE_NAME, "", values);

                if(rowID > 0){
                    _uri = ContentUris.withAppendedId(TABLE_USER_URI, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }
                else{
                    throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
                }

                break;

            case QUESTIONARE:
                rowID = db.insert(QUESTIONARE_TABLE_NAME, "", values);

                if(rowID > 0){
                    _uri = ContentUris.withAppendedId(TABLE_QUESTIONARE_URI, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }
                else{
                    throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
                }

                break;

            case QUESTION:
                rowID = db.insert(QUESTION_TABLE_NAME, "", values);

                if(rowID > 0){
                    _uri = ContentUris.withAppendedId(TABLE_QUESTION_URI, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }
                else{
                    throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
                }

                break;

            case ANSWER:
                rowID = db.insert(ANSWER_TABLE_NAME, "", values);

                if(rowID > 0){
                    _uri = ContentUris.withAppendedId(TABLE_ANSWER_URI, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }
                else{
                    throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
                }

                break;

            default:
                throw new SQLException("Unknown uri: " + uri);
        }

        return _uri;
    }

    @Override
    public Cursor query(Uri uri, String[] projection,
                        String selection,String[] selectionArgs, String sortOrder) {

        Cursor retCursor;

        switch (uriMatcher.match(uri)){
            case EVENT:
                retCursor = db.query(
                        EVENT_TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case EVENT_ID:
                long _id = ContentUris.parseId(uri);
                retCursor = db.query(
                        EVENT_TABLE_NAME,
                        projection,
                        DBHelper.EVENT_ID + " = ? ",
                        new String[]{String.valueOf(_id)},
                        null,
                        null,
                        sortOrder
                );
                break;

            case USER:
                retCursor = db.query(
                        USER_TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case USER_ID:
                long _user_id = ContentUris.parseId(uri);
                retCursor = db.query(
                        USER_TABLE_NAME,
                        projection,
                        DBHelper.EVENT_ID + " = ? ",
                        new String[]{String.valueOf(_user_id)},
                        null,
                        null,
                        sortOrder
                );
                break;

            case QUESTIONARE:
                retCursor = db.query(
                        QUESTIONARE_TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case QUESTIONARE_ID:
                long _questionare_id = ContentUris.parseId(uri);
                retCursor = db.query(
                        QUESTIONARE_TABLE_NAME,
                        projection,
                        DBHelper.EVENT_ID + " = ? ",
                        new String[]{String.valueOf(_questionare_id)},
                        null,
                        null,
                        sortOrder
                );
                break;

            case EVENT_CREATOR_QUESTIONARE:
                String _questionare_event_id = uri.getPathSegments().get(1);
                String _questionare_creator_id = uri.getPathSegments().get(3);

                retCursor = db.query(
                        QUESTIONARE_TABLE_NAME,
                        projection,
                        DBHelper.QUESTIONARE_EVENT_ID + " = ? AND "
                                + DBHelper.QUESTIONARE_CREATOR_ID + " = ? ",
                        new String[]{_questionare_event_id, _questionare_creator_id},
                        null,
                        null,
                        sortOrder
                );
                break;

            case QUESTION:
                retCursor = db.query(
                        QUESTION_TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case QUESTION_ID:
                long _question_id = ContentUris.parseId(uri);
                retCursor = db.query(
                        QUESTION_TABLE_NAME,
                        projection,
                        DBHelper.EVENT_ID + " = ? ",
                        new String[]{String.valueOf(_question_id)},
                        null,
                        null,
                        sortOrder
                );
                break;

            case QUESTIONARE_QUESTION:
                String _question_questionare_id = uri.getPathSegments().get(1);
                retCursor = db.query(
                        QUESTION_TABLE_NAME,
                        projection,
                        DBHelper.QUESTIONARE_ID + " = ? ",
                        new String[]{_question_questionare_id},
                        null,
                        null,
                        sortOrder
                );
                break;

            case ANSWER:
                retCursor = db.query(
                        ANSWER_TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case ANSWER_ID:
                long _answer_id = ContentUris.parseId(uri);
                retCursor = db.query(
                        ANSWER_TABLE_NAME,
                        projection,
                        DBHelper.EVENT_ID + " = ? ",
                        new String[]{String.valueOf(_answer_id)},
                        null,
                        null,
                        sortOrder
                );
                break;

            case QUESTION_ANSWER:
                String _answer_question_id = uri.getPathSegments().get(1);
                retCursor = db.query(
                        ANSWER_TABLE_NAME,
                        projection,
                        DBHelper.ANSWER_QUESTION_ID + " = ? ",
                        new String[]{_answer_question_id},
                        null,
                        null,
                        sortOrder
                );
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }

        /*Set the notification URI for the cursor to the one passed into the function. This
        causes the cursor to register a content observer to watch for changes that happen to
        this URI and any of it's descendants. By descendants, we mean any URI that begins
        with this path.*/

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int affectedRows = 0;

        switch (uriMatcher.match(uri)){
            case EVENT:
                affectedRows = db.delete(EVENT_TABLE_NAME, selection, selectionArgs);
                break;

            case EVENT_ID:
                String _event_id = String.valueOf(ContentUris.parseId(uri));
                affectedRows = db.delete( EVENT_TABLE_NAME, EVENT_ID +  " = " + _event_id +
                                (!TextUtils.isEmpty(selection) ? "AND ("
                                        + selection + ')' : ""), selectionArgs);
                break;

            case USER:
                affectedRows = db.delete(USER_TABLE_NAME, selection, selectionArgs);
                break;

            case USER_ID:
                String _user_id = String.valueOf(ContentUris.parseId(uri));
                affectedRows = db.delete( USER_TABLE_NAME, USER_ID +  " = " + _user_id +
                        (!TextUtils.isEmpty(selection) ? "AND ("
                                + selection + ')' : ""), selectionArgs);
                break;

            case QUESTIONARE:
                affectedRows = db.delete(QUESTIONARE_TABLE_NAME, selection, selectionArgs);
                break;

            case QUESTIONARE_ID:
                String _questionare_id = String.valueOf(ContentUris.parseId(uri));
                affectedRows = db.delete( QUESTIONARE_TABLE_NAME, QUESTIONARE_ID +  " = " + _questionare_id +
                        (!TextUtils.isEmpty(selection) ? "AND ("
                                + selection + ')' : ""), selectionArgs);
                break;

            case QUESTION:
                affectedRows = db.delete(QUESTION_TABLE_NAME, selection, selectionArgs);
                break;

            case QUESTION_ID:
                String _question_id = String.valueOf(ContentUris.parseId(uri));
                affectedRows = db.delete( QUESTION_TABLE_NAME, QUESTION_ID +  " = " + _question_id +
                        (!TextUtils.isEmpty(selection) ? "AND ("
                                + selection + ')' : ""), selectionArgs);
                break;

            case ANSWER:
                affectedRows = db.delete(ANSWER_TABLE_NAME, selection, selectionArgs);
                break;

            case ANSWER_ID:
                String _answer_id = String.valueOf(ContentUris.parseId(uri));
                affectedRows = db.delete( ANSWER_TABLE_NAME, ANSWER_ID +  " = " + _answer_id +
                        (!TextUtils.isEmpty(selection) ? "AND ("
                                + selection + ')' : ""), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        if(affectedRows != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return affectedRows;
    }

    @Override
    public int update(Uri uri, ContentValues values,
                      String selection, String[] selectionArgs) {
        int affectedRows = 0;

        switch (uriMatcher.match(uri)) {
            case EVENT:
                affectedRows = db.update(EVENT_TABLE_NAME, values, selection, selectionArgs);
                break;

            case EVENT_ID:
                affectedRows = db.update(EVENT_TABLE_NAME, values,
                        DBHelper.EVENT_ID + " = " + uri.getPathSegments().get(1) +
                                (!TextUtils.isEmpty(selection) ? "AND ("
                                        +selection + ')' : ""), selectionArgs);
                break;

            case USER:
                affectedRows = db.update(USER_TABLE_NAME, values, selection, selectionArgs);
                break;

            case USER_ID:
                affectedRows = db.update(USER_TABLE_NAME, values,
                        DBHelper.USER_ID + " = " + uri.getPathSegments().get(1) +
                                (!TextUtils.isEmpty(selection) ? "AND ("
                                        +selection + ')' : ""), selectionArgs);
                break;

            case QUESTIONARE:
                affectedRows = db.update(QUESTIONARE_TABLE_NAME, values, selection, selectionArgs);
                break;

            case QUESTIONARE_ID:
                affectedRows = db.update(QUESTIONARE_TABLE_NAME, values,
                        DBHelper.QUESTIONARE_ID + " = " + uri.getPathSegments().get(1) +
                                (!TextUtils.isEmpty(selection) ? "AND ("
                                        +selection + ')' : ""), selectionArgs);
                break;

            case QUESTION:
                affectedRows = db.update(QUESTION_TABLE_NAME, values, selection, selectionArgs);
                break;

            case QUESTION_ID:
                affectedRows = db.update(QUESTION_TABLE_NAME, values,
                        DBHelper.QUESTION_ID + " = " + uri.getPathSegments().get(1) +
                                (!TextUtils.isEmpty(selection) ? "AND ("
                                        +selection + ')' : ""), selectionArgs);
                break;

            case ANSWER:
                affectedRows = db.update(ANSWER_TABLE_NAME, values, selection, selectionArgs);
                break;

            case ANSWER_ID:
                affectedRows = db.update(ANSWER_TABLE_NAME, values,
                        DBHelper.ANSWER_ID + " = " + uri.getPathSegments().get(1) +
                                (!TextUtils.isEmpty(selection) ? "AND ("
                                        +selection + ')' : ""), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri );
        }

        if(affectedRows != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return affectedRows;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){
            case EVENT:
                return "vnd.android.cursor.dir/vnd.com.sparq.quizpolls.application.event";
            case EVENT_ID:
                return "vnd.android.cursor.item/vnd.com.sparq.quizpolls.application.event.id";
            case USER:
                return "vnd.android.cursor.dir/vnd.com.sparq.quizpolls.application.user";
            case USER_ID:
                return "vnd.android.cursor.item/vnd.com.sparq.quizpolls.application.user.id";
            case QUESTIONARE:
                return "vnd.android.cursor.dir/vnd.com.sparq.quizpolls.application.questionare";
            case QUESTIONARE_ID:
                return "vnd.android.cursor.item/vnd.com.sparq.quizpolls.application.questionare.id";
            case EVENT_CREATOR_QUESTIONARE:
                return "vnd.android.cursor.item/vnd.com.sparq.quizpolls.application.event.id.creator.id";
            case QUESTION:
                return "vnd.android.cursor.dir/vnd.com.sparq.quizpolls.application.question";
            case QUESTION_ID:
                return "vnd.android.cursor.item/vnd.com.sparq.quizpolls.application.question.id";
            case QUESTIONARE_QUESTION:
                return "vnd.android.cursor.dir/vnd.com.sparq.quizpolls.application.question";
            case ANSWER:
                return "vnd.android.cursor.dir/vnd.com.sparq.quizpolls.application.answer";
            case ANSWER_ID:
                return "vnd.android.cursor.item/vnd.com.sparq.quizpolls.application.answer.id";
            case QUESTION_ANSWER:
                return "vnd.android.cursor.dir/vnd.com.sparq.quizpolls.application.answer.question.id";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }
}

