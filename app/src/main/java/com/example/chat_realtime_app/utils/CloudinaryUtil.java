package com.example.chat_realtime_app.utils;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class CloudinaryUtil {

    // Upload image on Cloudinary và save URL in Firestore
    public static void uploadAvatar(Context context, Uri imageUri, String uploadPreset, String cloudName,
                                    DocumentReference userRef, Runnable onSuccess, Runnable onFail) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
            byte[] imageBytes = new byte[inputStream.available()];
            inputStream.read(imageBytes);
            inputStream.close();

            String uploadUrl = "https://api.cloudinary.com/v1_1/" + cloudName + "/image/upload";
            OkHttpClient client = new OkHttpClient();

            MultipartBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", "avatar.jpg",
                            RequestBody.create(imageBytes, MediaType.parse("image/*")))
                    .addFormDataPart("upload_preset", uploadPreset)
                    .build();

            Request request = new Request.Builder()
                    .url(uploadUrl)
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    e.printStackTrace();
                    if (onFail != null) onFail.run();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull okhttp3.Response response) throws IOException {
                    if (response.isSuccessful()) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response.body().string());
                            String imageUrl = jsonResponse.getString("secure_url");

                            // Lưu vào Firestore
                            userRef.update("avatarUrl", imageUrl)
                                    .addOnSuccessListener(aVoid -> {
                                        if (onSuccess != null) onSuccess.run();
                                    })
                                    .addOnFailureListener(e -> {
                                        if (onFail != null) onFail.run();
                                    });

                        } catch (Exception e) {
                            e.printStackTrace();
                            if (onFail != null) onFail.run();
                        }
                    } else {
                        if (onFail != null) onFail.run();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            if (onFail != null) onFail.run();
        }
    }
}
