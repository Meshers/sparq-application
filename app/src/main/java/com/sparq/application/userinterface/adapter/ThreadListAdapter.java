package com.sparq.application.userinterface.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.sparq.R;
import com.sparq.application.SPARQApplication;
import com.sparq.application.layer.almessage.AlVote;
import com.sparq.application.layer.pdu.ApplicationLayerPdu;
import com.sparq.application.userinterface.ConverstaionThreadActivity;
import com.sparq.application.userinterface.model.ConversationThread;
import com.sparq.util.Constants;

import java.text.SimpleDateFormat;
import java.util.List;

import static android.content.ContentValues.TAG;
import static com.sparq.application.SPARQApplication.SPARQInstance;

/**
 * Created by sarahcs on 2/24/2017.
 */

public class ThreadListAdapter extends RecyclerView.Adapter<ThreadListAdapter.MyViewHolder>{

    private static int colors[] = {
            R.color.colorAccent,
            R.color.colorAccentDark,
    };

    private List<ConversationThread> threads;
    private Context mContext;
    private boolean mbtnEnable;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView threadName;
        public TextView threadDate;
        public TextView threadVotes;
        public LinearLayout threadImage;
        public ImageView likeBtn, unlikeBtn, answerCount;
        public CardView card;

        public MyViewHolder(View view) {
            super(view);
            threadName = (TextView) view.findViewById(R.id.thread_name);
            threadDate = (TextView) view.findViewById(R.id.thread_date);
            threadVotes = (TextView) view.findViewById(R.id.thread_votes);
            threadImage = (LinearLayout) view.findViewById(R.id.thread_image);
            likeBtn = (ImageView) view.findViewById(R.id.like_image);
            unlikeBtn = (ImageView) view.findViewById(R.id.unlike_image);
            answerCount = (ImageView) view.findViewById(R.id.answer_count);
            card = (CardView) view.findViewById(R.id.card_view);
        }
    }


    public ThreadListAdapter(List<ConversationThread> threads, Context context, boolean btnEnable) {
        this.threads = threads;
        this.mContext = context;
        this.mbtnEnable = btnEnable;
    }

    @Override
    public ThreadListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_thread_list, parent, false);

        return new ThreadListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ThreadListAdapter.MyViewHolder holder, final int position) {
        final ConversationThread thread = threads.get(position);
        holder.threadName.setText(thread.getQuestionItem().getQuestion());
        holder.threadDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(thread.getDate()));
        holder.threadVotes.setText(String.valueOf(thread.getQuestionItem().getVotes()));

        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        // generate color based on a key (same key returns the same color), useful for list/grid views
        int color = generator.getColor(thread.getAnswers().size());

        TextDrawable drawable = TextDrawable.builder()
                .buildRound(String.valueOf(thread.getAnswers().size()), color);
        holder.answerCount.setImageDrawable(drawable);


        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConversationThread thread =  threads.get(position);

                Intent intent = new Intent(mContext, ConverstaionThreadActivity.class);
                intent.putExtra(ConverstaionThreadActivity.THREAD_ID, thread.getQuestionareId());
                intent.putExtra(ConverstaionThreadActivity.CREATOR_ID, thread.getCreator().getUserId());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });

        holder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SPARQApplication.sendThreadMessage(
                        ApplicationLayerPdu.TYPE.QUESTION_VOTE,
                        SPARQApplication.getBdcastAddress(),
                        null,
                        thread.getCreator().getUserId(),
                        thread.getQuestionareId(),
                        Constants.DEFAULT_ADDRESS,
                        Constants.DEFAULT_ADDRESS,
                        AlVote.VOTE_TYPE.UPVOTE);

                Toast.makeText(mContext, mContext.getResources().getString(R.string.vote_recorded), Toast.LENGTH_SHORT).show();
                SPARQApplication.getInstance().startTimer();

            }
        });

        holder.unlikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SPARQApplication.sendThreadMessage(
                        ApplicationLayerPdu.TYPE.QUESTION_VOTE,
                        SPARQApplication.getBdcastAddress(),
                        null,
                        thread.getCreator().getUserId(),
                        thread.getQuestionareId(),
                        Constants.DEFAULT_ADDRESS,
                        Constants.DEFAULT_ADDRESS,
                        AlVote.VOTE_TYPE.DOWNVOTE);

                Toast.makeText(mContext, mContext.getResources().getString(R.string.vote_recorded), Toast.LENGTH_SHORT).show();
                SPARQApplication.getInstance().startTimer();

            }
        });

        if(mbtnEnable == true && thread.getQuestionItem().hasVoted() == false){
            activateVotes(holder);
        }
        else{
            deactivateVotes(holder);
        }

        if(position % 2 == 0){
            holder.threadImage.setBackgroundColor(mContext.getResources().getColor(colors[0]));
        }
        else{
            holder.threadImage.setBackgroundColor(mContext.getResources().getColor(colors[1]));
        }

    }

    public void deactivateVotes(ThreadListAdapter.MyViewHolder holder){
        // deactivate the vote buttons
        holder.likeBtn.setImageResource(R.drawable.ic_like_disabled);
        holder.likeBtn.setEnabled(false);

        holder.unlikeBtn.setImageResource(R.drawable.ic_unlike_disabled);
        holder.unlikeBtn.setEnabled(false);
    }

    public void activateVotes(ThreadListAdapter.MyViewHolder holder){
        // deactivate the vote buttons
        holder.likeBtn.setImageResource(R.drawable.ic_like);
        holder.likeBtn.setEnabled(true);

        holder.unlikeBtn.setImageResource(R.drawable.ic_unlike);
        holder.unlikeBtn.setEnabled(true);
    }

    @Override
    public int getItemCount() {
        return threads.size();
    }
}
