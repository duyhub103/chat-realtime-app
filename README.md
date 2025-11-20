Chat Realtime App 💬    CHATTY

Ứng dụng nhắn tin thời gian thực trên Android, xây dựng bằng Java + Firebase, hỗ trợ nhắn tin 1-1, cập nhật hồ sơ người dùng, tìm kiếm nâng cao và thông báo đẩy (Push Notifications) qua Node.js Server.

🌟 Tính Năng Chính
🔐 Authentication

Đăng nhập / Đăng ký bằng Email & Password

Đăng nhập bằng số điện thoại OTP (Firebase Phone Auth)

Giao diện được tách riêng cho từng loại login

💬 Messaging

Chat 1–1 thời gian thực bằng Firestore

Hiển thị tin nhắn người gửi / người nhận

Danh sách cuộc trò chuyện

🔍 Search người dùng

Tìm kiếm theo Username

Hỗ trợ tìm kiếm nâng cao bằng Keyword Tokenization

👤 Hồ sơ cá nhân (Profile)

Cập nhật Avatar (Cloudinary)

Cập nhật Username

Cập nhật Email hoặc Phone (tùy loại tài khoản)

Ngày sinh, giới tính

🔔 Thông báo (Notifications)

Sử dụng Firebase Cloud Messaging

Server Node.js (tự triển khai)

Hỗ trợ gửi thông báo khi có tin nhắn mới

🛠️ Công Nghệ Sử Dụng
| Thành phần       | Công nghệ                  |
| ---------------- | -------------------------- |
| App              | Android – Java, XML        |
| Backend realtime | Firebase Firestore         |
| Authentication   | Firebase Auth              |
| Notifications    | FCM + Node.js Server       |
| Media Hosting    | Cloudinary                 |
| Libraries        | Glide, ImagePicker, OkHttp |


🚀 Quick Start

Dùng chung 1 Firebase Project + 1 Cloudinary Config

1️⃣ Yêu cầu môi trường

Android Studio (mới nhất)

JDK 11+

Node.js (16+)

Thiết bị thật hoặc emulator

2️⃣ Clone dự án
git clone https://github.com/duyhub103/chat-realtime-app.git

cd chat-realtime-app

3️⃣ Cấu hình Firebase (quan trọng ⚠️)

| File                     | Dùng cho    | Đặt tại thư mục |
| ------------------------ | ----------- | --------------- |
| `google-services.json`   | Android app | `app/`          |
| `serviceAccountKey.json` | FCM server  | `fcm-server/`   |


➡ Liên hệ haogduy12345@gmail.com để lấy 2 file này

4️⃣ Chạy Android App

Mở Android Studio

File > Open → chọn thư mục dự án

Chờ Gradle Sync

Nhấn ▶ để chạy

5️⃣ Chạy FCM Notification Server (Node.js)
cd fcm-server
npm install
npm start


Server chạy tại:

http://localhost:10000

Nếu muốn, bạn có thể deploy lên Render/Heroku.

📂 Cấu trúc dự án
chat-realtime-app/
├── app/
│   ├── src/main/java/...      # Android Java code
│   ├── src/main/res/          # Layout, drawable, icons
│   └── google-services.json   # (bạn phải tự thêm)
│
├── fcm-server/
│   ├── server.js              # FCM Node server
│   ├── serviceAccountKey.json # (bạn phải tự thêm)
│   └── package.json
│
└── README.md

🔑 Cấu hình API Key
📸 Cloudinary

Trong ProfileFragment.java:

| Key           | Value                  |
| ------------- | ---------------------- |
| Cloud Name    | `dq6ygkf8k`            |
| Upload Preset | `unsigned_chat_avatar` |


Trong ChatActivity.java:

https://chat-realtime-app-mgyn.onrender.com/send

🤝 Quy tắc làm việc nhóm (Git)

Tạo branch mới khi làm tính năng:

git checkout -b feature/ten-tinh-nang


Commit:

git commit -m "Xong login email"


Push:

git push origin feature/ten-tinh-nang


Lên GitHub tạo Pull Request → Review → Merge.

📄 License

MIT License — No copyright.

Phát triển bởi Nhóm 5 (Duy - Tùng)
Phát triển ứng dụng di động - Mobile Application Development
Phiên bản: 1.0.0



