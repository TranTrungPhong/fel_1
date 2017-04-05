package com.framgia.fel1.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.framgia.fel1.R;
import com.framgia.fel1.model.Lesson;

import java.util.List;

/**
 * Created by PhongTran on 05/02/2016.
 */
public class LessonLearnedAdapter
        extends RecyclerView.Adapter<LessonLearnedAdapter.LessonLearnedViewHolder> {
    private Context mContext;
    private List<Lesson> mLessonLearnedList;
    private OnClickItemLessonLearned mClickItemLessonLearned;

    public LessonLearnedAdapter(Context context, List<Lesson> mLessonLearnedList) {
        this.mContext = context;
        this.mLessonLearnedList = mLessonLearnedList;
        if (context instanceof OnClickItemLessonLearned) {
            mClickItemLessonLearned = (OnClickItemLessonLearned) context;
        } else {
            throw new RuntimeException(
                    context.toString() + context.getString(R.string.implement_listener)
            );
        }
    }

    @Override
    public LessonLearnedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(mContext).inflate(R.layout.item_lesson_learned, parent, false);
        LessonLearnedViewHolder categoryViewHolder = new LessonLearnedViewHolder(view);
        return categoryViewHolder;
    }

    @Override
    public void onBindViewHolder(final LessonLearnedViewHolder holder, final int position) {
        holder.mTextViewNameLessonLearned
                .setText(mLessonLearnedList.get(position).getName().toString());
        holder.mLesson = mLessonLearnedList.get(position);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickItemLessonLearned != null) {
                    mClickItemLessonLearned.onClickItemLessonLearned(position, holder.mLesson);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mLessonLearnedList == null ? 0 : mLessonLearnedList.size();
    }

    public class LessonLearnedViewHolder extends RecyclerView.ViewHolder {
        private TextView mTextViewNameLessonLearned;
        private final View mView;
        private Lesson mLesson;

        public LessonLearnedViewHolder(View itemView) {
            super(itemView);
            this.mView = itemView;
            this.mTextViewNameLessonLearned =
                    (TextView) itemView.findViewById(R.id.text_name_lesson_learned);
        }
    }

    public interface OnClickItemLessonLearned {
        void onClickItemLessonLearned(int position, Lesson lesson);
    }
}
