package com.example.fit5046_a3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Signup extends AppCompatActivity {
    EditText password, password2, email;
    Button signup;
    TextView title, signupTitle, newPassword, comfirmPassword, newEmail, loginButton;
    FirebaseAuth fAuth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        password = findViewById(R.id.enterNewPassword);
        password2 = findViewById(R.id.enterNewPassword2);
        email = findViewById(R.id.enterNewEmail);
        signup = findViewById(R.id.signupButton);
        title = findViewById(R.id.title);
        signupTitle = findViewById(R.id.signupTitle);
        newPassword = findViewById(R.id.newPassword);
        comfirmPassword = findViewById(R.id.comfirmPassword);
        newEmail = findViewById(R.id.newEmail);
        loginButton = findViewById(R.id.loginBtn);

        fAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String e = email.getText().toString().trim();
                String p = password.getText().toString().trim();
                String p2 = password2.getText().toString().trim();

                // empty email check
                if (TextUtils.isEmpty(e)) {
                    email.setError("Email is Required.");
                    return;
                }

                // password check
                if (TextUtils.isEmpty(p)) {
                    password.setError("Password is required.");
                    return;
                }

                if (p.length() < 6) {
                    password.setError("Password must be >= 6 Characters");
                    return;
                }
                if (!p.equals(p2)){
                    password2.setError("Please Comfirm Password!");
                    return;
                }

                progressBar.setVisibility(View.INVISIBLE);

                // Register the user in firebase
                fAuth.createUserWithEmailAndPassword(e, p).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Signup.this, "User Created", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } else {
                            Toast.makeText(Signup.this, "Error!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Login.class));
            }
        });
    }
}