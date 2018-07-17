package com.example.michel.quizzes20;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    ArrayList<User> allUsers = new ArrayList<>();

    EditText usernameEditText;
    EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Intent intent = getIntent();
        User user = (User) intent.getSerializableExtra("newUser");
        if (user != null) {
            allUsers.add(user);
        }

        usernameEditText = (EditText) findViewById(R.id.et_username);
        passwordEditText = (EditText) findViewById(R.id.et_password);

        loadAllUsersFromFile();
    }

    @Override
    protected void onPause() {
        saveAllUsersToFile();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        saveAllUsersToFile();
        super.onDestroy();
    }

    private void loadAllUsersFromFile() {
        FileInputStream fileInputStream;
        ObjectInputStream objectInputStream;
        try {
            fileInputStream = openFileInput("users");
            objectInputStream = new ObjectInputStream(fileInputStream);
            List<?> genericListFromFile = (List<?>) objectInputStream.readObject();
            populateArrayListOfUsersFrom(genericListFromFile);
        } catch (FileNotFoundException e) {
            File file = new File("users");
        } catch (IOException e) {
            Log.e("IOException", "There was an error loading the users database.");
        } catch (ClassNotFoundException e) {
            Log.e("ClassNotFoundException", "No users found in file.");
        }
    }

    private void populateArrayListOfUsersFrom(List<?> genericList) {
        for (Object object : genericList) {
            if (object instanceof User) {
                allUsers.add((User) object);
            }
        }
    }

    private void saveAllUsersToFile() {
        FileOutputStream fileOutputStream;
        ObjectOutputStream objectOutputStream;
        try {
            fileOutputStream = openFileOutput("users", Context.MODE_PRIVATE);
            objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(allUsers);
        } catch (FileNotFoundException e) {
            Log.e("FileNotFoundException", "File was not found.");
        } catch (IOException e) {
            Log.e("IOException", "Error loading file.");
        }
    }

    public void onClickLoginButton(View view) {
        String inputtedUsername = usernameEditText.getText().toString();
        String inputtedPassword = passwordEditText.getText().toString();

        for (User user : allUsers) {
            if (inputtedUsername.equals(user.getUsername()) && inputtedPassword.equals(user.getPassword())) {
                Intent startMainActivityIntent = new Intent(this, MainActivity.class);
                startMainActivityIntent.putExtra("user", user);
                startActivity(startMainActivityIntent);
            }
        }
    }

    public void onClickCreateAccountButton(View view) {
        Intent startCreateNewAccountActivity = new Intent(this, CreateNewAccountActivity.class);
        startCreateNewAccountActivity.putExtra("registeredUsers", allUsers);
        startActivity(startCreateNewAccountActivity);
    }

    //onClickCreateAccountButton -- new activity to create an account. Then implement functionality in main menu.
}
