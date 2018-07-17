package com.example.michel.quizzes20;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class CreateNewAccountActivity extends AppCompatActivity {

    ArrayList<User> registeredUsers = new ArrayList<>();

    EditText emailEditText;
    EditText desiredUsernameEditText;
    EditText desiredPasswordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_account);

        emailEditText = (EditText) findViewById(R.id.et_email);
        desiredUsernameEditText = (EditText) findViewById(R.id.et_desired_username);
        desiredPasswordEditText = (EditText) findViewById(R.id.et_desired_password);

        Intent intent = getIntent();
        List<?> genericList = (List<?>) intent.getSerializableExtra("registeredUsers");
        populateRegisteredUsersFrom(genericList);
    }

    private void populateRegisteredUsersFrom(List<?> genericList) {
        for (Object object : genericList) {
            if (object instanceof User) {
                registeredUsers.add((User) object);
            }
        }
    }

    public void onClickSignUpButton(View view) {
        String email = emailEditText.getText().toString();
        String desiredUsername = desiredUsernameEditText.getText().toString();
        String desiredPassword = desiredPasswordEditText.getText().toString();

        if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            boolean validEmail = checkEmailForMatches(email);
            boolean validUsername = checkUsernameForMatches(desiredUsername);

            if (validUsername && validEmail) {
                User newAccountToAdd = new User(email, desiredUsername, desiredPassword, new ArrayList<Quiz>());
                Intent startLoginActivityIntent = new Intent(this, LoginActivity.class);
                startLoginActivityIntent.putExtra("newUser", newAccountToAdd);
                startActivity(startLoginActivityIntent);
            }

        } else {
            emailEditText.setError(getText(R.string.valid_email));
        }
    }

    private boolean checkEmailForMatches(String email) {
        boolean validEmail = true;
        for (User user : registeredUsers) {
            Log.d("email", user.getEmail());
            if (user.getEmail().equals(email)) {
                emailEditText.setError(getText(R.string.email_taken));
                validEmail = false;
                break;
            }
        }
        return validEmail;
    }

    private boolean checkUsernameForMatches(String username) {
        boolean validUsername = true;
        for (User user : registeredUsers) {
            Log.d("username", user.getUsername());
            if (user.getUsername().equals(username)) {
                desiredUsernameEditText.setError(getText(R.string.username_taken));
                validUsername = false;
                break;
            }
        }
        return validUsername;
    }

    public void onClickAlreadyHaveAccountButton(View view) {
        Intent startLoginActivityIntent = new Intent(this, LoginActivity.class);
        startActivity(startLoginActivityIntent);
    }



}
