package org.apptive.pictolearn;

import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import static org.apptive.pictolearn.EditActivity.textList;


public class ExampleAdapter extends RecyclerView.Adapter<ExampleAdapter.ExampleViewHolder> {
    // Refer to the DataSetAdapter class for description.

    private ArrayList<ExampleItem> mExampleList;
    private OnClickListener mListener;

    public interface OnClickListener {
        void OnDeleteClick(int position);
        void OnCheckClick(int position);
    }

    private static InputFilter filter = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            // This filter allows for blocking certain characters from the text input.
            // src: https://stackoverflow.com/questions/21828323/how-can-restrict-my-edittext-input-to-some-special-character-like-backslash-t/21828520#21828520

            for (int i = start; i < end; i++) {
                int type = Character.getType(source.charAt(i));
                if (type == Character.SURROGATE || type == Character.OTHER_SYMBOL) {
                    return "";
                }
            }

            return null;
        }
    };

    void setOnItemClickListener(OnClickListener listener) {
        mListener = listener;
    }

    static class ExampleViewHolder extends RecyclerView.ViewHolder {
        ImageView mDelButton;
        ImageView mCordButton;
        EditText mEditText;
        TextView mTextView;

        ExampleViewHolder(View itemView, final OnClickListener listener) {
            super(itemView);
            mDelButton = itemView.findViewById(R.id.delButton);
            mCordButton = itemView.findViewById(R.id.cordButton);
            mEditText = itemView.findViewById(R.id.editText);
            mTextView = itemView.findViewById(R.id.textView_num);

            mEditText.setFilters(new InputFilter[] { filter });

            mEditText.addTextChangedListener(new TextWatcher() {
                // The TextWatcher updates the textList ArrayList with the new text when the user
                // changes it in the input box.

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    if (s.length() > 0) {

                        int position = getAdapterPosition();

                        String text = s.toString();

                        textList.set(position, text);
                    }

                }

                @Override
                public void afterTextChanged(Editable s) {


                }
            });

            // The buttons have their OnClickListeners defined in the EditActivity, so they don't
            // have to be static which prevents memory leaks.

            mDelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();

                        if (position != RecyclerView.NO_POSITION) {
                            listener.OnDeleteClick(position);

                        }

                    }

                }
            });

            mCordButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();

                        if (position != RecyclerView.NO_POSITION) {
                            listener.OnCheckClick(position);

                        }

                    }

                }
            });




        }


    }

    ExampleAdapter(ArrayList<ExampleItem> exampleList) {
        mExampleList = exampleList;
    }

    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.example_item, parent, false);
        ExampleViewHolder evh = new ExampleViewHolder(v, mListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(ExampleViewHolder holder, int position) {
        holder.mEditText.setText(textList.get(position));
        String pos = Integer.toString(position + 1);
        holder.mTextView.setText(pos);
    }

    @Override
    public int getItemCount() {
        return mExampleList.size();
    }

}