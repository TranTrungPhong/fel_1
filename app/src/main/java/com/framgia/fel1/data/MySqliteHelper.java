package com.framgia.fel1.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import com.framgia.fel1.model.Answer;
import com.framgia.fel1.model.Category;
import com.framgia.fel1.model.Lesson;
import com.framgia.fel1.model.Result;
import com.framgia.fel1.model.User;
import com.framgia.fel1.model.UserActivity;
import com.framgia.fel1.model.Word;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by vuduychuong1994 on 4/18/16.
 */
public class MySqliteHelper extends SQLiteOpenHelper {
    //Database Config
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "EnglishLearning.db";
    //Table name
    public static final String TABLE_USER = "user";
    public static final String TABLE_USER_ACTIVITY = "user_activity";
    public static final String TABLE_CATEGORY = "category";
    public static final String TABLE_LESSON = "lesson";
    public static final String TABLE_WORD = "word";
    public static final String TABLE_ANSWER = "answer";
    public static final String TABLE_RESULT = "result";
    //Column name
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_AVATAR = "avatar";
    public static final String COLUMN_ADMIN = "admin";
    public static final String COLUMN_AUTH_TOKEN = "auth_token";
    public static final String COLUMN_CREATED_AT = "created_at";
    public static final String COLUMN_UPDATED_AT = "updated_at";
    public static final String COLUMN_LEARNED_WORDS = "learned_words";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_PHOTO = "photo";
    public static final String COLUMN_LESSON_ID = "lesson_id";
    public static final String COLUMN_RESULT_ID = "result_id";
    public static final String COLUMN_WORD_ID = "word_id";
    public static final String COLUMN_IS_CORRECT = "is_correct";
    public static final String COLUMN_ID_USER = "id_user";
    public static final String COLUMN_ID_LESSON = "id_lesson";
    public static final String COLUMN_ID_WORD = "id_word";
    public static final String COLUMN_ID_ANSWER = "id_answer";

