package com.example.michel.quizzes20;

import java.io.Serializable;
import java.util.ArrayList;

public class Question implements Serializable {
    private String questionText;
    private ArrayList<Answer> answers = new ArrayList<>();

    public Question(String questionText, ArrayList<Answer> answers) {
        this.questionText = questionText;
        this.answers = answers;
    }

    public String getQuestionText() { return questionText; }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public ArrayList<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<Answer> answers) {
        this.answers = answers;
    }

    public Answer getCorrectAnswer() {
        for (Answer answer : answers) {
            if (answer.isCorrect()) {
                return answer;
            }
        }
        return null;
    }
}