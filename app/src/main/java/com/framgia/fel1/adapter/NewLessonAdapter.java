package com.framgia.fel1.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;
import com.framgia.fel1.R;
import com.framgia.fel1.activity.NewLessonActivity;
import com.framgia.fel1.constant.Const;
import com.framgia.fel1.model.Word;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by PhongTran on 05/02/2016.
 */
public class NewLessonAdapter extends RecyclerView.Adapter<NewLessonAdapter.NewLessonViewHolder>
        implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    private Context mContext;
    private List<Word> mWordList = new ArrayList<>();
    private OnListWordsClickItem mOnListWordsClickItem;

    public NewLessonAdapter(Context context, List<Word> wordList) {
        this.mContext = context;
        this.mWordList = wordList;
        if (context instanceof OnListWordsClickItem) {
            mOnListWordsClickItem = (OnListWordsClickItem) context;
        } else {
            throw new RuntimeException(
                    context.toString() + context.getString(R.string.onListWordsClickItem)
            );
        }
    }

    @Override
    public NewLessonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_new_lesson, parent, false);
        NewLessonViewHolder lessonViewHolder = new NewLessonViewHolder(view);
        return lessonViewHolder;
    }

    @Override
    public void onBindViewHolder(final NewLessonViewHolder holder, final int position) {
        holder.mWord = mWordList.get(position);
        holder.mTextViewNameWord.setText("#" + (mWordList.get(position).getId() + 1));
        holder.mTextViewContentWord.setText(mWordList.get(position).getContent().toString());
        holder.mTextViewContentWord.setOnClickListener(this);
        holder.mTextViewContentWord.setTag(holder.mWord);
        holder.mRadioButtonA.setText(
                mWordList.get(position)
                        .getAnswers()
                        .get(Const.POSITION_ANSWER_1)
                        .getContent()
                        .toString());
        holder.mRadioButtonB.setText(
                mWordList.get(position)
                        .getAnswers()
                        .get(Const.POSITION_ANSWER_2)
                        .getContent()
                        .toString());
        holder.mRadioButtonC.setText(
                mWordList.get(position)
                        .getAnswers()
                        .get(Const.POSITION_ANSWER_3)
                        .getContent()
                        .toString());
        holder.mRadioButtonD.setText(
                mWordList.get(position)
                        .getAnswers()
                        .get(Const.POSITION_ANSWER_4)
                        .getContent()
                        .toString());
        holder.mRadioButtonA.setOnCheckedChangeListener(this);
        holder.mRadioButtonA.setTag(holder.mWord);
        holder.mRadioButtonB.setOnCheckedChangeListener(this);
        holder.mRadioButtonB.setTag(holder.mWord);
        holder.mRadioButtonC.setOnCheckedChangeListener(this);
        holder.mRadioButtonC.setTag(holder.mWord);
        holder.mRadioButtonD.setOnCheckedChangeListener(this);
        holder.mRadioButtonD.setTag(holder.mWord);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        Word word = (Word) buttonView.getTag();
        switch (buttonView.getId()) {
            case R.id.radiobutton_a:
                if (isChecked) {
                    if (word.getAnswers().get(Const.POSITION_ANSWER_1).getCorrect()) {
                        word.setResultId(word.getAnswers().get(Const.POSITION_ANSWER_1).getId());
                    }
                }
                break;
            case R.id.radiobutton_b:
                if (isChecked) {
                    if (word.getAnswers().get(Const.POSITION_ANSWER_2).getCorrect()) {
                        word.setResultId(word.getAnswers().get(Const.POSITION_ANSWER_2).getId());
                    }
                }
                break;
            case R.id.radiobutton_c:
                if (isChecked) {
                    if (word.getAnswers().get(Const.POSITION_ANSWER_3).getCorrect()) {
                        word.setResultId(word.getAnswers().get(Const.POSITION_ANSWER_3).getId());
                    }
                }
                break;
            case R.id.radiobutton_d:
                if (isChecked) {
                    if (word.getAnswers().get(Const.POSITION_ANSWER_4).getCorrect()) {
                        word.setResultId(word.getAnswers().get(Const.POSITION_ANSWER_4).getId());
                    }
                }
                break;
            default:
                break;
        }

    }

    @Override
    public void onClick(View v) {
        Word word = (Word) v.getTag();
        switch (v.getId()) {
            case R.id.text_content_word_new:
                NewLessonActivity.mReadWord = word.getContent().toString();
                ((NewLessonActivity) mContext).onClick(v);
                break;
        }
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

        public NewLessonViewHolder(View itemView) {
            super(itemView);
            this.mView = itemView;
            this.mTextViewNameWord = (TextView) itemView.findViewById(R.id.text_stt_new_word);
            this.mTextViewContentWord = (TextView) itemView.findViewById(R.id.text_content_word_new);
            this.mRadioButtonA = (RadioButton) itemView.findViewById(R.id.radiobutton_a);
            this.mRadioButtonB = (RadioButton) itemView.findViewById(R.id.radiobutton_b);
            this.mRadioButtonC = (RadioButton) itemView.findViewById(R.id.radiobutton_c);
            this.mRadioButtonD = (RadioButton) itemView.findViewById(R.id.radiobutton_d);
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }

    public interface OnListWordsClickItem {
        void onListWordsClickItem(int position, Word word);
    }
}
