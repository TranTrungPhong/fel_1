package com.framgia.fel1.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.framgia.fel1.R;
import com.framgia.fel1.model.Category;

import java.util.List;

/**
 * Created by PhongTran on 04/15/2016.
 */
public class CategoryAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<Category> mListCategory;

    public CategoryAdapter(Context mContext, List<Category> listCategory) {
        this.mContext = mContext;
        this.mListCategory = listCategory;
    }

    @Override
    public int getCount() {
        return mListCategory.size();
    }

    @Override
    public Category getItem(int position) {
        return mListCategory.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHodel viewHodel;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_category, parent, false);
            viewHodel = new ViewHodel();
            viewHodel.mTextNameCategory = (TextView) convertView.findViewById(R.id.text_item_category_name);
            viewHodel.mTextCategoryId = (TextView) convertView.findViewById(R.id.text_item_category_id);
            convertView.setTag(convertView);
        } else {
            viewHodel = (ViewHodel) convertView.getTag();
        }
        viewHodel.mTextNameCategory.setText(mListCategory.get(position).getName());
        viewHodel.mTextCategoryId.setText(mListCategory.get(position).getId());
        return convertView;
    }

    private class ViewHodel {
        private TextView mTextNameCategory;
        private TextView mTextCategoryId;
    }
}
