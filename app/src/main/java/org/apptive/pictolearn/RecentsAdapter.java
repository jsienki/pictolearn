package org.apptive.pictolearn;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;


public class RecentsAdapter extends RecyclerView.Adapter<RecentsAdapter.RecentsViewHolder> {
    // Refer to the ExampleAdapter and DataSetAdapter class for description

    private static ArrayList<RecentsItem> mRecentsList;
    private OnClickListener mListener;

    RecentsAdapter(ArrayList<RecentsItem> recentsList) {

        mRecentsList = recentsList;
    }

    void setOnItemClickListener(RecentsAdapter.OnClickListener listener) {
        mListener = listener;
    }

    @Override
    public RecentsAdapter.RecentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recents_item, parent, false);
        RecentsAdapter.RecentsViewHolder evh = new RecentsAdapter.RecentsViewHolder(v, mListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(RecentsAdapter.RecentsViewHolder holder, int position) {
        RecentsItem currentItem = mRecentsList.get(position);

        holder.mTextView1.setText(currentItem.getText1());

        holder.mTextViewDate.setText(currentItem.getDate());

        String scoreText = currentItem.getLastScoreP() + "%" + "/" + currentItem.getLastScoreA() + "%";
        holder.mTextViewScore.setText(scoreText);
    }

    @Override
    public int getItemCount() {

        return mRecentsList.size();
    }

    public interface OnClickListener {
        void OnOpenClick(int position);
    }

    static class RecentsViewHolder extends RecyclerView.ViewHolder {

        TextView mTextView1;
        TextView mTextViewDate;
        TextView mTextViewScore;
        Button mOpenButton;


        RecentsViewHolder(View itemView, final OnClickListener listener) {
            super(itemView);

            mTextView1 = itemView.findViewById(R.id.textView);
            mTextViewDate = itemView.findViewById(R.id.textView_date);
            mTextViewScore = itemView.findViewById(R.id.textView_score);
            mOpenButton = itemView.findViewById(R.id.openButton);

            mOpenButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();

                        if (position != RecyclerView.NO_POSITION) {
                            listener.OnOpenClick(position);

                        }

                    }

                }
            });


        }


    }
}
