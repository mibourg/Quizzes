package com.example.michel.quizzes20;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class QuizFinishedActivity extends AppCompatActivity {

    User loggedInUser;

    TextView quizScoreTextView;
    TextView totalPointsTextView;

    Quiz completedQuiz;
    int quizScore;
    int totalPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_finished);

        quizScoreTextView = (TextView) findViewById(R.id.tv_quiz_score);
        totalPointsTextView = (TextView) findViewById(R.id.tv_total_points);

        quizScore = getIntent().getIntExtra("quizScore", 0);
        completedQuiz = (Quiz) getIntent().getSerializableExtra("quizToDo");
        loggedInUser = (User) getIntent().getSerializableExtra("user");

        Log.d("Username", loggedInUser.getUsername());

        totalPoints = completedQuiz.getQuestions().size();

        loadInformationIntoViews();
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

    private void loadInformationIntoViews() {
        quizScoreTextView.setText(String.valueOf(quizScore));
        totalPointsTextView.setText(String.valueOf(totalPoints));
    }

    public void retryQuiz(View view) {
        Intent startDoQuizActivityIntent = new Intent(this, DoQuizActivity.class);
        startDoQuizActivityIntent.putExtra("quizToDo", completedQuiz);
        startDoQuizActivityIntent.putExtra("user", loggedInUser);
        startActivity(startDoQuizActivityIntent);
    }

    public void returnToMainMenu(View view) {
        Intent startMainActivityIntent = new Intent(this, MainActivity.class);
        startMainActivityIntent.putExtra("user", loggedInUser);
        startActivity(startMainActivityIntent);
    }
}
