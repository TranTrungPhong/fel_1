package com.framgia.fel1.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.framgia.fel1.R;
import com.framgia.fel1.constant.Const;
import com.framgia.fel1.data.MySqliteHelper;
import com.framgia.fel1.model.ItemList2;
import com.framgia.fel1.model.Word;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link ItemList2} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}. for your data type.
 */
public class MyItemRecyclerViewAdapter
        extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> implements Filterable {
    private Context mContext;
    private List<ItemList2> mValues;
    private List<ItemList2> mListFiltered = new ArrayList<>();
    private OnListFragmentInteractionListener mListener;
    private ItemFilter mFilter = new ItemFilter();
    private MySqliteHelper mSqliteHelper;
    private String mFilterString = Const.ALL_WORD;

    public MyItemRecyclerViewAdapter(Context context, List<ItemList2> items) {
        mContext = context;
        mValues = items;
        mListFiltered = items;
        mSqliteHelper = new MySqliteHelper(context);
        if ( context instanceof OnListFragmentInteractionListener ) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(
                    context.toString() + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public int getItemViewType(int position) {
        int type = position % 2;
        ItemList2 item = mListFiltered.get(position);
        MySqliteHelper sqliteHelper = new MySqliteHelper(mContext);
        Word word = sqliteHelper.getWord(Integer.parseInt(item.getId()));
        if ( word.getId() == Integer.parseInt(item.getId()) && word.getResultId() != 0 &&
                mFilterString.equals(Const.ALL_WORD) ) {
            type += 2;
        }
        return type;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case 0:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_word, parent,
                                                                        false);
//                ((CardView) view.findViewById(R.id.card_view)).setCardBackgroundColor(
//                        mContext.getResources().getColor(R.color.gray));
                break;
            case 1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_word, parent,
                                                                        false);
                break;
            case 2:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_word_learned,
                                                                        parent, false);
//                ((CardView) view.findViewById(R.id.card_view)).setCardBackgroundColor(
//                        mContext.getResources().getColor(R.color.gray));
                break;
            case 3:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_word_learned,
                                                                        parent, false);
                break;
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_word, parent,
                                                                        false);
                break;
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        //        if( holder.getItemViewType() == 0 || holder.getItemViewType() == 2)
        //            holder.mCardView.setCardBackgroundColor(mContext.);
        holder.mItem = mListFiltered.get(position);
        holder.mContentView.setText(mListFiltered.get(position).getContent());
        holder.mDetailView.setText(mListFiltered.get(position).getDetail());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( null != mListener ) {
                    mListener.onListFragmentInteraction(position, holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if ( mListFiltered != null ) { return mListFiltered.size(); } else { return 0; }
    }

    public List<ItemList2> getListFiltered() {
        return mListFiltered;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContentView;
        public final TextView mDetailView;
        public final CardView mCardView;
        public ItemList2 mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mCardView = (CardView) view.findViewById(R.id.card_view);
            mContentView = (TextView) view.findViewById(R.id.text_content);
            mDetailView = (TextView) view.findViewById(R.id.text_detail);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence state) {
            //            String query = state.toString().toLowerCase();
            mFilterString = state.toString();
            FilterResults results = new FilterResults();
            final List<ItemList2> list = mValues;
            final List<ItemList2> resultList = new ArrayList<>(list.size());

            for (ItemList2 item : list) {
                Word word = mSqliteHelper.getWord(Integer.parseInt(item.getId()));
                switch (state.toString()) {
                    case Const.ALL_WORD:
                        resultList.add(item);
                        break;
                    case Const.LEARNED:
                        if ( word.getId() == Integer.parseInt(item.getId()) &&
                                word.getResultId() != 0 ) {
                            resultList.add(item);
                        }
                        break;
                    case Const.NO_LEARN:
                        if ( word.getResultId() == 0 ) {
                            resultList.add(item);
                        }
                        break;
                    default:
                        resultList.add(item);
                        break;
                }
            }

            results.values = resultList;
            results.count = resultList.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mListFiltered = (List<ItemList2>) results.values;
            notifyDataSetChanged();
        }
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(int position, ItemList2 item);
    }
}
