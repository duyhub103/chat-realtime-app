// server.js (ESM)
import express from "express";
import admin from "firebase-admin";
import bodyParser from "body-parser";
import cors from "cors";
import fs from "fs";

const app = express();
app.use(cors());
app.use(bodyParser.json());

let serviceAccount;
if (fs.existsSync("./serviceAccountKey.json")) {
  serviceAccount = JSON.parse(fs.readFileSync("./serviceAccountKey.json"));
} else {
  serviceAccount = JSON.parse(process.env.SERVICE_ACCOUNT);
}

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
});

// Health check
app.get("/", (req, res) => {
  res.status(200).send(`🚀 Server fix running`);
});

// Gửi FCM: ưu tiên data (tự render notification ở client)
app.post("/send", async (req, res) => {
  try {
    const { fcmToken, title, body, data } = req.body || {};
    if (!fcmToken) return res.status(400).json({ success: false, error: "Missing fcmToken" });
    if (!title || !body) return res.status(400).json({ success: false, error: "Missing title or body" });

    const message = {
      token: fcmToken,
      data: {
        title: title,
        body: body,
        ...data, // Ví dụ: { userId: '...' }
      },
      android: { priority: 'high' }, // Đảm bảo high priority cho Android
      apns: { headers: { 'apns-priority': '10' } }, // Cho iOS nếu cần
    };

    const response = await admin.messaging().send(message);
    console.log('FCM sent:', response); // Log cho debug
    res.status(200).json({ success: true, response });
  } catch (e) {
    console.error('Error sending FCM:', e); // Log lỗi
    res.status(500).json({ success: false, error: e.message });
  }
});

const PORT = process.env.PORT || 10000;
app.listen(PORT, () => console.log(`🚀 Server running on port ${PORT}`));