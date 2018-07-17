package com.example.michel.quizzes20;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    User loggedInUser;

    ArrayList<Quiz> allSavedQuizzes = new ArrayList<>();
    ArrayList<LinearLayout> buttonHoldingLinearLayouts = new ArrayList<>();
    LinearLayout quizzesHolderLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        quizzesHolderLinearLayout = (LinearLayout) findViewById(R.id.ll_quizzes_holder);

        Quiz quizToAdd = (Quiz) getIntent().getSerializableExtra("quiz");

        if (quizToAdd != null) {
            Log.d("Question 1", quizToAdd.getQuestions().get(0).getQuestionText());
            allSavedQuizzes.add(quizToAdd);
        }

        loggedInUser = (User) getIntent().getSerializableExtra("user");

        Log.d("Username", loggedInUser.getUsername());

        loadQuizzesFromSavedFile();
        populateButtonHoldingLinearLayouts();
        loadViewsIntoQuizzesHolderLinearLayout();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        saveQuizzesToFile();
        finish();
    }

    private void saveQuizzesToFile() {
        try {
            FileOutputStream fileOutputStream = openFileOutput("quizzesDatabase_" + loggedInUser.getUsername(), Context.MODE_PRIVATE);
            Log.d("Quiz database file name", "quizzesDatabase_" + loggedInUser.getUsername());
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(allSavedQuizzes);
        } catch (FileNotFoundException e) {
            File quizzesDatabaseFile = new File("quizzesDatabase");
        } catch (IOException e) {
            Log.e("IOException", "Error loading file.");
        }
    }

    private void loadQuizzesFromSavedFile() {
        try {
            FileInputStream fileInputStream = openFileInput("quizzesDatabase_" + loggedInUser.getUsername());
            Log.d("Quiz database file name", "quizzesDatabase_" + loggedInUser.getUsername());
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            List<?> savedQuizzesList = (List<?>) objectInputStream.readObject();
            populateSavedQuizzesListFrom(savedQuizzesList);
        } catch (FileNotFoundException e) {
            File quizzesDatabaseFile = new File("quizzesDatabase");
        } catch (IOException e) {
            Log.e("IOException", "Error loading file.");
        } catch (ClassNotFoundException e) {
            Log.e("ClassNotFoundException", "Quizzes not found in the file. Perhaps the file is empty.");
        }
    }

    private void populateSavedQuizzesListFrom(List<?> list) {
        for (Object object : list) {
            if (object instanceof Quiz) {
                allSavedQuizzes.add((Quiz) object);
            }
        }
    }

    private void populateButtonHoldingLinearLayouts() {
        buttonHoldingLinearLayouts.clear();
        for (Quiz quiz : allSavedQuizzes) {
            LinearLayout buttonHoldingLinearLayout = new LinearLayout(this);
            getLayoutInflater().inflate(R.layout.quiz_delete_edit, buttonHoldingLinearLayout);
            Button doQuizButton = (Button) buttonHoldingLinearLayout.findViewById(R.id.btn_do_quiz);
            doQuizButton.setText(quiz.getName());
            buttonHoldingLinearLayouts.add(buttonHoldingLinearLayout);
        }
    }

    private void loadViewsIntoQuizzesHolderLinearLayout() {
        quizzesHolderLinearLayout.removeAllViews();
        for (LinearLayout buttonHoldingLinearLayout : buttonHoldingLinearLayouts) {
            quizzesHolderLinearLayout.removeView(buttonHoldingLinearLayout);
            quizzesHolderLinearLayout.addView(buttonHoldingLinearLayout);
        }
    }

    public void addQuiz(View view) {
        Intent startNewQuizActivityIntent = new Intent(this, AddNewQuizActivity.class);
        startNewQuizActivityIntent.putExtra("quizzes", allSavedQuizzes);
        startNewQuizActivityIntent.putExtra("user", loggedInUser);
        startActivity(startNewQuizActivityIntent);
    }

    public void doQuiz(View view) {
        Quiz quizToDo = getQuizBasedOnButtonPosition((Button) view);

        Intent startDoQuizActivityIntent = new Intent(this, DoQuizActivity.class);
        startDoQuizActivityIntent.putExtra("quizToDo", quizToDo);
        startDoQuizActivityIntent.putExtra("user", loggedInUser);
        startActivity(startDoQuizActivityIntent);
    }

    public void editQuiz(View view) {
        Quiz quizToEdit = getQuizBasedOnButtonPosition((Button) view);

        allSavedQuizzes.remove(quizToEdit);

        Intent startNewQuizActivityIntent = new Intent(this, AddNewQuizActivity.class);
        startNewQuizActivityIntent.putExtra("quizzes", allSavedQuizzes);
        startNewQuizActivityIntent.putExtra("quizToEdit", quizToEdit);
        startNewQuizActivityIntent.putExtra("user", loggedInUser);
        startActivity(startNewQuizActivityIntent);
    }

    public void deleteQuiz(View view) {
        final Quiz quizToDelete = getQuizBasedOnButtonPosition((Button) view);

        Log.d("Quiz to delete", quizToDelete.getName());

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialogBuilder.setTitle(R.string.are_you_sure);
        alertDialogBuilder.setMessage(R.string.delete_quiz_message);
        alertDialogBuilder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                allSavedQuizzes.remove(quizToDelete);
                populateButtonHoldingLinearLayouts();
                loadViewsIntoQuizzesHolderLinearLayout();
            }
        });
        alertDialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialogBuilder.create().show();
    }

    private Quiz getQuizBasedOnButtonPosition(Button button) {
        LinearLayout quizHoldingLinearLayout = (LinearLayout) button.getParent();

        Button doQuizButton = (Button) quizHoldingLinearLayout.findViewById(R.id.btn_do_quiz);

        String quizToFindName = doQuizButton.getText().toString();

        Log.d("Name of Quiz to Find", quizToFindName);

        int quizPosition = 0;
        for (Quiz quiz : allSavedQuizzes) {
            Log.d("Quiz name", quiz.getName());
            if (quiz.getName().equals(quizToFindName)) {
                quizPosition = allSavedQuizzes.indexOf(quiz);
            }
        }

        Log.d("Quiz position", String.valueOf(quizPosition));

        return allSavedQuizzes.get(quizPosition);
    }

}
