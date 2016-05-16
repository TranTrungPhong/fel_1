package com.framgia.fel1.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.framgia.fel1.constant.Const;
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
    public static final int DATABASE_VERSION = 3;
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
    public static final String COLUMN_ID_CATEGORY = "id_category";

    public MySqliteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuffer createUserTable =
                new StringBuffer().append("CREATE TABLE ").append(TABLE_USER + " (")
                        .append(COLUMN_ID + " INTEGER PRIMARY KEY, ")
                        .append(COLUMN_NAME).append(" TEXT, ")
                        .append(COLUMN_EMAIL).append(" TEXT, ")
                        .append(COLUMN_AVATAR).append(" TEXT, ")
                        .append(COLUMN_ADMIN).append(" INTEGER, ")
                        .append(COLUMN_AUTH_TOKEN).append(" TEXT, ")
                        .append(COLUMN_CREATED_AT).append(" TEXT, ")
                        .append(COLUMN_UPDATED_AT).append(" TEXT, ")
                        .append(COLUMN_LEARNED_WORDS).append(" INTEGER)");
        StringBuffer createUserActivityTable = new StringBuffer().append("CREATE TABLE ")
                .append(TABLE_USER_ACTIVITY + " (").append(COLUMN_ID + " INTEGER PRIMARY KEY, ")
                .append(COLUMN_ID_USER + " INTEGER, ")
                .append(COLUMN_CONTENT).append(" TEXT, ")
                .append(COLUMN_CREATED_AT).append(" TEXT)");
        StringBuffer createCategoryTable =
                new StringBuffer().append("CREATE TABLE ").append(TABLE_CATEGORY + " (")
                        .append(COLUMN_ID + " INTEGER PRIMARY KEY, ")
                        .append(COLUMN_NAME).append(" TEXT, ")
                        .append(COLUMN_PHOTO).append(" TEXT, ")
                        .append(COLUMN_LEARNED_WORDS).append(" INTEGER)");
        StringBuffer createLessonTable =
                new StringBuffer().append("CREATE TABLE ").append(TABLE_LESSON + " (")
                        .append(COLUMN_ID + " INTEGER PRIMARY KEY, ")
                        .append(COLUMN_ID_CATEGORY + " INTEGER, ")
                        .append(COLUMN_NAME).append(" TEXT)");
        StringBuffer createWordTable =
                new StringBuffer().append("CREATE TABLE ").append(TABLE_WORD + " (")
                        .append(COLUMN_ID + " INTEGER PRIMARY KEY, ")
                        .append(COLUMN_LESSON_ID).append(" INTEGER, ")
                        .append(COLUMN_RESULT_ID).append(" INTEGER, ")
                        .append(COLUMN_CONTENT).append(" TEXT)");
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
    public long addUser(User user) throws SQLiteConstraintException {
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
        long id = db.insertOrThrow(TABLE_USER, null, cv);
        db.close();
        return id;
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
        if ( cursor != null )
            cursor.close();
        db.close();
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
        long numRow = db.update(TABLE_USER, cv, COLUMN_ID + " = ?",
                                new String[]{String.valueOf(user.getId())});
        db.close();
        return numRow;
    }
    //endregion

    //region USERACTIVITY
    // Add UserActivity
    public long addUserActivity(UserActivity userActivity, int idUser)
            throws SQLiteConstraintException {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        if ( userActivity != null ) {
            cv.put(COLUMN_ID, userActivity.getId());
            cv.put(COLUMN_ID_USER, idUser);
            cv.put(COLUMN_CONTENT, userActivity.getContent());
            cv.put(COLUMN_CREATED_AT, userActivity.getCreated_at());
        }
        long id = db.insertOrThrow(TABLE_USER_ACTIVITY, null, cv);
        db.close();
        return id;
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
        if ( cursor != null )
            cursor.close();
        db.close();
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
        long numRow = db.update(TABLE_USER_ACTIVITY, cv, COLUMN_ID + " = ?",
                                new String[]{String.valueOf(userActivity.getId())});
        db.close();
        return numRow;
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
        long id = db.insertOrThrow(TABLE_CATEGORY, null, cv);
        db.close();
        return id;
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
        if ( cursor != null )
            cursor.close();
        db.close();
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
        long numRow = db.update(TABLE_CATEGORY, cv, COLUMN_ID + " = ?",
                                new String[]{String.valueOf(category.getId())});
        db.close();
        return numRow;
    }
    //endregion

    //region Lesson
    //Add Lesson
    public long addLesson(Lesson lesson) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        if ( lesson != null ) {
            cv.put(COLUMN_ID, lesson.getId());
            cv.put(COLUMN_ID_CATEGORY,lesson.getmIdCategory());
            cv.put(COLUMN_NAME, lesson.getName());
        }
        long id = db.insertOrThrow(TABLE_LESSON, null, cv);
        db.close();
        return id;
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
        if ( cursor != null )
            cursor.close();
        db.close();
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
        long numRow = db.update(TABLE_LESSON, cv, COLUMN_ID + " = ?",
                                new String[]{String.valueOf(lesson.getId())});
        db.close();
        return numRow;
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
        long id = db.insertOrThrow(TABLE_WORD, null, cv);
        db.close();
        return id;
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
        if ( cursor != null )
            cursor.close();
        db.close();
        return word;
    }

    public List<Word> getListWord() {
        List<Word> wordList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_WORD, null, null, null, null, null, null);
        if ( cursor.getCount() > 0 && cursor.moveToFirst() ) {
            do {
                Word word = new Word();
                word.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                word.setLessonId(cursor.getInt(cursor.getColumnIndex(COLUMN_LESSON_ID)));
                word.setResultId(cursor.getInt(cursor.getColumnIndex(COLUMN_RESULT_ID)));
                word.setContent(cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT)));
                wordList.add(word);
            }
            while (cursor.moveToNext());
        }
        if ( cursor != null )
            cursor.close();
        db.close();
        return wordList;
    }

    public List<Word> getListWordByLesson(int idLesson) {
        List<Word> wordList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_RESULT, null, COLUMN_ID_LESSON + " = ?",
                                 new String[]{String.valueOf(idLesson)}, null, null, null);
        if ( cursor.getCount() > 0 && cursor.moveToFirst() ) {
            do {
                Word word = getWord(cursor.getInt(cursor.getColumnIndex(COLUMN_ID_WORD)));
                word.setAnswers(getListAnswerByWord(word.getId()));
                wordList.add(word);
            }
            while (cursor.moveToNext());
        }
        if ( cursor != null )
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
        long numRow = db.update(TABLE_WORD, cv, COLUMN_ID + " = ?",
                                new String[]{String.valueOf(word.getId())});
        db.close();
        return numRow;
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
        long id = db.insertOrThrow(TABLE_ANSWER, null, cv);
        db.close();
        return id;
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
        if ( cursor != null )
            cursor.close();
        db.close();
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
        if ( cursor != null )
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
        long numRow = db.update(TABLE_ANSWER, cv, COLUMN_ID + " = ?",
                                new String[]{String.valueOf(answer.getId())});
        db.close();
        return numRow;
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
        long id = db.insertOrThrow(TABLE_RESULT, null, cv);
        db.close();
        return id;
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
        if ( cursor != null )
            cursor.close();
        db.close();
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
        long numRow = db.update(TABLE_RESULT, cv, COLUMN_ID + " = ?",
                                new String[]{String.valueOf(result.getId())});
        db.close();
        return numRow;
    }
    //endregion

    // Delete table
    public boolean deleteTable(String tableName, String columnName, String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        long numRow = db.delete(tableName, columnName + " = ?", new String[]{value});
        db.close();
        return numRow != 0;
    }

    public boolean deleteTable(String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        long numRow = db.delete(tableName, null, null);
        db.close();
        return numRow != 0;
    }

    public int countTable(String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        int count = (int) DatabaseUtils.queryNumEntries(db, tableName);
        db.close();
        return count;
    }

    public List<Lesson> getListLesson() throws SQLiteException {
        List<Lesson> lessonsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_LESSON, null, null, null, null, null, null);
        if ( cursor != null && cursor.moveToFirst() ) {
            while (! cursor.isAfterLast()) {
                Lesson lesson = new Lesson();
                lesson.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                lesson.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
                lessonsList.add(lesson);
                cursor.moveToNext();
            }
        }
        if ( cursor != null )
            cursor.close();
        db.close();
        return lessonsList;
    }

    public List<Lesson> getListLesson(int id) throws SQLiteException {
        List<Lesson> lessonsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =
                db.query(TABLE_LESSON, null, COLUMN_ID + " = ?", new String[]{String.valueOf(id)},
                         null, null, null);
        if ( cursor != null && cursor.moveToFirst() ) {
            while (! cursor.isAfterLast()) {
                Lesson lesson = new Lesson();
                lesson.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                lesson.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
                lessonsList.add(lesson);
                cursor.moveToNext();
            }
        }
        if ( cursor != null )
            cursor.close();
        db.close();
        return lessonsList;
    }
    public List<Lesson> getListLesson(int idLesson, int idCategory) throws SQLiteException {
        List<Lesson> lessonsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =
                db.query(TABLE_LESSON, null, COLUMN_ID + " = ? AND " + COLUMN_ID_CATEGORY + " = ?"
                        , new String[]{String.valueOf(idLesson),String.valueOf(idCategory)},
                        null, null, null);
        if ( cursor != null && cursor.moveToFirst() ) {
            while (! cursor.isAfterLast()) {
                Lesson lesson = new Lesson();
                lesson.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                lesson.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
                lessonsList.add(lesson);
                cursor.moveToNext();
            }
        }
        if ( cursor != null )
            cursor.close();
        db.close();
        return lessonsList;
    }

    public User getUser() {
        User user = new User();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER, null, null, null, null, null, null);
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
        if ( cursor != null )
            cursor.close();
        db.close();
        return user;
    }

    public List<UserActivity> getListUserActivity() {
        List<UserActivity> activityList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =
                db.query(TABLE_USER_ACTIVITY, null, null, null, null, null, Const.ID + " DESC");
        if ( cursor != null && cursor.moveToFirst() ) {
            while (! cursor.isAfterLast()) {
                UserActivity userActivity = new UserActivity();
                userActivity.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                userActivity.setContent(cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT)));
                userActivity.setCreated_at(
                        cursor.getString(cursor.getColumnIndex(COLUMN_CREATED_AT)));
                activityList.add(userActivity);
                cursor.moveToNext();
            }
        }
        if ( cursor != null )
            cursor.close();
        db.close();
        return activityList;
    }

    public List<UserActivity> getListUserActivity(int id) {
        List<UserActivity> activityList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER_ACTIVITY, null, COLUMN_ID_USER + " = ?",
                                 new String[]{String.valueOf(id)}, null, null, Const.ID + " DESC");
        if ( cursor != null && cursor.moveToFirst() ) {
            while (! cursor.isAfterLast()) {
                UserActivity userActivity = new UserActivity();
                userActivity.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                userActivity.setContent(cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT)));
                userActivity.setCreated_at(
                        cursor.getString(cursor.getColumnIndex(COLUMN_CREATED_AT)));
                activityList.add(userActivity);
                cursor.moveToNext();
            }
        }
        if ( cursor != null )
            cursor.close();
        db.close();
        return activityList;
    }

    public List<Result> getListResultByUser(int idUser) {

        List<Result> mListResults = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(true, TABLE_RESULT, new String[]{COLUMN_ID_LESSON},
                                 COLUMN_ID_USER + " = ?", new String[]{String.valueOf(idUser)},
                                 null, null, null, null);
        if ( cursor != null && cursor.moveToFirst() ) {
            while (! cursor.isAfterLast()) {
                Result result = new Result();
                result.setIdLesson(cursor.getInt(cursor.getColumnIndex(COLUMN_ID_LESSON)));
                mListResults.add(result);
                cursor.moveToNext();
            }
        }
        if ( cursor != null )
            cursor.close();
        db.close();
        return mListResults;
    }

    public int getIdAnswerFromResult(int idLesson, int idWord) {
        int idAnswer = - 1;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =
                db.query(true, TABLE_RESULT, new String[]{COLUMN_ID_ANSWER}, COLUMN_ID_LESSON +
                                 "=? AND " + COLUMN_ID_WORD + "=?",
                         new String[]{String.valueOf(idLesson), String.valueOf(idWord)}, null, null,
                         null, null);
        if ( cursor != null && cursor.moveToFirst() ) {
            idAnswer = cursor.getInt(cursor.getColumnIndex(COLUMN_ID_ANSWER));
            cursor.moveToNext();
        }
        if ( cursor != null )
            cursor.close();
        cursor.close();
        db.close();
        return idAnswer;
    }

}
