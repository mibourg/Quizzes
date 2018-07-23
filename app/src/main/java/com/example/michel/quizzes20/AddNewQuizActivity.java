package com.example.michel.quizzes20;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AddNewQuizActivity extends AppCompatActivity {

    User loggedInUser;

    ArrayList<Quiz> allSavedQuizzes = new ArrayList<>();

    ArrayList<Question> questionsInQuizBeingCreated = new ArrayList<>();
    LinearLayout questionsHolderLinearLayout;

    boolean editingMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_quiz);

        Intent intent = getIntent();
        List<?> quizzesObjects = (List<?>) intent.getSerializableExtra("quizzes");
        if (quizzesObjects != null) {
            for (Object object : quizzesObjects) {
                if (object instanceof Quiz) {
                    allSavedQuizzes.add((Quiz) object);
                }
            }
        }

        loggedInUser = (User) intent.getSerializableExtra("user");

        questionsHolderLinearLayout = (LinearLayout) findViewById(R.id.ll_questions_holder);

        checkForQuizToEdit();
    }

    @Override
    public void onBackPressed() {
        handleBackPressWithAlertDialog();
    }

    @Override
    protected void onPause() {
        finish();
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            handleBackPressWithAlertDialog();
            return true;
        }
        return false;
    }

    private void handleBackPressWithAlertDialog() {
        if (editingMode) {
            EditText quizNameEditText = (EditText) findViewById(R.id.et_quiz_name);
            String quizName = quizNameEditText.getText().toString();

            final Quiz quizToEdit = (Quiz) getIntent().getSerializableExtra("quizToEdit");

            if (quizToEdit != null) {
                if (quizName.equals(quizToEdit.getName()) && quizToEdit.getQuestions().equals(questionsInQuizBeingCreated)) {
                    Intent startMainActivityIntent = new Intent(AddNewQuizActivity.this, MainActivity.class);
                    startMainActivityIntent.putExtra("quiz", quizToEdit);
                    startMainActivityIntent.putExtra("user", loggedInUser);
                    startActivity(startMainActivityIntent);
                } else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                    alertDialogBuilder.setTitle(R.string.are_you_sure);
                    alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
                    alertDialogBuilder.setMessage(R.string.changes_will_be_lost);
                    alertDialogBuilder.setPositiveButton(R.string.return_to_menu, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent startMainActivityIntent = new Intent(AddNewQuizActivity.this, MainActivity.class);
                            startMainActivityIntent.putExtra("quiz", quizToEdit);
                            startMainActivityIntent.putExtra("user", loggedInUser);
                            startActivity(startMainActivityIntent);
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
            }
        } else {
            Intent startMainActivityIntent = new Intent(AddNewQuizActivity.this, MainActivity.class);
            startMainActivityIntent.putExtra("user", loggedInUser);
            startActivity(startMainActivityIntent);
        }
    }

    public void addNewQuestion(View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        final LinearLayout inputQuestionInfoLinearLayout = new LinearLayout(this);
        getLayoutInflater().inflate(R.layout.input_question_info, inputQuestionInfoLinearLayout);
        alertDialogBuilder.setView(inputQuestionInfoLinearLayout);
        alertDialogBuilder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText questionNameEditText = (EditText) inputQuestionInfoLinearLayout.findViewById(R.id.et_question_name);
                EditText correctAnswerEditText = (EditText) inputQuestionInfoLinearLayout.findViewById(R.id.et_correct_answer);
                EditText falseAnswer1EditText = (EditText) inputQuestionInfoLinearLayout.findViewById(R.id.et_false_answer1);
                EditText falseAnswer2EditText = (EditText) inputQuestionInfoLinearLayout.findViewById(R.id.et_false_answer2);
                EditText falseAnswer3EditText = (EditText) inputQuestionInfoLinearLayout.findViewById(R.id.et_false_answer3);

                String questionNameText = questionNameEditText.getText().toString();
                String correctAnswerText = correctAnswerEditText.getText().toString();
                String falseAnswer1Text = falseAnswer1EditText.getText().toString();
                String falseAnswer2Text = falseAnswer2EditText.getText().toString();
                String falseAnswer3Text = falseAnswer3EditText.getText().toString();

                if (!questionNameText.isEmpty() && !correctAnswerText.isEmpty() && !falseAnswer1Text.isEmpty() && !falseAnswer2Text.isEmpty() && !falseAnswer3Text.isEmpty()) {
                    ArrayList<Answer> answers = new ArrayList<>();

                    Answer correctAnswer = new Answer(correctAnswerEditText.getText().toString(), true);
                    Answer falseAnswer1 = new Answer(falseAnswer1EditText.getText().toString(), false);
                    Answer falseAnswer2 = new Answer(falseAnswer2EditText.getText().toString(), false);
                    Answer falseAnswer3 = new Answer(falseAnswer3EditText.getText().toString(), false);

                    answers.add(correctAnswer);
                    answers.add(falseAnswer1);
                    answers.add(falseAnswer2);
                    answers.add(falseAnswer3);

                    Question questionToAdd = new Question(questionNameEditText.getText().toString(), answers);

                    boolean questionAlreadyExists = false;
                    for (Question question : questionsInQuizBeingCreated) {
                        if (question.getQuestionText().equals(questionToAdd.getQuestionText())) {
                            questionAlreadyExists = true;
                        }
                    }

                    HashSet<String> answerSet = new HashSet<>();
                    for (Answer answer : answers) {
                        answerSet.add(answer.getAnswerText());
                    }

                    if (!questionAlreadyExists && answerSet.size() == answers.size()) {
                        questionsInQuizBeingCreated.add(questionToAdd);
                        updateQuestionsHolderLinearLayout();
                    } else if (questionAlreadyExists) {
                        AlertDialog.Builder questionAlreadyExistsDialog = new AlertDialog.Builder(AddNewQuizActivity.this);
                        questionAlreadyExistsDialog.setIcon(android.R.drawable.ic_dialog_alert);
                        questionAlreadyExistsDialog.setTitle(R.string.question_already_exists);
                        questionAlreadyExistsDialog.setMessage(R.string.two_same_questions);
                        questionAlreadyExistsDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        questionAlreadyExistsDialog.create().show();
                    } else if (answerSet.size() < answers.size()) {
                        AlertDialog.Builder duplicateAnswerDialog = new AlertDialog.Builder(AddNewQuizActivity.this);
                        duplicateAnswerDialog.setIcon(android.R.drawable.ic_dialog_alert);
                        duplicateAnswerDialog.setTitle(R.string.duplicate_answer);
                        duplicateAnswerDialog.setMessage(R.string.unique_answers_only);
                        duplicateAnswerDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        duplicateAnswerDialog.create().show();
                    }

                } else {
                    dialog.cancel();
                }
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

    private void updateQuestionsHolderLinearLayout() {
        questionsHolderLinearLayout.removeAllViews();
        for (Question question : questionsInQuizBeingCreated) {
            LinearLayout questionEditDeleteLayout = new LinearLayout(this);
            getLayoutInflater().inflate(R.layout.question_edit_delete, questionEditDeleteLayout);

            TextView questionNameTextView = (TextView) questionEditDeleteLayout.findViewById(R.id.tv_question_name);
            TextView correctAnswerTextView = (TextView) questionEditDeleteLayout.findViewById(R.id.tv_correct_answer);

            ArrayList<TextView> falseAnswerTextViews = new ArrayList<>();

            TextView falseAnswer1TextView = (TextView) questionEditDeleteLayout.findViewById(R.id.tv_false_answer1);
            TextView falseAnswer2TextView = (TextView) questionEditDeleteLayout.findViewById(R.id.tv_false_answer2);
            TextView falseAnswer3TextView = (TextView) questionEditDeleteLayout.findViewById(R.id.tv_false_answer3);

            falseAnswerTextViews.add(falseAnswer1TextView);
            falseAnswerTextViews.add(falseAnswer2TextView);
            falseAnswerTextViews.add(falseAnswer3TextView);

            questionNameTextView.setText(question.getQuestionText());
            correctAnswerTextView.setText(question.getCorrectAnswer().getAnswerText());

            ArrayList<Answer> questionAnswers = question.getAnswers();

            int falseAnswerTextViewCounter = 0;
            for (int i = 0; i < questionAnswers.size(); i++) {
                if (!questionAnswers.get(i).isCorrect()) {
                    falseAnswerTextViews.get(falseAnswerTextViewCounter).setText(questionAnswers.get(i).getAnswerText());
                    falseAnswerTextViewCounter++;
                }
            }

            questionsHolderLinearLayout.addView(questionEditDeleteLayout);
        }
    }

    public void finishAddingQuiz(View view) {
        EditText quizNameEditText = (EditText) findViewById(R.id.et_quiz_name);

        String quizToAddName = quizNameEditText.getText().toString();
        Quiz quizBeingCreated = new Quiz(quizToAddName, questionsInQuizBeingCreated);

        if (quizBeingCreated.getQuestions().size() > 0) {
            boolean quizAlreadyExists = false;
            for (Quiz quiz : allSavedQuizzes) {
                if (quiz.getName().equals(quizToAddName)) {
                    quizAlreadyExists = true;
                }
            }

            if (!quizAlreadyExists) {
                Intent startMainActivityIntent = new Intent(AddNewQuizActivity.this, MainActivity.class);
                startMainActivityIntent.putExtra("quiz", quizBeingCreated);
                startMainActivityIntent.putExtra("user", loggedInUser);
                startActivity(startMainActivityIntent);
            } else {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
                alertDialogBuilder.setTitle(R.string.quiz_already_exists);
                alertDialogBuilder.setMessage(R.string.two_same_quizzes);
                alertDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialogBuilder.create().show();
            }
        } else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
            alertDialogBuilder.setTitle(R.string.quiz_empty);
            alertDialogBuilder.setMessage(R.string.quiz_must_have_questions);
            alertDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            alertDialogBuilder.create().show();
        }
    }

    public void editQuestion(View view) {
        final Question questionToEdit = getQuestionFromButtonClick((Button) view);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        LinearLayout inputQuestionInfoLinearLayout = new LinearLayout(this);
        getLayoutInflater().inflate(R.layout.input_question_info, inputQuestionInfoLinearLayout);
        alertDialogBuilder.setView(inputQuestionInfoLinearLayout);

        final EditText questionNameEditText = (EditText) inputQuestionInfoLinearLayout.findViewById(R.id.et_question_name);
        final EditText correctAnswerEditText = (EditText) inputQuestionInfoLinearLayout.findViewById(R.id.et_correct_answer);
        final EditText falseAnswer1EditText = (EditText) inputQuestionInfoLinearLayout.findViewById(R.id.et_false_answer1);
        final EditText falseAnswer2EditText = (EditText) inputQuestionInfoLinearLayout.findViewById(R.id.et_false_answer2);
        final EditText falseAnswer3EditText = (EditText) inputQuestionInfoLinearLayout.findViewById(R.id.et_false_answer3);

        questionNameEditText.setText(questionToEdit.getQuestionText());
        correctAnswerEditText.setText(questionToEdit.getCorrectAnswer().getAnswerText());

        ArrayList<TextView> falseAnswerTextViews = new ArrayList<>();

        falseAnswerTextViews.add(falseAnswer1EditText);
        falseAnswerTextViews.add(falseAnswer2EditText);
        falseAnswerTextViews.add(falseAnswer3EditText);

        ArrayList<Answer> questionAnswers = questionToEdit.getAnswers();

        int falseAnswerTextViewCounter = 0;
        for (int i = 0; i < questionAnswers.size(); i++) {
            if (!questionAnswers.get(i).isCorrect()) {
                falseAnswerTextViews.get(falseAnswerTextViewCounter).setText(questionAnswers.get(i).getAnswerText());
                falseAnswerTextViewCounter++;
            }
        }

        alertDialogBuilder.setPositiveButton(R.string.edit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ArrayList<Answer> answers = new ArrayList<>();

                Answer correctAnswer = new Answer(correctAnswerEditText.getText().toString(), true);
                Answer falseAnswer1 = new Answer(falseAnswer1EditText.getText().toString(), false);
                Answer falseAnswer2 = new Answer(falseAnswer2EditText.getText().toString(), false);
                Answer falseAnswer3 = new Answer(falseAnswer3EditText.getText().toString(), false);

                answers.add(correctAnswer);
                answers.add(falseAnswer1);
                answers.add(falseAnswer2);
                answers.add(falseAnswer3);

                Question question = new Question(questionNameEditText.getText().toString(), answers);

                questionsInQuizBeingCreated.remove(questionToEdit);
                questionsInQuizBeingCreated.add(question);
                updateQuestionsHolderLinearLayout();
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

    public void deleteQuestion(View view) {
        final Question questionToDelete = getQuestionFromButtonClick((Button) view);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.are_you_sure);
        alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialogBuilder.setMessage(R.string.delete_question_message);
        alertDialogBuilder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                questionsInQuizBeingCreated.remove(questionToDelete);
                updateQuestionsHolderLinearLayout();
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

    private Question getQuestionFromButtonClick(Button button) {
        LinearLayout questionHoldingLinearLayout = (LinearLayout) button.getParent();

        TextView questionToFindNameTextView = (TextView) questionHoldingLinearLayout.findViewById(R.id.tv_question_name);

        String questionToFindName = questionToFindNameTextView.getText().toString();

        int questionPosition = 0;
        for (Question question : questionsInQuizBeingCreated) {
            if (question.getQuestionText().equals(questionToFindName)) {
                questionPosition = questionsInQuizBeingCreated.indexOf(question);
            }
        }

        return questionsInQuizBeingCreated.get(questionPosition);
    }

    private void checkForQuizToEdit() {
        Intent intent = getIntent();
        Quiz quizToEdit = (Quiz) intent.getSerializableExtra("quizToEdit");
        if (quizToEdit != null) {
            editingMode = true;
            questionsInQuizBeingCreated = quizToEdit.getQuestions();
            EditText quizNameEditText = (EditText) findViewById(R.id.et_quiz_name);
            quizNameEditText.setText(quizToEdit.getName());
            updateQuestionsHolderLinearLayout();
        }
    }
}
