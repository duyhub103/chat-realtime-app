package com.example.chat_realtime_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chat_realtime_app.model.UserModel;
import com.example.chat_realtime_app.utils.FirebaseUtil;
import com.example.chat_realtime_app.utils.KeywordUtils;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpEmailActivity extends AppCompatActivity {

    private EditText fullnameInput, emailInput, passwordInput, confirmPasswordInput;
    private CheckBox termsCheckBox;
    private Button signUpBtn;
    private ProgressBar progressBar;
    private TextView loginHereText;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_email);

        auth = FirebaseAuth.getInstance();

        fullnameInput = findViewById(R.id.et_fullname);
        emailInput = findViewById(R.id.et_email);
        passwordInput = findViewById(R.id.et_password);
        confirmPasswordInput = findViewById(R.id.et_confirm_password);
        termsCheckBox = findViewById(R.id.cb_terms);
        signUpBtn = findViewById(R.id.btn_sign_up);
        progressBar = findViewById(R.id.signup_progress);
        loginHereText = findViewById(R.id.tv_login_here);

        signUpBtn.setOnClickListener(v -> registerUser());
        loginHereText.setOnClickListener(v ->
                startActivity(new Intent(this, LoginEmailActivity.class))
        );
    }

    private void registerUser() {
        String fullname = fullnameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        if (fullname.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!termsCheckBox.isChecked()) {
            Toast.makeText(this, "Please agree to the terms", Toast.LENGTH_SHORT).show();
            return;
        }

        setInProgress(true);

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    setInProgress(false);
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = auth.getCurrentUser();

                        UserModel user = new UserModel();
                        user.setUserId(firebaseUser.getUid());
                        user.setEmail(email);
                        user.setUsername(fullname);
                        user.setCreatedTimestamp(Timestamp.now());
                        user.setSearchKeywords(KeywordUtils.generateKeywords(fullname.toLowerCase()));

                        // Lưu vào Firestore
                        FirebaseUtil.currentUserDetails().set(user)
                                .addOnCompleteListener(saveTask -> {
                                    if (saveTask.isSuccessful()) {
                                        Toast.makeText(this, "Account created!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(this, MainActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(this, "Error saving user info", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(this, "Sign up failed: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void setInProgress(boolean inProgress) {
        progressBar.setVisibility(inProgress ? View.VISIBLE : View.GONE);
        signUpBtn.setEnabled(!inProgress);
    }
}
