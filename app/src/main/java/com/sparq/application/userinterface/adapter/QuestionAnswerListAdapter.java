package com.sparq.application.userinterface.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.sparq.R;
import com.sparq.application.SPARQApplication;
import com.sparq.application.userinterface.model.AnswerItem;
import com.sparq.application.userinterface.model.EventItem;
import com.sparq.application.userinterface.model.QuestionItem;
import com.sparq.application.userinterface.model.UserItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QuestionAnswerListAdapter extends RecyclerView.Adapter<QuestionAnswerListAdapter.MyViewHolder> {

    private ArrayList<QuestionItem> questions;
    HashMap<Integer, AnswerItem> answers = new HashMap<>(0);
    private Context mContext;
    private HashMap<Integer, EditText> editTexts = new HashMap<>();
    private HashMap<Integer, QuestionareAdapter> adapters = new HashMap<>();

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView questionText;
        public EditText answerText;
        public RecyclerView recyclerView;
        public QuestionareAdapter innerAdapter;
        public LinearLayout linearLayout;

        public MyViewHolder(View view) {
            super(view);
            questionText = (TextView) view.findViewById(R.id.question_text);
            answerText = (EditText) view.findViewById(R.id.answer_text);
            recyclerView = (RecyclerView) view.findViewById(R.id.questionare_recycler_view);
            recyclerView.setVisibility(View.GONE);
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));

            innerAdapter = new QuestionareAdapter();
            recyclerView.setAdapter(innerAdapter);

            linearLayout = (LinearLayout) view.findViewById((R.id.layout_short_ans));
            linearLayout.setVisibility(View.GONE);
        }
    }


    public QuestionAnswerListAdapter(Context context, ArrayList<QuestionItem> questions) {
        this.mContext = context;
        this.questions = questions;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_question_full, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        QuestionItem question = questions.get(position);
        holder.questionText.setText(question.getQuestion());
        AnswerItem questionareAnswer = null;

        switch(question.getFormat()){
            case MCQ_SINGLE:
                holder.recyclerView.setVisibility(View.VISIBLE);
                Log.i("HERE", question.getOptions().toString());
                holder.innerAdapter.setData(question.getOptions(), question.getFormat()); // List of Strings
                holder.innerAdapter.setRowIndex(position);

                questionareAnswer = AnswerItem.getMCQSingleAnswer(
                        question.getQuestionId(),
                        new UserItem(SPARQApplication.getOwnAddress()),
                        question.getQuestionId(),
                        new UserItem(1),
                        null
                );

                adapters.put(question.getQuestionId(),holder.innerAdapter);
                break;
            case MCQ_MULTIPLE:
                holder.recyclerView.setVisibility(View.VISIBLE);
                holder.innerAdapter.setData(question.getOptions(), question.getFormat()); // List of Strings
                holder.innerAdapter.setRowIndex(position);

                questionareAnswer = AnswerItem.getMCQMultipleAnswer(
                        question.getQuestionId(),
                        new UserItem(SPARQApplication.getOwnAddress()),
                        question.getQuestionId(),
                        new UserItem(1),
                        null
                );

                adapters.put(question.getQuestionId(),holder.innerAdapter);
                break;
            case ONE_WORD:
                holder.linearLayout.setVisibility(View.VISIBLE);

                questionareAnswer = AnswerItem.getMCQOneWordAnswer(
                        question.getQuestionId(),
                        new UserItem(SPARQApplication.getOwnAddress()),
                        question.getQuestionId(),
                        new UserItem(1),
                        null
                );

                editTexts.put(question.getQuestionId(),holder.answerText);
                break;

            case SHORT:
                holder.linearLayout.setVisibility(View.VISIBLE);

                questionareAnswer = AnswerItem.getShortAnswer(
                        question.getQuestionId(),
                        new UserItem(SPARQApplication.getOwnAddress()),
                        question.getQuestionId(),
                        new UserItem(1),
                        null
                );

                editTexts.put(question.getQuestionId(),holder.answerText);
                break;
        }

        answers.put(question.getQuestionId(),questionareAnswer);
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public ArrayList<AnswerItem> getAnswerForQuestion(){

        for(int questionId: answers.keySet()){
            AnswerItem answer = answers.get(questionId);

            switch (answer.getFormat()){
                case MCQ_SINGLE:
                    Log.i("HERE single", adapters.get(questionId).getChoices().toString());
                    answer.setAnswerChoices(adapters.get(questionId).getChoices());
                    break;
                case MCQ_MULTIPLE:
                    Log.i("HERE multiple", adapters.get(questionId).getChoices().toString());
                    answer.setAnswerChoices(adapters.get(questionId).getChoices());
                    break;
                case ONE_WORD:
                case SHORT:
                    Log.i("HERE", editTexts.get(questionId).getText().toString());
                    answer.setAnswer(editTexts.get(questionId).getText().toString());
                    break;
            }
        }

        ArrayList<AnswerItem> answerArray = new ArrayList<AnswerItem>(answers.values());

        return answerArray;
    }
}