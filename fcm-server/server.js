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
  res.status(200).send(`🚀 Server running`);
});

// Gửi FCM: ưu tiên data (tự render notification ở client)
app.post("/send", async (req, res) => {
  try {
    const { fcmToken, title, body, data } = req.body || {};
    if (!fcmToken) return res.status(400).json({ success:false, error:"Missing fcmToken" });

    const message = {
      token: fcmToken,
      // Có thể bỏ hẳn `notification` để luôn vào onMessageReceived
      // notification: { title: title || "", body: body || "" },
      data: {
        title: title || "Tin nhắn mới",
        body: body || "",
        ...data, // otherUserId, otherUsername, otherAvatarUrl, chatroomId
      },
    };

    const response = await admin.messaging().send(message);
    res.status(200).json({ success:true, response });
  } catch (e) {
    res.status(500).json({ success:false, error:e.message });
  }
});

const PORT = process.env.PORT || 10000;
app.listen(PORT, () => console.log(`🚀 Server running on port ${PORT}`));