    public MySqliteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuffer createUserTable =
                new StringBuffer().append("CREATE TABLE ").append(TABLE_USER + " (").append(
                        COLUMN_ID + " INTEGER PRIMARY KEY, ").append(COLUMN_NAME).append(
                        " TEXT, ").append(COLUMN_EMAIL).append(" TEXT, ").append(
                        COLUMN_AVATAR).append(" TEXT, ").append(COLUMN_ADMIN).append(
                        " INTEGER, ").append(COLUMN_AUTH_TOKEN).append(" TEXT, ").append(
                        COLUMN_CREATED_AT).append(" TEXT, ").append(COLUMN_UPDATED_AT).append(
                        " TEXT, ").append(COLUMN_LEARNED_WORDS).append(" INTEGER)");
        StringBuffer createUserActivityTable = new StringBuffer().append("CREATE TABLE ").append(
                TABLE_USER_ACTIVITY + " (").append(COLUMN_ID + " INTEGER PRIMARY KEY, ").append(
                COLUMN_CONTENT).append(" TEXT, ").append(COLUMN_CREATED_AT).append(" TEXT)");
        StringBuffer createCategoryTable =
                new StringBuffer().append("CREATE TABLE ").append(TABLE_CATEGORY + " (").append(
                        COLUMN_ID + " INTEGER PRIMARY KEY, ").append(COLUMN_NAME).append(
                        " TEXT, ").append(COLUMN_PHOTO).append(" TEXT, ").append(
                        COLUMN_LEARNED_WORDS).append(" INTEGER)");
        StringBuffer createLessonTable =
                new StringBuffer().append("CREATE TABLE ").append(TABLE_LESSON + " (").append(
                        COLUMN_ID + " INTEGER PRIMARY KEY, ").append(COLUMN_NAME).append(" TEXT)");
        StringBuffer createWordTable =
                new StringBuffer().append("CREATE TABLE ").append(TABLE_WORD + " (").append(
                        COLUMN_ID + " INTEGER PRIMARY KEY, ").append(COLUMN_LESSON_ID).append(
                        " INTEGER, ").append(COLUMN_RESULT_ID).append(" INTEGER, ").append(
                        COLUMN_CONTENT).append(" TEXT)");
        StringBuffer createAnswerTable =
                new StringBuffer().append("CREATE TABLE ").append(TABLE_ANSWER + " (")
                        .append(COLUMN_ID + " INTEGER PRIMARY KEY, ")
                        .append(COLUMN_WORD_ID).append(" INTEGER, ")
                        .append(COLUMN_CONTENT).append(" INTEGER, ")
                        .append(COLUMN_IS_CORRECT).append(" INTEGER)");
        StringBuffer createResultTable =
                new StringBuffer().append("CREATE TABLE ").append(TABLE_RESULT + " (")
                        .append(COLUMN_ID + " INTEGER PRIMARY KEY, ")
                        .append(COLUMN_ID_USER).append(" INTEGER, ")
                        .append(COLUMN_ID_LESSON).append(" INTEGER, ")
                        .append(COLUMN_ID_WORD).append(" INTEGER, ")
                        .append(COLUMN_ID_ANSWER).append(" INTEGER)");
        db.execSQL(createUserTable.toString());
        db.execSQL(createUserActivityTable.toString());
        db.execSQL(createCategoryTable.toString());
        db.execSQL(createLessonTable.toString());
        db.execSQL(createWordTable.toString());
        db.execSQL(createAnswerTable.toString());
        db.execSQL(createResultTable.toString());

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_ACTIVITY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LESSON);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORD);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ANSWER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESULT);
        onCreate(db);
    }

    //region USER
    // Add User
    public long addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        if ( user != null ) {
            cv.put(COLUMN_ID, user.getId());
            cv.put(COLUMN_NAME, user.getName());
            cv.put(COLUMN_EMAIL, user.getEmail());
            cv.put(COLUMN_AVATAR, user.getAvatar());
            cv.put(COLUMN_ADMIN, user.getAdmin());
            cv.put(COLUMN_AUTH_TOKEN, user.getAuthToken());
            cv.put(COLUMN_CREATED_AT, user.getCreatedAt());
            cv.put(COLUMN_UPDATED_AT, user.getUpdatedAt());
            cv.put(COLUMN_LEARNED_WORDS, user.getLearnedWords());
        }
        return db.insert(TABLE_USER, null, cv);
    }

    // Read User
    public User getUser(int id) {
        User user = new User();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =
                db.query(TABLE_USER, null, COLUMN_ID + " = ?", new String[]{String.valueOf(id)},
                         null, null, null);
        if ( cursor != null && cursor.moveToFirst() ) {
            user.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
            user.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
            user.setEmail(cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL)));
            user.setAvatar(cursor.getString(cursor.getColumnIndex(COLUMN_AVATAR)));
            user.setAdmin(cursor.getInt(cursor.getColumnIndex(COLUMN_ADMIN)) == 1);
            user.setAuthToken(cursor.getString(cursor.getColumnIndex(COLUMN_AUTH_TOKEN)));
            user.setCreatedAt(cursor.getString(cursor.getColumnIndex(COLUMN_CREATED_AT)));
            user.setUpdatedAt(cursor.getString(cursor.getColumnIndex(COLUMN_UPDATED_AT)));
            user.setLearnedWords(cursor.getInt(cursor.getColumnIndex(COLUMN_LEARNED_WORDS)));
        }
        return user;
    }

    // Update User
    public long updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        if ( user != null ) {
            cv.put(COLUMN_NAME, user.getName());
            cv.put(COLUMN_EMAIL, user.getEmail());
            cv.put(COLUMN_AVATAR, user.getAvatar());
            cv.put(COLUMN_ADMIN, user.getAdmin());
            cv.put(COLUMN_AUTH_TOKEN, user.getAuthToken());
            cv.put(COLUMN_CREATED_AT, user.getCreatedAt());
            cv.put(COLUMN_UPDATED_AT, user.getUpdatedAt());
            cv.put(COLUMN_LEARNED_WORDS, user.getLearnedWords());
        }
        return db.update(TABLE_USER, cv, COLUMN_ID + " = ?",
                         new String[]{String.valueOf(user.getId())});
    }
    //endregion

    //region USERACTIVITY
    // Add UserActivity
    public long addUserActivity(UserActivity userActivity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        if ( userActivity != null ) {
            cv.put(COLUMN_ID, userActivity.getId());
            cv.put(COLUMN_CONTENT, userActivity.getContent());
            cv.put(COLUMN_CREATED_AT, userActivity.getCreated_at());
        }
        return db.insert(TABLE_USER_ACTIVITY, null, cv);
    }

    // Read UserActivity
    public UserActivity getUserActivity(int id) {
        UserActivity userActivity = new UserActivity();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER_ACTIVITY, null, COLUMN_ID + " = ?",
                                 new String[]{String.valueOf(id)}, null, null, null);
        if ( cursor != null && cursor.moveToFirst() ) {
            userActivity.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
            userActivity.setContent(cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT)));
            userActivity.setCreated_at(cursor.getString(cursor.getColumnIndex(COLUMN_CREATED_AT)));
        }
        return userActivity;
    }

    // Update UserActivity
    public long updateUserActivity(UserActivity userActivity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        if ( userActivity != null ) {
            cv.put(COLUMN_ID, userActivity.getId());
            cv.put(COLUMN_CONTENT, userActivity.getContent());
            cv.put(COLUMN_CREATED_AT, userActivity.getCreated_at());
        }
        return db.update(TABLE_USER_ACTIVITY, cv, COLUMN_ID + " = ?",
                         new String[]{String.valueOf(userActivity.getId())});
    }
    //endregion

    //region CATEGORY
    // Add Category
    public long addCategory(Category category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        if ( category != null ) {
            cv.put(COLUMN_ID, category.getId());
            cv.put(COLUMN_NAME, category.getName());
            cv.put(COLUMN_PHOTO, category.getPhoto());
            cv.put(COLUMN_LEARNED_WORDS, category.getLearnWords());
        }
        return db.insert(TABLE_CATEGORY, null, cv);
    }

    // Read Category
    public Category getCategory(int id) {
        Category category = new Category();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =
                db.query(TABLE_CATEGORY, null, COLUMN_ID + " = ?", new String[]{String.valueOf(id)},
                         null, null, null);
        if ( cursor != null && cursor.moveToFirst() ) {
            category.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
            category.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
            category.setPhoto(cursor.getString(cursor.getColumnIndex(COLUMN_PHOTO)));
            category.setLearnWords(cursor.getString(cursor.getColumnIndex(COLUMN_LEARNED_WORDS)));
        }
        return category;
    }

    // Update Category
    public long updateCategory(Category category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        if ( category != null ) {
            cv.put(COLUMN_ID, category.getId());
            cv.put(COLUMN_NAME, category.getName());
            cv.put(COLUMN_PHOTO, category.getPhoto());
            cv.put(COLUMN_LEARNED_WORDS, category.getLearnWords());
        }
        return db.update(TABLE_CATEGORY, cv, COLUMN_ID + " = ?",
                         new String[]{String.valueOf(category.getId())});
    }
    //endregion

    //region Lesson
    //Add Lesson
    public long addLesson(Lesson lesson) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        if ( lesson != null ) {
            cv.put(COLUMN_ID, lesson.getId());
            cv.put(COLUMN_NAME, lesson.getName());
        }
        return db.insert(TABLE_LESSON, null, cv);
    }

    // Read Lesson
    public Lesson getLesson(int id) {
        Lesson lesson = new Lesson();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =
                db.query(TABLE_LESSON, null, COLUMN_ID + " = ?", new String[]{String.valueOf(id)},
                         null, null, null);
        if ( cursor != null && cursor.moveToFirst() ) {
            lesson.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
            lesson.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
        }
        return lesson;
    }

    // Update Lesson
    public long updateLesson(Lesson lesson) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        if ( lesson != null ) {
            cv.put(COLUMN_ID, lesson.getId());
            cv.put(COLUMN_NAME, lesson.getName());
        }
        return db.update(TABLE_LESSON, cv, COLUMN_ID + " = ?",
                         new String[]{String.valueOf(lesson.getId())});
    }
    //endregion

    //region WORD
    //Add Word
    public long addWord(Word word) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        if ( word != null ) {
            cv.put(COLUMN_ID, word.getId());
            cv.put(COLUMN_LESSON_ID, word.getLessonId());
            cv.put(COLUMN_RESULT_ID, word.getResultId());
            cv.put(COLUMN_CONTENT, word.getContent());
        }
        return db.insert(TABLE_WORD, null, cv);
    }

    // Read Word
    public Word getWord(int id) {
        Word word = new Word();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =
                db.query(TABLE_WORD, null, COLUMN_ID + " = ?", new String[]{String.valueOf(id)},
                         null, null, null);
        if ( cursor != null && cursor.moveToFirst() ) {
            word.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
            word.setLessonId(cursor.getInt(cursor.getColumnIndex(COLUMN_LESSON_ID)));
            word.setResultId(cursor.getInt(cursor.getColumnIndex(COLUMN_RESULT_ID)));
            word.setContent(cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT)));
        }
        return word;
    }

    public List<Word> getListWordByLesson(int idLesson) {
        List<Word> wordList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_WORD, null, COLUMN_LESSON_ID + " = ?",
                                 new String[]{String.valueOf(idLesson)}, null, null, null);
        if ( cursor.getCount() > 0 && cursor.moveToFirst() ) {
            do {
                Word word = new Word();
                word.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                word.setContent(cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT)));
                word.setLessonId(cursor.getInt(cursor.getColumnIndex(COLUMN_LESSON_ID)));
                word.setResultId(cursor.getInt(cursor.getColumnIndex(COLUMN_RESULT_ID)));
                word.setAnswers(getListAnswerByWord(word.getId()));
                wordList.add(word);
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return wordList;
    }

    // Update Word
    public long updateWord(Word word) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        if ( word != null ) {
            cv.put(COLUMN_ID, word.getId());
            cv.put(COLUMN_LESSON_ID, word.getLessonId());
            cv.put(COLUMN_RESULT_ID, word.getResultId());
            cv.put(COLUMN_CONTENT, word.getContent());
        }
        return db.update(TABLE_WORD, cv, COLUMN_ID + " = ?",
                         new String[]{String.valueOf(word.getId())});
    }
    //endregion

    //region ANSWER
    //Add Answer
    public long addAnswer(Answer answer) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        if ( answer != null ) {
            cv.put(COLUMN_ID, answer.getId());
            cv.put(COLUMN_WORD_ID, answer.getWordId());
            cv.put(COLUMN_CONTENT, answer.getContent());
            cv.put(COLUMN_IS_CORRECT, answer.getCorrect());
        }
        return db.insert(TABLE_ANSWER, null, cv);
    }

    // Read Answer
    public Answer getAnswer(int id) {
        Answer answer = new Answer();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =
                db.query(TABLE_ANSWER, null, COLUMN_ID + " = ?", new String[]{String.valueOf(id)},
                         null, null, null);
        if ( cursor != null && cursor.moveToFirst() ) {
            answer.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
            answer.setWordId(cursor.getInt(cursor.getColumnIndex(COLUMN_WORD_ID)));
            answer.setContent(cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT)));
            answer.setCorrect(cursor.getInt(cursor.getColumnIndex(COLUMN_IS_CORRECT)) == 1);
        }
        return answer;
    }

    public List<Answer> getListAnswerByWord(int idWord) {
        List<Answer> answerList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ANSWER, null, COLUMN_WORD_ID + " = ?",
                                 new String[]{String.valueOf(idWord)}, null, null, null);
        if ( cursor.getCount() > 0 && cursor.moveToFirst() ) {
            do {
                Answer answer = new Answer();
                answer.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                answer.setWordId(cursor.getInt(cursor.getColumnIndex(COLUMN_WORD_ID)));
                answer.setContent(cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT)));
                answer.setCorrect(cursor.getInt(cursor.getColumnIndex(COLUMN_IS_CORRECT)) == 1);
                answerList.add(answer);
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return answerList;
    }

    // Update Answer
    public long updateAnswer(Answer answer) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        if ( answer != null ) {
            cv.put(COLUMN_ID, answer.getId());
            cv.put(COLUMN_WORD_ID, answer.getWordId());
            cv.put(COLUMN_CONTENT, answer.getContent());
            cv.put(COLUMN_IS_CORRECT, answer.getCorrect());
        }
        return db.update(TABLE_WORD, cv, COLUMN_ID + " = ?",
                         new String[]{String.valueOf(answer.getId())});
    }
    //endregion

    //region RESULT
    //Add Result
    public long addResult(Result result) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        if ( result != null ) {
            //cv.put(COLUMN_ID, result.getId());
            cv.put(COLUMN_ID_USER, result.getIdUser());
            cv.put(COLUMN_ID_LESSON, result.getIdLesson());
            cv.put(COLUMN_ID_WORD, result.getIdWord());
            cv.put(COLUMN_ID_ANSWER, result.getIdAnswer());
        }
        return db.insert(TABLE_RESULT, null, cv);
    }

    // Read Result
    public Result getResult(int id) {
        Result result = new Result();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =
                db.query(TABLE_RESULT, null, COLUMN_ID + " = ?", new String[]{String.valueOf(id)},
                         null, null, null);
        if ( cursor != null && cursor.moveToFirst() ) {
            result.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
            result.setIdUser(cursor.getInt(cursor.getColumnIndex(COLUMN_ID_USER)));
            result.setIdLesson(cursor.getInt(cursor.getColumnIndex(COLUMN_ID_LESSON)));
            result.setIdAnswer(cursor.getInt(cursor.getColumnIndex(COLUMN_ID_ANSWER)));
        }
        return result;
    }

    // Update Result
    public long updateResult(Result result) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        if ( result != null ) {
            cv.put(COLUMN_ID, result.getId());
            cv.put(COLUMN_ID_USER, result.getIdUser());
            cv.put(COLUMN_ID_LESSON, result.getIdLesson());
            cv.put(COLUMN_ID_WORD, result.getIdWord());
            cv.put(COLUMN_ID_ANSWER, result.getIdAnswer());
        }
        return db.update(TABLE_RESULT, cv, COLUMN_ID + " = ?",
                         new String[]{String.valueOf(result.getId())});
    }
    //endregion

    // Delete table
    public boolean deleteTable(String tableName, String columnName, String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(tableName, columnName + " = ?", new String[]{value}) != 0;
    }

    public List<Lesson> getListLesson() throws SQLiteException{
        List<Lesson> lessonsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =
                db.query(TABLE_LESSON,null, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Lesson lesson = new Lesson();
                lesson.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                lesson.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
                lessonsList.add(lesson);
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return lessonsList;
    }

    public User getUser() {
        User user = new User();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =
                db.query(TABLE_USER, null, null,null, null, null, null);
        if ( cursor != null && cursor.moveToFirst() ) {
            user.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
            user.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
            user.setEmail(cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL)));
            user.setAvatar(cursor.getString(cursor.getColumnIndex(COLUMN_AVATAR)));
            user.setAdmin(cursor.getInt(cursor.getColumnIndex(COLUMN_ADMIN)) == 1);
            user.setAuthToken(cursor.getString(cursor.getColumnIndex(COLUMN_AUTH_TOKEN)));
            user.setCreatedAt(cursor.getString(cursor.getColumnIndex(COLUMN_CREATED_AT)));
            user.setUpdatedAt(cursor.getString(cursor.getColumnIndex(COLUMN_UPDATED_AT)));
            user.setLearnedWords(cursor.getInt(cursor.getColumnIndex(COLUMN_LEARNED_WORDS)));
        }
        db.close();
        return user;
    }

}
