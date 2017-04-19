package com.sparq.quizpolls.application.userinterface.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sparq.quizpolls.R;
import com.sparq.quizpolls.application.SPARQApplication;
import com.sparq.quizpolls.application.userinterface.QuestionareResultsActivity;
import com.sparq.quizpolls.application.userinterface.QuestionareActivity;
import com.sparq.quizpolls.application.userinterface.model.QuizItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static com.sparq.quizpolls.application.SPARQApplication.USER_TYPE.TEACHER;
import static com.sparq.quizpolls.application.userinterface.model.Questionare.QUESTIONARE_TYPE.QUIZ;

/**
 * Created by sarahcs on 2/24/2017.
 */

public class QuizListAdapter extends RecyclerView.Adapter<QuizListAdapter.MyViewHolder>{

    private ArrayList<QuizItem> quizzes;
    private Context mContext;

    private static int colors[] = {
            R.color.warning,
            R.color.warningDark,
    };

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView quizName;
        public TextView quizDate;
        public LinearLayout quizImage;
        public TextView quizQuestions;
        public ImageView quizStatusImage;
        public CardView cardView;

        public MyViewHolder(View view) {
            super(view);
            quizName = (TextView) view.findViewById(R.id.quiz_name);
            quizDate = (TextView) view.findViewById(R.id.quiz_date);
            quizImage = (LinearLayout) view.findViewById(R.id.quiz_image);
            quizQuestions = (TextView) view.findViewById(R.id.quiz_questions);
            quizStatusImage = (ImageView) view.findViewById(R.id.quiz_status);
            cardView = (CardView) view.findViewById(R.id.card_view);

        }
    }


    public QuizListAdapter(Context context, ArrayList<QuizItem> quizzes) {
        this.quizzes = quizzes;
        this.mContext = context;
    }

    @Override
    public QuizListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_quiz_list, parent, false);

        return new QuizListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(QuizListAdapter.MyViewHolder holder, int position) {
        final QuizItem quiz = quizzes.get(position);

        holder.quizName.setText(quiz.getName());
        holder.quizDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(quiz.getDate()));
        holder.quizQuestions.setText(String.valueOf(quiz.getNumberOfQuestions()));

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(SPARQApplication.getUserType() == TEACHER){
                    Intent intent = new Intent(mContext, QuestionareResultsActivity.class);
                    intent.putExtra(QuestionareResultsActivity.QUESTIONARE_TYPE, QUIZ);
                    intent.putExtra(QuestionareResultsActivity.QUESTIONARE_ID, quiz.getQuestionareId());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                }else{
                    if(quiz.hasAnswered()){
                        Toast.makeText(mContext, mContext.getResources().getString(R.string.has_answered), Toast.LENGTH_SHORT);
                    }
                    else{
                        Intent intent = new Intent(mContext, QuestionareActivity.class);
                        intent.putExtra(QuestionareActivity.QUESTIONARE_TYPE, QUIZ);
                        intent.putExtra(QuestionareActivity.QUESTIONARE_ID, quiz.getQuestionareId());
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                    }
                }
            }
        });

//        holder.quizStatusImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                WifiUtils.disableWifi(mContext);
//
//                quiz.setState(QuizItem.QUIZ_STATE.INACTIVE);
//                notifyDataSetChanged();
//            }
//        });

        switch(quiz.getState()){
            case ACTIVE:
                holder.quizStatusImage.setBackgroundResource(R.drawable.ic_bookmark_play);
                break;
            case INACTIVE:
                holder.quizStatusImage.setBackgroundResource(R.drawable.ic_bookmark_pause);
                break;
        }

        if(position % 2 == 0){
            holder.quizImage.setBackgroundColor(mContext.getResources().getColor(colors[0]));
        }
        else{
            holder.quizImage.setBackgroundColor(mContext.getResources().getColor(colors[1]));
        }


    }

    @Override
    public int getItemCount() {
        return quizzes.size();
    }
}
