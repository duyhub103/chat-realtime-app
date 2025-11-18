//package com.example.chat_realtime_app;
//
//import androidx.appcompat.app.AppCompatActivity;
//import android.os.Bundle;
//import android.util.Log;
//
//import com.example.chat_realtime_app.utils.KeywordUtils;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//import java.util.List;
//
//public class UpdateKeywordsActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        updateAllUsers();
//    }
//
//    private void updateAllUsers() {
//        FirebaseFirestore.getInstance().collection("users")
//                .get()
//                .addOnSuccessListener(query -> {
//                    for (DocumentSnapshot doc : query) {
//                        String username = doc.getString("username");
//
//                        if (username != null) {
//                            List<String> keys = KeywordUtils.generateKeywords(username.toLowerCase());
//                            doc.getReference().update("searchKeywords", keys);
//
//                            Log.d("KEY_UPDATE", "Updated: " + username);
//                        }
//                    }
//
//                    Log.d("KEY_UPDATE", "Done updating keywords!");
//                    finish(); // đóng activity sau khi chạy xong
//                })
//                .addOnFailureListener(e -> Log.e("KEY_UPDATE", "Error: " + e.getMessage()));
//    }
//}
