package com.example.chat_realtime_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chat_realtime_app.model.UserModel;
import com.example.chat_realtime_app.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

public class LoginEmailActivity extends AppCompatActivity {

    EditText emailInput, passwordInput;
    Button loginBtn;
    ProgressBar progressBar;
    TextView signUpText;
    View phoneLoginBtn;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_email);

        emailInput = findViewById(R.id.et_email);
        passwordInput = findViewById(R.id.et_password);
        loginBtn = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.login_progress);
        signUpText = findViewById(R.id.tv_sign_up_here);
        phoneLoginBtn = findViewById(R.id.btn_login_phone);

        progressBar.setVisibility(View.GONE);

        // Đăng nhập
        loginBtn.setOnClickListener(v -> loginUser());
        
        
        signUpText.setOnClickListener(v -> {
            startActivity(new Intent(this, SignUpEmailActivity.class));
        });
        phoneLoginBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginPhoneNumberActivity.class));
        });

        // Chuyển sang đăng ký hoặc login phone
        signUpText.setOnClickListener(v -> {
            startActivity(new Intent(LoginEmailActivity.this, SignUpEmailActivity.class));
            finish();
        });


        phoneLoginBtn.setOnClickListener(v -> startActivity(new Intent(this, LoginPhoneNumberActivity.class)));
    }

    private void loginUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        setInProgress(true);

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    setInProgress(false);
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, "Login failed: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    void checkUserProfile() {
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            setInProgress(false);
            if (task.isSuccessful()) {
                DocumentSnapshot doc = task.getResult();
                if (doc.exists()) {
                    startMain();
                } else {
                    createUserProfile();
                }
            }
        });
    }

    void createUserProfile() {
        String email = emailInput.getText().toString().trim();
        userModel = new UserModel();
        userModel.setEmail(email);
        userModel.setUserId(FirebaseUtil.currentUserId());
        userModel.setCreatedTimestamp(Timestamp.now());
        userModel.setUsername(email.substring(0, email.indexOf("@")));

        FirebaseUtil.currentUserDetails().set(userModel).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                startMain();
            } else {
                Toast.makeText(this, "Failed to save user", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void startMain() {
        Intent intent = new Intent(LoginEmailActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    void setInProgress(boolean inProgress) {
        progressBar.setVisibility(inProgress ? View.VISIBLE : View.GONE);
        loginBtn.setVisibility(inProgress ? View.GONE : View.VISIBLE);
    }
}
