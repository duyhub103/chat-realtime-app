package com.example.chat_realtime_app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.chat_realtime_app.model.UserModel;
import com.example.chat_realtime_app.utils.FirebaseUtil;
import com.example.chat_realtime_app.utils.AndroidUtil;


public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if(getIntent().getExtras()!=null){
            //from notification
            String userId = getIntent().getExtras().getString("userId");
            //Log.d("SplashActivity", "From notification with userId: " + userId);

            if (userId != null && !userId.isEmpty()){
                openChatFromNotification(userId);

            }else{
                openMainAct();
                // Trường hợp không có userId → chỉ mở MainActivity
            }
        }else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(FirebaseUtil.isLoggedIn()){
                        startActivity(new Intent(SplashActivity.this,MainActivity.class));
                    }else{
                        startActivity(new Intent(SplashActivity.this,LoginEmailActivity.class));
                    }
                    finish();
                }
            },1000);
        }
    }

    private void openMainAct() {
        new Handler().postDelayed(() -> {
            if (FirebaseUtil.isLoggedIn()) {
                startActivity(new Intent(this, MainActivity.class));
            } else {
                openLoginEmail();
            }
            finish();
        }, 1000);

        }

    private void openLoginEmail() {
        Intent intent = new Intent(this, LoginEmailActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    private void openChatFromNotification(String userId) {
        FirebaseUtil.allUserCollectionReference().document(userId).get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        UserModel model = task.getResult().toObject(UserModel.class);

                        Intent mainIntent = new Intent(this,MainActivity.class);
                        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(mainIntent);

                        Intent intent = new Intent(this, ChatActivity.class);
                        AndroidUtil.passUserModelAsIntent(intent,model);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        Log.e("SplashActivity", "Failed to fetch user", task.getException());
                        openLoginEmail();
                    }
                });
    }
}
