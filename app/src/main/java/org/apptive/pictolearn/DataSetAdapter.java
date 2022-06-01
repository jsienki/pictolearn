package org.apptive.pictolearn;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class DataSetAdapter extends RecyclerView.Adapter<DataSetAdapter.DataSetViewHolder> {
    // This is a RecyclerView adapter which handles the behaviour of the DataSetRecycler.
    // My RecyclerView Adapters are built following this video series:
    // https://www.youtube.com/watch?v=Nw9JF55LDzE&list=PLrnPJCHvNZuBtTYUuc5Pyo4V7xZ2HNtf4

    private static ArrayList<DataItem> mDataList;
    private static Context mContext;

    static class DataSetViewHolder extends RecyclerView.ViewHolder {

        ImageView mImageView;
        TextView mTextView1;
        Button mOpenButton;


        DataSetViewHolder(View itemView) {
            // This constructor allows us to define the behaviour of the separate Views inside the
            // RecyclerView.

            super(itemView);

            mImageView = itemView.findViewById(R.id.imageView);
            mTextView1 = itemView.findViewById(R.id.textView);
            mOpenButton = itemView.findViewById(R.id.openButton);

            mOpenButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = getAdapterPosition();

                    DataItem currentItem = mDataList.get(position);

                    String imageFile = currentItem.getText1() + ".jpg";
                    String dataFile = currentItem.getText1() + ".bin";

                    LearnPopup.openFile(imageFile, dataFile);

                    ((Activity)mContext).finish();
                }
            });


        }


    }


    DataSetAdapter(Context context, ArrayList<DataItem> dataList) {
        // The constructor passes the list of data and also the context of the Activity used.
        // The context is required for the RecyclerView to be able to close the LearnPopup when the user
        // clicks on one of the "Open" buttons.

        mDataList = dataList;
        mContext = context;
    }

    @Override
    public DataSetAdapter.DataSetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.data_item, parent, false);
        DataSetAdapter.DataSetViewHolder evh = new DataSetAdapter.DataSetViewHolder(v);
        return evh;
    }

    @Override
    public void onBindViewHolder(DataSetAdapter.DataSetViewHolder holder, int position) {
        // This method prevents data from being "Recycled". The RecyclerViews are made to be
        // efficient with memory and CPU so it just recycles the data we don't see. We still want
        // it to display the right filename and image though, so we can do that here.

        DataItem currentItem = mDataList.get(position);
        holder.mImageView.setImageResource(currentItem.getImageResource());
        holder.mTextView1.setText(currentItem.getText1());
    }

    @Override
    public int getItemCount() {
        // This method returns the size of the list.

        return mDataList.size();
    }
}
