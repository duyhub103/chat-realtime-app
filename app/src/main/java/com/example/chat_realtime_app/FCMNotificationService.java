package com.example.chat_realtime_app;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class FCMNotificationService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage msg) {
        String title = "Tin nhắn mới";
        String body  = "";

        Map<String, String> data = msg.getData();
        if (data != null && !data.isEmpty()) {
            if (data.containsKey("title")) title = data.get("title");
            if (data.containsKey("body"))  body  = data.get("body");
        } else if (msg.getNotification() != null) {
            if (msg.getNotification().getTitle() != null) title = msg.getNotification().getTitle();
            if (msg.getNotification().getBody()  != null) body  = msg.getNotification().getBody();
        }

        String otherUserId    = data.getOrDefault("otherUserId", "");
        String otherUsername  = data.getOrDefault("otherUsername", "");
        String otherAvatarUrl = data.getOrDefault("otherAvatarUrl", "");
        String chatroomId     = data.getOrDefault("chatroomId", "");

        // Channel cho Android 8+
        String CHANNEL_ID = "chat_default";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel ch = new NotificationChannel(
                    CHANNEL_ID, "Chat", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager nm = getSystemService(NotificationManager.class);
            if (nm != null) nm.createNotificationChannel(ch);
        }

        // Intent mở thẳng ChatActivity
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("otherUserId", otherUserId);
        intent.putExtra("otherUsername", otherUsername);
        intent.putExtra("otherAvatarUrl", otherAvatarUrl);
        intent.putExtra("chatroomId", chatroomId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pi = PendingIntent.getActivity(
                this,
                (int) System.currentTimeMillis(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder b = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.person_icon)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setContentIntent(pi)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            NotificationManagerCompat.from(this).notify((int) System.currentTimeMillis(), b.build());
        }
    }
}

