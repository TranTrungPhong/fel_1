package com.framgia.fel1.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.framgia.fel1.R;
import com.framgia.fel1.activity.NewLessonActivity;
import com.framgia.fel1.constant.Const;
import com.framgia.fel1.model.AnswerTag;
import com.framgia.fel1.model.Word;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by PhongTran on 05/02/2016.
 */
public class NewLessonAdapter extends RecyclerView.Adapter< NewLessonAdapter.NewLessonViewHolder >
		implements  View.OnClickListener {

	private Context mContext;
	private List< Word > mWordList = new ArrayList<>();
	private ArrayList< Word > mWordListAnswer = new ArrayList<>();
	private OnListWordsClickItem mOnListWordsClickItem;
	private List< AnswerTag > mAnswerTagList = new ArrayList<>();
	private List<Integer> mStateList;
	private SharedPreferences mPreferences;
	private String TAG = "NewLessonAdapter";

	public NewLessonAdapter( Context context, ArrayList< Word > wordList ) {
		this.mContext = context;
		this.mWordList = wordList;
		Integer[] mIntegers = new Integer[wordList.size()];
		Arrays.fill(mIntegers, 0);
		mStateList = Arrays.asList(mIntegers);
		this.mWordListAnswer = wordList;
		if (context instanceof OnListWordsClickItem) {
			mOnListWordsClickItem = ( OnListWordsClickItem ) context;
		} else {
			throw new RuntimeException(context.toString() + context.getString(R.string.onListWordsClickItem));
		}
	}

	@Override
	public NewLessonViewHolder onCreateViewHolder( ViewGroup parent, int viewType ) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_new_lesson, parent, false);
		NewLessonViewHolder lessonViewHolder = new NewLessonViewHolder(view);
		return lessonViewHolder;
	}

	@Override
	public void onBindViewHolder( final NewLessonViewHolder holder, final int position ) {
		holder.mWord = mWordListAnswer.get(position);
		holder.mTextViewNameWord.setText("#" + ( mWordListAnswer.get(position).getId() + 1 ));
		holder.mTextViewContentWord.setText(mWordListAnswer.get(position).getContent().toString());
		holder.mTextViewContentWord.setOnClickListener(this);
		holder.mTextViewContentWord.setTag(holder.mWord);
		holder.mRadioButtonA.setText(mWordListAnswer.get(position).getAnswers().get(Const.POSITION_ANSWER_1).getContent().toString());
		holder.mRadioButtonB.setText(mWordListAnswer.get(position).getAnswers().get(Const.POSITION_ANSWER_2).getContent().toString());
		holder.mRadioButtonC.setText(mWordListAnswer.get(position).getAnswers().get(Const.POSITION_ANSWER_3).getContent().toString());
		holder.mRadioButtonD.setText(mWordListAnswer.get(position).getAnswers().get(Const.POSITION_ANSWER_4).getContent().toString());

		mPreferences = mContext.getSharedPreferences(Const.MY_PREFERENCE, Context.MODE_PRIVATE);
		int tag = mPreferences.getInt(Const.ANSWER_TAG, -1);
		Log.d(TAG, "onBindViewHolder: tag = " + tag);
		if (tag == 1) {
			RadioGroup radioGroup = ( RadioGroup ) holder.mView.findViewById(R.id.radiogroup);
			radioGroup.clearCheck();
			SharedPreferences.Editor editor = mPreferences.edit();
			editor.putInt(Const.ANSWER_TAG, 2);
			editor.apply();
		}
		holder.mRadioButtonA.setChecked(mStateList.get(position) == 1);
		holder.mRadioButtonB.setChecked(mStateList.get(position) == 2);
		holder.mRadioButtonC.setChecked(mStateList.get(position) == 3);
		holder.mRadioButtonD.setChecked(mStateList.get(position) == 4);
		holder.mRadioButtonA.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged( CompoundButton buttonView, boolean isChecked ) {
				if(isChecked){
					holder.mWord.setResultId(holder.mWord.getAnswers().get(Const.POSITION_ANSWER_1).getId());
					mStateList.set(position, 1);
//					buttonView.setChecked(true);
				}
			}
		});
		holder.mRadioButtonB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged( CompoundButton buttonView, boolean isChecked ) {
				if(isChecked){
					holder.mWord.setResultId(holder.mWord.getAnswers().get(Const
							.POSITION_ANSWER_2).getId());
//					buttonView.setChecked(true);
					mStateList.set(position, 2);
				}
			}
		});
		holder.mRadioButtonC.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged( CompoundButton buttonView, boolean isChecked ) {
				if(isChecked){
					holder.mWord.setResultId(holder.mWord.getAnswers().get(Const
							.POSITION_ANSWER_3).getId());
					mStateList.set(position, 3);
//					buttonView.setChecked(true);
				}
			}
		});
		holder.mRadioButtonD.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged( CompoundButton buttonView, boolean isChecked ) {
				if(isChecked){
					holder.mWord.setResultId(holder.mWord.getAnswers().get(Const
							.POSITION_ANSWER_4).getId());
					mStateList.set(position, 4);
//					buttonView.setChecked(true);
				}
			}
		});
		holder.mView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick( View v ) {
				if (mOnListWordsClickItem != null) {
					mOnListWordsClickItem.onListWordsClickItem(position, holder.mWord);
				}
			}
		});
	}

	@Override
	public int getItemCount() {
		return mWordList == null ? 0 : mWordList.size();
	}

	@Override
	public void onClick( View v ) {
		Word word = ( Word ) v.getTag();
		switch ( v.getId() ) {
			case R.id.text_content_word_new:
				NewLessonActivity.mReadWord = word.getContent().toString();
				( ( NewLessonActivity ) mContext ).onClick(v);
				break;
		}
	}

	public ArrayList< Word > getListWordAnswer() {
		return mWordListAnswer;
	}

	public void clearCheck() {
		Integer[] mIntegers = new Integer[mWordList.size()];
		Arrays.fill(mIntegers, 0);
		mStateList = Arrays.asList(mIntegers);
	}

	public class NewLessonViewHolder extends RecyclerView.ViewHolder {

		private TextView mTextViewNameWord;
		private TextView mTextViewContentWord;
		private RadioButton mRadioButtonA;
		private RadioButton mRadioButtonB;
		private RadioButton mRadioButtonC;
		private RadioButton mRadioButtonD;
		private View mView;
		private Word mWord;

		public NewLessonViewHolder( View itemView ) {
			super(itemView);
			this.mView = itemView;
			this.mTextViewNameWord = ( TextView ) itemView.findViewById(R.id.text_stt_new_word);
			this.mTextViewContentWord = ( TextView ) itemView.findViewById(R.id.text_content_word_new);
			this.mRadioButtonA = ( RadioButton ) itemView.findViewById(R.id.radiobutton_a);
			this.mRadioButtonB = ( RadioButton ) itemView.findViewById(R.id.radiobutton_b);
			this.mRadioButtonC = ( RadioButton ) itemView.findViewById(R.id.radiobutton_c);
			this.mRadioButtonD = ( RadioButton ) itemView.findViewById(R.id.radiobutton_d);
		}

		@Override
		public String toString() {
			return super.toString();
		}

	}

	public interface OnListWordsClickItem {

		void onListWordsClickItem( int position, Word word );
	}
}
