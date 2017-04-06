package com.sparq.application.userinterface.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.sparq.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OptionsAdapter extends RecyclerView.Adapter<OptionsAdapter.MyViewHolder> {

    private ArrayList<String> options;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView optionName;
        public ImageView deleteOption;

        public MyViewHolder(View view) {
            super(view);
            optionName = (TextView) view.findViewById(R.id.option_text);
            deleteOption = (ImageView) view.findViewById(R.id.delete_option);
        }
    }


    public OptionsAdapter(ArrayList<String> options) {
        this.options = options;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_new_option, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.optionName.setText(options.get(position));

        holder.deleteOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                options.remove(position);
                notifyItemRemoved(position);
            }
        });

        // FIXME: 4/7/2017 We need a better solution than this when the size becomes 0 throws nullpointerexception if everything is deleted
        if(options.size() == 1){
            holder.deleteOption.setEnabled(false);
        }

    }

    @Override
    public int getItemCount() {
        return options.size();
    }

    public ArrayList<String> getOptions(){
        return this.options;
    }

}