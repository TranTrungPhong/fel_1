package com.framgia.fel1.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.framgia.fel1.R;
import com.framgia.fel1.model.UserActivity;

import java.util.List;

/**
 * Created by PhongTran on 05/09/2016.
 */
public class UserActionAdapter extends RecyclerView.Adapter<UserActionAdapter.UserActionHolder> {
    private Context mContext;
    private List<UserActivity> mListActivities;

    public UserActionAdapter(Context montext, List<UserActivity> listActivities) {
        this.mContext = montext;
        this.mListActivities = listActivities;
    }

    @Override
    public UserActionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_user_activities, parent,false);
        UserActionHolder actionHolder = new UserActionHolder(view);
        return actionHolder;
    }

    @Override
    public void onBindViewHolder(UserActionHolder holder, int position) {
        holder.mTextViewIdUserAction.setText(mListActivities.get(position).getId()+"");
        holder.mTextViewAction.setText(mListActivities.get(position).getContent().toString());
//        holder.mUserActivity = mListActivities.get(position);
    }

    @Override
    public int getItemCount() {
        return mListActivities == null ? 0 : mListActivities.size();
    }

    public class UserActionHolder extends RecyclerView.ViewHolder{
        private TextView mTextViewIdUserAction;
        private TextView mTextViewAction;
//        private final View mView;
//        private UserActivity mUserActivity;

        public UserActionHolder(View itemView) {
            super(itemView);
//            this.mView = itemView;
            this.mTextViewIdUserAction = (TextView)itemView
                    .findViewById(R.id.text_item_user_activities_id);
            this.mTextViewAction = (TextView)itemView
                    .findViewById(R.id.text_item_user_activities_name);
        }
        @Override
        public String toString() {
            return super.toString();
        }
    }

}
