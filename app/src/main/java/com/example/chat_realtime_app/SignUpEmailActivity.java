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
            Toast.makeText(this, "Please fill your information", Toast.LENGTH_SHORT).show();
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

        FirebaseUtil.allUserCollectionReference()
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        setInProgress(false);
                        Toast.makeText(this, "Error checking email. Try again.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!task.getResult().isEmpty()) {
                        // email đã tồn tại trong firestore
                        setInProgress(false);
                        Toast.makeText(this, "This email is already exists", Toast.LENGTH_LONG).show();
                    } else {
                        // 2) Không trùng mail trong firestore thì tạo trong Auth
                        createAuthAccount(fullname, email, password);
                    }
                });
    }

    private void createAuthAccount(String fullname, String email, String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        setInProgress(false);
                        Toast.makeText(this,
                                "Sign up failed: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    // Tạo document user trong Firestore
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    if (firebaseUser == null) {
                        setInProgress(false);
                        Toast.makeText(this, "Error: user is null", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    UserModel user = new UserModel();
                    user.setUserId(firebaseUser.getUid());
                    user.setEmail(email);
                    user.setUsername(fullname);
                    user.setCreatedTimestamp(Timestamp.now());
                    user.setPhone(null); // signup bằng email nên phone để null
                    user.setSearchKeywords(
                            KeywordUtils.generateKeywords(fullname.toLowerCase())
                    );

                    FirebaseUtil.currentUserDetails().set(user)
                            .addOnCompleteListener(saveTask -> {
                                setInProgress(false);
                                if (saveTask.isSuccessful()) {
                                    Toast.makeText(this, "Account created!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(this, MainActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(this, "Error saving user info", Toast.LENGTH_SHORT).show();
                                }
                            });
                });
    }


    private void setInProgress(boolean inProgress) {
        progressBar.setVisibility(inProgress ? View.VISIBLE : View.GONE);
        signUpBtn.setEnabled(!inProgress);
    }
}
