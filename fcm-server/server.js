import express from "express";
import admin from "firebase-admin";
import bodyParser from "body-parser";
import cors from "cors";
import fs from "fs";

const app = express();
app.use(cors());
app.use(bodyParser.json());

// Đọc key từ biến môi trường, render cung cấp
//const serviceAccount  = JSON.parse(process.env.SERVICE_ACCOUNT);
let serviceAccount;


if (fs.existsSync("./serviceAccountKey.json")) {
  // 🔹 Dùng file local khi chạy trên máy
  serviceAccount = JSON.parse(fs.readFileSync("./serviceAccountKey.json"));
} else {
  // 🔹 Dùng biến môi trường khi deploy Render
  serviceAccount = JSON.parse(process.env.SERVICE_ACCOUNT);
}


// Khởi tạo Firebase Admin SDK
admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});

app.get("/", (req, res) => {
res.status(200).send("🚀 Server running on port ${PORT}");}
}

// API gửi notification
app.post("/send", async (req, res) => {
  try {
    const { fcmToken, title, body } = req.body || {};
    if (!fcmToken || !title) {
      return res.status(400).json({ success:false, error:"Missing fcmToken/title/body" });
    }
    const msg = { token: fcmToken, notification: { title, body: body || "" } };
    const response = await admin.messaging().send(msg);
    res.status(200).json({ success:true, response });
  } catch (e) {
    console.error("Error sending FCM:", e);
    res.status(500).json({ success:false, error:e.message });
  }
});


// Cổng mặc định
const PORT = process.env.PORT || 10000;
app.listen(PORT, () => console.log(`🚀 Server running on port ${PORT}`));
