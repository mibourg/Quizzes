package com.example.michel.quizzes20;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

public class DoQuizActivity extends AppCompatActivity {

    User loggedInUser;

    Quiz quizToDo;
    int questionNumber = 0;
    int quizScore = 0;

    TextView questionTextView;
    Button answer1Button;
    Button answer2Button;
    Button answer3Button;
    Button answer4Button;
    LinearLayout mainLinearLayout;

    ArrayList<Button> answerButtons = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do_quiz);

        quizToDo = (Quiz) getIntent().getSerializableExtra("quizToDo");
        loggedInUser = (User) getIntent().getSerializableExtra("user");

        Log.d("Username", loggedInUser.getUsername());

        questionTextView = (TextView) findViewById(R.id.tv_question);
        answer1Button = (Button) findViewById(R.id.btn_answer1);
        answer2Button = (Button) findViewById(R.id.btn_answer2);
        answer3Button = (Button) findViewById(R.id.btn_answer3);
        answer4Button = (Button) findViewById(R.id.btn_answer4);
        mainLinearLayout = (LinearLayout) findViewById(R.id.ll_do_quiz);

        answerButtons.add(answer1Button);
        answerButtons.add(answer2Button);
        answerButtons.add(answer3Button);
        answerButtons.add(answer4Button);

        shuffleQuizQuestions();
        shuffleQuizAnswers();
        loadViewsForCurrentQuestion();
    }

    @Override
    public void onBackPressed() {
        Intent startMainActivityIntent = new Intent(this, MainActivity.class);
        startMainActivityIntent.putExtra("user", loggedInUser);
        startActivity(startMainActivityIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent startMainActivityIntent = new Intent(this, MainActivity.class);
            startMainActivityIntent.putExtra("user", loggedInUser);
            startActivity(startMainActivityIntent);
            return true;
        }
        return false;
    }

    private void loadViewsForCurrentQuestion() {
        mainLinearLayout.setOnTouchListener(null);
        enableAllButtons();
        Question currentQuestion = quizToDo.getQuestions().get(questionNumber);

        questionTextView.setText(currentQuestion.getQuestionText());
        answer1Button.setText(currentQuestion.getAnswers().get(0).getAnswerText());
        answer2Button.setText(currentQuestion.getAnswers().get(1).getAnswerText());
        answer3Button.setText(currentQuestion.getAnswers().get(2).getAnswerText());
        answer4Button.setText(currentQuestion.getAnswers().get(3).getAnswerText());
    }

    private void shuffleQuizQuestions() {
        ArrayList<Question> questionsInQuizToDo = quizToDo.getQuestions();
        Collections.shuffle(questionsInQuizToDo);
        quizToDo.setQuestions(questionsInQuizToDo);
    }

    private void shuffleQuizAnswers() {
        for (Question question : quizToDo.getQuestions()) {
            ArrayList<Answer> answers = question.getAnswers();
            Collections.shuffle(answers);
            question.setAnswers(answers);
        }
    }

    private void disableAllButtons() {
        for (Button button : answerButtons) {
            button.setEnabled(false);
            button.setActivated(false);
            button.setClickable(false);
        }

    }

    private void enableAllButtons() {
        for (Button button : answerButtons) {
            button.setEnabled(true);
            button.setActivated(true);
            button.setClickable(true);
        }
    }

    public void onClickAnswerButton(View view) {
        Button answerButton = (Button) view;
        String answerText = answerButton.getText().toString();
        Question currentQuestion = quizToDo.getQuestions().get(questionNumber);

        if (currentQuestion.getCorrectAnswer().getAnswerText().equals(answerText)) {
            Toast.makeText(this, R.string.correct_answer_chosen, Toast.LENGTH_SHORT).show();
            answerButton.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.greenForCorrect), PorterDuff.Mode.MULTIPLY);
            quizScore++;
            disableAllButtons();
        } else {
            Toast.makeText(this, R.string.incorrect_answer_chosen, Toast.LENGTH_SHORT).show();
            answerButton.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.redForWrong), PorterDuff.Mode.MULTIPLY);
            setCorrectAnswerButtonColorToGreen(currentQuestion);
            disableAllButtons();
        }

        mainLinearLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int screenWidth = v.getWidth();
                if (event.getX() > screenWidth/2) {
                    questionNumber++;
                    if (questionNumber < quizToDo.getQuestions().size()) {
                        resetButtonColors();
                        loadViewsForCurrentQuestion();
                    } else {
                        startQuizFinishedActivity();
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private void setCorrectAnswerButtonColorToGreen(Question currentQuestion) {
        for (Button button : answerButtons) {
            if (button.getText().toString().equals(currentQuestion.getCorrectAnswer().getAnswerText())) {
                button.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.greenForCorrect), PorterDuff.Mode.MULTIPLY);
            }
        }
    }

    private void resetButtonColors() {
        for (Button button : answerButtons) {
            button.getBackground().clearColorFilter();
        }
    }

    private void startQuizFinishedActivity() {
        Intent startQuizFinishedActivityIntent = new Intent(DoQuizActivity.this, QuizFinishedActivity.class);
        startQuizFinishedActivityIntent.putExtra("quizToDo", quizToDo);
        startQuizFinishedActivityIntent.putExtra("quizScore", quizScore);
        startQuizFinishedActivityIntent.putExtra("user", loggedInUser);
        startActivity(startQuizFinishedActivityIntent);
    }
}
