package com.example.michel.quizzes20;

import java.io.Serializable;
import java.util.ArrayList;

public class Quiz implements Serializable {
    private String name;
    private ArrayList<Question> questions = new ArrayList<>();

    public Quiz(String name, ArrayList<Question> questions) {
        this.name = name;
        this.questions = questions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<Question> questions) {
        this.questions = questions;
    }
}
