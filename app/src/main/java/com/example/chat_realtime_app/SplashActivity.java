package com.example.chat_realtime_app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.chat_realtime_app.model.UserModel;
import com.example.chat_realtime_app.utils.FirebaseUtil;
import com.example.chat_realtime_app.utils.AndroidUtil;


public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if(getIntent().getExtras()!=null){
            //from notification
            String userId = getIntent().getExtras().getString("userId");

            if (userId != null && !userId.isEmpty()){
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
                        });
            }else{
                // Trường hợp không có userId → chỉ mở MainActivity
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
            }
        }else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(FirebaseUtil.isLoggedIn()){
                        startActivity(new Intent(SplashActivity.this,MainActivity.class));
                    }else{
                        startActivity(new Intent(SplashActivity.this,LoginPhoneNumberActivity.class));
                    }
                    finish();
                }
            },1000);
        }
    }
}