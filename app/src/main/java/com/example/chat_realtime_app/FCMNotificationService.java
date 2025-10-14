package com.example.chat_realtime_app;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FCMNotificationService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage msg) {

        // 🔹 Lấy title, body từ payload
        String title = msg.getNotification() != null ? msg.getNotification().getTitle() : "Tin nhắn mới";
        String body = msg.getNotification() != null ? msg.getNotification().getBody() : "";

        // 🔹 Tạo Notification Channel cho Android 8+
        String CHANNEL_ID = "chat_default";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Chat Messages",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        // 🔹 Hiển thị thông báo
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.person_icon) // icon trắng 24x24dp
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        try {
            NotificationManagerCompat.from(this)
                    .notify((int) System.currentTimeMillis(), builder.build());
        } catch (SecurityException e) {
            e.printStackTrace();
        }

    }
}
