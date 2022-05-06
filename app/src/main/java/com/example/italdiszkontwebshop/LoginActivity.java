package com.example.italdiszkontwebshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private static final String LOG_TAG = LoginActivity.class.getName();
    private static final String PREF_KEY = RegistrationActivity.class.getPackage().toString();
    private SharedPreferences preferences;
    private FirebaseAuth mAuth;

    EditText emailET;
    EditText passwordET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        emailET = findViewById(R.id.emailET);
        passwordET = findViewById(R.id.passwordET);

        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        String email = preferences.getString("email", "");
        String password = preferences.getString("password", "");

        emailET.setText(email);
        passwordET.setText(password);

        Log.i(LOG_TAG, "onCreate");
    }

    public void login(View view) {
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();

        boolean wrongInput = false;

        if (emailET.length() == 0) {
            emailET.setError("Ki kell tölteni!");
            wrongInput = true;
        }

        if (passwordET.length() == 0) {
            passwordET.setError("Ki kell tölteni!");
            wrongInput = true;
        }

        if (wrongInput) {
            return;
        }

        Log.i(LOG_TAG, email + "sikeresen bejelentkezett");

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(LOG_TAG, "Sikeres bejelentkezés");
                    grantAccess();
                } else {
                    Log.d(LOG_TAG, "Sikertelen bejelentkezés");
                    Toast.makeText(LoginActivity.this, "Hiba: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void cancel(View view) {
        finish();
    }

    private void grantAccess(){
        Intent intent = new Intent(this, ProductListActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("email", emailET.getText().toString());
        editor.putString("password", passwordET.getText().toString());
        editor.apply();

        Log.i(LOG_TAG, "onPause");
    }
}