package com.framgia.fel1.model;

/**
 * Created by PhongTran on 05/21/2016.
 */
public class AnswerTag {
	private int mIdWord;
	private int mRadioTag;

	public AnswerTag( int idWord, int radioTag ) {
		mIdWord = idWord;
		mRadioTag = radioTag;
	}

	public AnswerTag() {
	}

	public int getIdWord() {
		return mIdWord;
	}

	public void setIdWord( int idWord ) {
		mIdWord = idWord;
	}

	public int getRadioTag() {
		return mRadioTag;
	}

	public void setRadioTag( int radioTag ) {
		mRadioTag = radioTag;
	}
}
