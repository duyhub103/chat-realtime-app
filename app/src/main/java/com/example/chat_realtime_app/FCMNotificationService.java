package com.example.chat_realtime_app;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.chat_realtime_app.model.UserModel;
import com.example.chat_realtime_app.utils.AndroidUtil;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.firestore.DocumentSnapshot;

import com.example.chat_realtime_app.utils.FirebaseUtil;
import com.example.chat_realtime_app.utils.AndroidUtil;

import java.util.Map;

public class FCMNotificationService extends FirebaseMessagingService {

    // Nhận token mới khi cài đặt app hoặc đăng nhập lại
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        if (FirebaseUtil.isLoggedIn()) {
            FirebaseUtil.currentUserDetails().update("fcmToken", token);
        }
    }

    // Khi nhận thông báo
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        Log.d("FCMService", "Message received: " + message.getData().toString());

        // 🔹 Ưu tiên đọc từ "data" vì server bạn đang gửi theo kiểu data-only
        if (message.getData().size() > 0) {
            String title = message.getData().get("title");
            String body = message.getData().get("body");
            String userId = message.getData().get("userId");

            if (userId == null || userId.isEmpty() || title == null || body == null) { //không có userId th bỏ qua tránh crash
                Log.e("FCMService", "Missing data: title=" + title + ", body=" + body + ", userId=" + userId);
                return;
            }

            // 🔹 Lấy thông tin người gửi từ Firestore
            FirebaseUtil.allUserCollectionReference().document(userId).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            if (doc != null && doc.exists()) {
                                UserModel sender = doc.toObject(UserModel.class);
                                if (sender != null) {
                                    showNotification(this, title, body, sender);
                                    Log.d("FCMService", "Notification shown for user: " + userId);
                                }
                            }
                        }
                        else{
                            Log.e("FCMService", "Firestore fetch failed", task.getException());
                        }
                    });
        }
    }

    // Hiển thị popup notification
    private void showNotification(Context context, String title, String body, UserModel sender) {
        String channelId = "chat_message_channel";

        // Intent để mở đúng ChatActivity
        Intent intent = new Intent(context, ChatActivity.class);
        AndroidUtil.passUserModelAsIntent(intent, sender);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                sender.getUserId().hashCode(), // mã ID riêng cho mỗi user
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Âm thanh
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.launcher_icon) // bạn nên thêm icon này vào res/drawable
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager manager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Với Android 8+ phải tạo channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Chat Messages",
                    NotificationManager.IMPORTANCE_HIGH
            );
            manager.createNotificationChannel(channel);
        }

        // Hiển thị thông báo
        try {
            manager.notify(sender.getUserId().hashCode(), builder.build());  // Sử dụng hash làm ID
        } catch (Exception e) {
            Log.e("FCMService", "Error notifying", e);
        }
    }

}

