package com.framgia.fel1.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.framgia.fel1.R;
import com.framgia.fel1.model.Category;

import java.util.List;

/**
 * Created by PhongTran on 05/02/2016.
 */
public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.CategoryViewHolder> {
    private List<Category> mListCategory;
    private Context mContext;
    private OnListCategoryClickItem mCategoryClickItem;

    public HomeAdapter(Context context, List<Category> categoryList) {
        this.mContext = context;
        this.mListCategory = categoryList;
        if (context instanceof OnListCategoryClickItem) {
            mCategoryClickItem = (OnListCategoryClickItem) context;
        } else {
            throw new RuntimeException(
                    context.toString() + "must implement OnListCategoryClickItem"
            );
        }
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_category, parent,false);
        CategoryViewHolder categoryViewHolder = new CategoryViewHolder(view);
        return categoryViewHolder;
    }

    @Override
    public void onBindViewHolder(final CategoryViewHolder category, final int position) {
        category.mTextCategoryId.setText(mListCategory.get(position).getId() + "");
        category.mTextCategoryName.setText(mListCategory.get(position).getName() + "");
        category.mCategory = mListCategory.get(position);
        category.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCategoryClickItem != null) {
                    mCategoryClickItem.onListCategoryClickItem(position, category.mCategory);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mListCategory == null ? 0 : mListCategory.size();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        private TextView mTextCategoryName;
        private TextView mTextCategoryId;
        private final View mView;
        private Category mCategory;
        public CategoryViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            this.mTextCategoryId = (TextView) itemView.findViewById(R.id.text_item_category_id);
            this.mTextCategoryName = (TextView) itemView.findViewById(R.id.text_item_category_name);
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }

    public interface OnListCategoryClickItem {
        public void onListCategoryClickItem(int position, Category category);
    }

}
