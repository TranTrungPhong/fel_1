package com.framgia.fel1.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.framgia.fel1.R;
import com.framgia.fel1.activity.LessonActivity;
import com.framgia.fel1.constant.Const;
import com.framgia.fel1.model.Word;
import java.util.ArrayList;

/**
 * Created by PhongTran on 05/02/2016.
 */
public class NewLessonAdapter extends RecyclerView.Adapter<NewLessonAdapter.NewLessonViewHolder> {

    private Context mContext;
    private ArrayList<Word> mWordList;
    private SharedPreferences mPreferences;
    private String TAG = "NewLessonAdapter";

    public NewLessonAdapter(Context context, ArrayList<Word> wordList) {
        this.mContext = context;
        this.mWordList = wordList;
    }

    @Override
    public NewLessonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                                  .inflate(R.layout.item_new_lesson, parent, false);
        return new NewLessonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final NewLessonViewHolder holder, final int position) {
        holder.binding(mWordList.get(position));
    }

    @Override
    public int getItemCount() {
        return mWordList == null ? 0 : mWordList.size();
    }

    public class NewLessonViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private TextView mTextViewNameWord;
        private TextView mTextViewContentWord;
        private RadioGroup mRadioGroup;
        private RadioButton mRadioButtonA;
        private RadioButton mRadioButtonB;
        private RadioButton mRadioButtonC;
        private RadioButton mRadioButtonD;
        private View mView;
        private Word mWord;

        public NewLessonViewHolder(View itemView) {
            super(itemView);
            this.mView = itemView;
            this.mTextViewNameWord = (TextView) itemView.findViewById(R.id.text_stt_new_word);
            this.mTextViewContentWord =
                    (TextView) itemView.findViewById(R.id.text_content_word_new);
            this.mRadioGroup = (RadioGroup) itemView.findViewById(R.id.radiogroup);
            this.mRadioButtonA = (RadioButton) itemView.findViewById(R.id.radiobutton_a);
            this.mRadioButtonB = (RadioButton) itemView.findViewById(R.id.radiobutton_b);
            this.mRadioButtonC = (RadioButton) itemView.findViewById(R.id.radiobutton_c);
            this.mRadioButtonD = (RadioButton) itemView.findViewById(R.id.radiobutton_d);
        }

        public void binding(Word word) {
            mWord = word;
            mTextViewNameWord.setText("#" + (word.getId() + 1));
            mTextViewContentWord.setText(word.getContent());
            mTextViewContentWord.setOnClickListener(this);
            mTextViewContentWord.setTag(word);
            mRadioButtonA.setText(word.getAnswers().get(Const.POSITION_ANSWER_1).getContent());
            mRadioButtonB.setText(word.getAnswers().get(Const.POSITION_ANSWER_2).getContent());
            mRadioButtonC.setText(word.getAnswers().get(Const.POSITION_ANSWER_3).getContent());
            mRadioButtonD.setText(word.getAnswers().get(Const.POSITION_ANSWER_4).getContent());

            mRadioGroup.clearCheck();
            mRadioButtonA.setChecked(
                    word.getResultId() == word.getAnswers().get(Const.POSITION_ANSWER_1).getId());
            mRadioButtonB.setChecked(
                    word.getResultId() == word.getAnswers().get(Const.POSITION_ANSWER_2).getId());
            mRadioButtonC.setChecked(
                    word.getResultId() == word.getAnswers().get(Const.POSITION_ANSWER_3).getId());
            mRadioButtonD.setChecked(
                    word.getResultId() == word.getAnswers().get(Const.POSITION_ANSWER_4).getId());
            mRadioButtonA.setOnClickListener(this);
            mRadioButtonB.setOnClickListener(this);
            mRadioButtonC.setOnClickListener(this);
            mRadioButtonD.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.text_content_word_new:
                    LessonActivity.mReadWord = mWord.getContent().toString();
                    ((LessonActivity) mContext).onClick(view);
                    break;
                case R.id.radiobutton_a:
                    mWord.setResultId(mWord.getAnswers().get(Const.POSITION_ANSWER_1).getId());
                    break;
                case R.id.radiobutton_b:
                    mWord.setResultId(mWord.getAnswers().get(Const.POSITION_ANSWER_2).getId());
                    break;
                case R.id.radiobutton_c:
                    mWord.setResultId(mWord.getAnswers().get(Const.POSITION_ANSWER_3).getId());
                    break;
                case R.id.radiobutton_d:
                    mWord.setResultId(mWord.getAnswers().get(Const.POSITION_ANSWER_4).getId());
                    break;
            }
        }
    }
}
