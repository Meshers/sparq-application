package com.sparq.application.userinterface.adapter;

import android.support.v7.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.ArrayRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.sparq.R;

import java.util.ArrayList;

/**
 * Created by sarahcs on 2/26/2017.
 */

public class DialogListAdapter  extends RecyclerView.Adapter<DialogListAdapter.ButtonVH> {

    public interface ItemCallback {

        void onItemClicked(int itemIndex, MaterialDialog dialog);
    }

    private final CharSequence[] items;
    private final int[] images;
    private ItemCallback itemCallback;
    private static MaterialDialog dialog;

    public DialogListAdapter(Context context, ArrayList<String> arrayResId, int[] images) {
        this(arrayResId.toArray(new CharSequence[arrayResId.size()]), images);
    }

    private DialogListAdapter(CharSequence[] items, int[] images) {

        this.items = items;
        this.images = images;
    }

    public void setCallbacks(ItemCallback itemCallback, MaterialDialog dialog) {
        this.itemCallback = itemCallback;
        this.dialog = dialog;
    }

    @Override
    public ButtonVH onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dialog_list, parent, false);
        return new ButtonVH(view, this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ButtonVH holder, int position) {
        holder.title.setText(items[position]);
        holder.image.setBackgroundResource(images[position]);
    }

    @Override
    public int getItemCount() {
        return items.length;
    }

    public static class ButtonVH extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView title;
        final ImageView image;
        final DialogListAdapter adapter;

        ButtonVH(View itemView, DialogListAdapter adapter) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.poll_option_name);
            image = (ImageView) itemView.findViewById(R.id.poll_option_image);

            this.adapter = adapter;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (adapter.itemCallback == null)
                return;
            else {
                adapter.itemCallback.onItemClicked(getAdapterPosition(), dialog);
            }
        }
    }
}
