package com.example.italdiszkontwebshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrationActivity extends AppCompatActivity {
    private static final String LOG_TAG = RegistrationActivity.class.getName();
    private static final String PREF_KEY = LoginActivity.class.getPackage().toString();
    private static final int SECRET_KEY = 99;
    private SharedPreferences preferences;
    private FirebaseAuth mAuth;

    EditText emailET, userNameET, passwordET, passwordAgainET, phoneNumberET, postalAddressET;
    CheckBox consentCB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();

        emailET = findViewById(R.id.emailET);
        userNameET = findViewById(R.id.userNameET);
        passwordET = findViewById(R.id.passwordET);
        passwordAgainET = findViewById(R.id.passwordAgainET);
        phoneNumberET = findViewById(R.id.phoneNumberET);
        postalAddressET = findViewById(R.id.postalAddressET);
        consentCB = findViewById((R.id.consentCB));

        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        String email = preferences.getString("email", "");
        String password = preferences.getString("password", "");

        emailET.setText(email);
        passwordET.setText(password);
        passwordAgainET.setText(password);

        Log.i(LOG_TAG, "onCreate");
    }

    public void register(View view) {
        String email = emailET.getText().toString();
        String userName = userNameET.getText().toString();
        String password = passwordET.getText().toString();
        String passwordConfirm = passwordAgainET.getText().toString();

        boolean wrongInput = false;

        if (emailET.length() == 0) {
            emailET.setError("Ki kell tölteni!");
            wrongInput = true;
        }

        if (userNameET.length() == 0) {
            userNameET.setError("Ki kell tölteni!");
            wrongInput = true;
        }

        if (passwordET.length() < 6) {
            passwordET.setError("Kevesebb mint 6 karakter!");
            wrongInput = true;
        }

        if (passwordAgainET.length() < 6) {
            passwordAgainET.setError("Kevesebb mint 6 karakter!");
            wrongInput = true;
        }

        if (phoneNumberET.length() == 0) {
            phoneNumberET.setError("Ki kell tölteni!");
            wrongInput = true;
        }

        if (postalAddressET.length() == 0) {
            postalAddressET.setError("Ki kell tölteni!");
            wrongInput = true;
        }

        if (!consentCB.isChecked()) {
            Toast.makeText(RegistrationActivity.this, "Nem vagy elég idős!", Toast.LENGTH_SHORT).show();
            wrongInput = true;
        }

        if (!password.equals(passwordConfirm)) {
            Toast.makeText(RegistrationActivity.this, "Nem egyeznek meg a jelszavak!", Toast.LENGTH_SHORT).show();
            wrongInput = true;
        }

        if (wrongInput) {
            return;
        }

        Log.i(LOG_TAG, userName + "sikeresen regisztrált");

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Log.d(LOG_TAG, "Sikeres regisztráció");
                    grantAccess();
                } else {
                    Log.d(LOG_TAG, "Sikertelen regisztráció", task.getException());
                    Toast.makeText(RegistrationActivity.this, "Hiba: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void cancel(View view) {
        finish();
    }

    private void grantAccess() {
        Intent intent = new Intent(this, ProductListActivity.class);
        intent.putExtra("SECRET_KEY", SECRET_KEY);
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