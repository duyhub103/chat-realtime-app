import express from "express";
import admin from "firebase-admin";
import bodyParser from "body-parser";
import cors from "cors";

const app = express();
app.use(cors());
app.use(bodyParser.json());

// Đọc key từ biến môi trường, render cung cấp
const serviceAccount  = JSON.parse(process.env.SERVICE_ACCOUNT);

// Khởi tạo Firebase Admin SDK
admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});

// API gửi notification
app.post("/send", async (req, res) => {
  try {
    const { fcmToken, title, body } = req.body;

    const message = {
      token: fcmToken,
      notification: {
        title,
        body
      }
    };

    const response = await admin.messaging().send(message);
    console.log("Sent successfully:", response);
    res.status(200).send({ success: true, response });
  } catch (err) {
    console.error("Error sending FCM:", err);
    res.status(500).send({ success: false, error: err.message });
  }
});

// Cổng mặc định
const PORT = process.env.PORT || 10000;
app.listen(PORT, () => console.log(`🚀 Server running on port ${PORT}`));
