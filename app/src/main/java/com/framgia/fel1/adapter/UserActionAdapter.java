package com.framgia.fel1.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.framgia.fel1.R;
import com.framgia.fel1.model.UserActivity;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

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
        View view =
                LayoutInflater.from(mContext).inflate(R.layout.item_user_activities, parent, false);
        UserActionHolder actionHolder = new UserActionHolder(view);
        return actionHolder;
    }

    @Override
    public void onBindViewHolder(UserActionHolder holder, int position) {
        holder.binding(mListActivities.get(position));
    }

    @Override
    public int getItemCount() {
        return mListActivities == null ? 0 : mListActivities.size();
    }

    public class UserActionHolder extends RecyclerView.ViewHolder {
        private TextView mTextViewIdUserAction;
        private TextView mTextViewAction;

        public UserActionHolder(View itemView) {
            super(itemView);
            this.mTextViewIdUserAction =
                    (TextView) itemView.findViewById(R.id.text_item_user_activities_id);
            this.mTextViewAction =
                    (TextView) itemView.findViewById(R.id.text_item_user_activities_name);
        }

        public void binding(UserActivity userActivity) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm");
            try {
                Date convertedDate = sdf.parse(userActivity.getCreated_at());
                mTextViewIdUserAction.setText(dateFormat.format(convertedDate));
            } catch (ParseException e) {
                mTextViewIdUserAction.setText(userActivity.getCreated_at());
                e.printStackTrace();
            }

            mTextViewAction.setText(userActivity.getContent());
        }
    }
}
