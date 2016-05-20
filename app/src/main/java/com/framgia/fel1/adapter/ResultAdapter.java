package com.framgia.fel1.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.framgia.fel1.R;
import com.framgia.fel1.data.MySqliteHelper;
import com.framgia.fel1.model.Answer;
import com.framgia.fel1.model.ItemList2;
import com.framgia.fel1.model.Lesson;
import com.framgia.fel1.model.Word;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link ItemList2} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}. for your data type.
 */
public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ViewHolder> {
    private Context mContext;
    private List<Word> mValues;
    private OnListFragmentInteractionListener mListener;
    private Lesson mLesson;
    private MySqliteHelper mSqliteHelper;
    private int mCountCorrectAnswer = 0;

    public ResultAdapter(Context context, Lesson lesson, List<Word> items) {
        mContext = context;
        mLesson = lesson;
        mValues = items;
        mSqliteHelper = new MySqliteHelper(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(
                    context.toString() + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public int getItemViewType(int position) {
        for (Answer answer : mValues.get(position).getAnswers()) {

            if (answer.getCorrect() && mSqliteHelper.getIdAnswerFromResult(mLesson.getId(),
                    mValues.get(
                            position).getId()) ==
                    answer.getId())
                return 1; // correct answer
        }
        return 0; // incorrect answer
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_result, parent,
                false);
        switch (viewType) {
            case 0: // incorrect
                view.setBackgroundResource(R.color.bg_wrong_answer);
                break;
            case 1: // correct
                view.setBackgroundResource(R.color.bg_right_answer);
                break;
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        holder.mTextQuestion.setText(mValues.get(position).getContent());
        RadioButton[] arrRadioButton =
                new RadioButton[]{holder.mRadioButton1, holder.mRadioButton2, holder.mRadioButton3,
                        holder.mRadioButton4};
        int i = 0;
        for (RadioButton radioButton : arrRadioButton) {
            if (mValues.get(position).getAnswers().size() > i) {
                Answer answer = mValues.get(position).getAnswers().get(i);
                radioButton.setText(answer.getContent());
                radioButton.setChecked(mSqliteHelper
                        .getIdAnswerFromResult(mLesson.getId(),
                                mValues.get(position).getId())
                        == answer.getId());
            }
            i++;
        }
        holder.mImageSpeak.setImageDrawable(
                new IconicsDrawable(mContext).icon(FontAwesome.Icon.faw_volume_up).colorRes(
                        R.color.colorAccent).sizeRes(R.dimen.icon_size));
        holder.mImageSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onClickSpeakListener(position, holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mValues != null) {
            return mValues.size();
        } else {
            return 0;
        }
    }

    public String getCountCorrect() {
        for (int i = 0; i < getItemCount(); i++) {
            for (Answer answer : mValues.get(i).getAnswers()) {

                if (answer.getCorrect() &&
                        mSqliteHelper.getIdAnswerFromResult(mLesson.getId(),
                                mValues.get(i).getId()) == answer.getId())
                    mCountCorrectAnswer++;
            }
        }
        return " " + mCountCorrectAnswer + "/" + getItemCount() + " ";
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTextQuestion;
        public final TextView mDetailView;
        public final ImageView mImageSpeak;
        public final RadioGroup mRadioGroup;
        public final RadioButton mRadioButton1;
        public final RadioButton mRadioButton2;
        public final RadioButton mRadioButton3;
        public final RadioButton mRadioButton4;
        public Word mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTextQuestion = (TextView) view.findViewById(R.id.text_question);
            mDetailView = (TextView) view.findViewById(R.id.text_detail);
            mImageSpeak = (ImageView) view.findViewById(R.id.image_speak);
            mRadioGroup = (RadioGroup) view.findViewById(R.id.radio_group);
            mRadioButton1 = (RadioButton) view.findViewById(R.id.radio_answer_1);
            mRadioButton2 = (RadioButton) view.findViewById(R.id.radio_answer_2);
            mRadioButton3 = (RadioButton) view.findViewById(R.id.radio_answer_3);
            mRadioButton4 = (RadioButton) view.findViewById(R.id.radio_answer_4);
        }
    }

    public interface OnListFragmentInteractionListener {
        void onClickSpeakListener(int position, Word word);
    }
}
