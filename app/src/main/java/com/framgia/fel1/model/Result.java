package com.framgia.fel1.model;

/**
 * Created by vuduychuong1994 on 5/3/16.
 */
public class Result {
    private int mId;
    private int mIdUser;
    private int mIdLesson;
    private int mIdWord;
    private int mIdAnswer;

    public Result() {
    }

    public Result(int id, int idUser, int idLesson, int idWord, int idAnswer) {
        mId = id;
        mIdUser = idUser;
        mIdLesson = idLesson;
        mIdWord = idWord;
        mIdAnswer = idAnswer;
    }

    public Result(int idUser, int idLesson, int idWord, int idAnswer) {
        mIdUser = idUser;
        mIdLesson = idLesson;
        mIdWord = idWord;
        mIdAnswer = idAnswer;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public int getIdUser() {
        return mIdUser;
    }

    public void setIdUser(int idUser) {
        mIdUser = idUser;
    }

    public int getIdLesson() {
        return mIdLesson;
    }

    public void setIdLesson(int idLesson) {
        mIdLesson = idLesson;
    }

    public int getIdWord() {
        return mIdWord;
    }

    public void setIdWord(int idWord) {
        mIdWord = idWord;
    }

    public int getIdAnswer() {
        return mIdAnswer;
    }

    public void setIdAnswer(int idAnswer) {
        mIdAnswer = idAnswer;
    }
}
