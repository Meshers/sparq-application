package com.sparq.application.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.sparq.R;
import com.sparq.application.model.QuizItem;

import java.util.List;

/**
 * Created by sarahcs on 2/24/2017.
 */

public class QuizListAdapter extends RecyclerView.Adapter<QuizListAdapter.MyViewHolder>{

    private List<QuizItem> quizzes;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView quizName;
        public TextView quizDate;
        public ImageView quizImage;

        public MyViewHolder(View view) {
            super(view);
            quizName = (TextView) view.findViewById(R.id.quiz_name);
            quizDate = (TextView) view.findViewById(R.id.quiz_date);
            quizImage = (ImageView) view.findViewById(R.id.quiz_image);
        }
    }


    public QuizListAdapter(List<QuizItem> quizzes) {
        this.quizzes = quizzes;
    }

    @Override
    public QuizListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_quiz_list, parent, false);

        return new QuizListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(QuizListAdapter.MyViewHolder holder, int position) {
        QuizItem quiz = quizzes.get(position);
        holder.quizName.setText(quiz.getName());
        holder.quizDate.setText(quiz.getDate().toString());

        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        // generate color based on a key (same key returns the same color), useful for list/grid views
        int color = generator.getColor(quiz.getName());

        TextDrawable drawable = TextDrawable.builder()
                .buildRound(String.valueOf(quiz.getName().charAt(0)), color);
        holder.quizImage.setImageDrawable(drawable);

    }

    @Override
    public int getItemCount() {
        return quizzes.size();
    }
}
