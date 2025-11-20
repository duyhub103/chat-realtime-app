# Chat Realtime App 💬 — CHATTY

Ứng dụng nhắn tin thời gian thực trên **Android**, xây dựng bằng **Java + Firebase**. Hỗ trợ chat 1-1, cập nhật hồ sơ, tìm kiếm nâng cao và thông báo đẩy qua **Node.js server**.

---

## 🌟 Tính năng chính

### 🔐 Authentication
- Đăng nhập / Đăng ký bằng **Email & Password**
- Đăng nhập bằng **OTP qua số điện thoại** (Firebase Phone Auth)
- Tách riêng giao diện cho từng loại đăng nhập

### 💬 Messaging
- Chat 1–1 thời gian thực bằng **Firestore**
- Hiển thị tin nhắn theo người gửi / người nhận
- Danh sách cuộc trò chuyện (Recent chats)

### 🔍 Tìm kiếm người dùng
- Tìm kiếm theo **Username**
- Hỗ trợ tìm kiếm nâng cao bằng **Keyword Tokenization**

### 👤 Hồ sơ cá nhân (Profile)
- Cập nhật **Avatar** (Cloudinary)
- Cập nhật **Username**, Email hoặc Phone
- Ngày sinh, giới tính

### 🔔 Thông báo (Notifications)
- Sử dụng **Firebase Cloud Messaging (FCM)**
- Backend **Node.js** tự triển khai
- Gửi thông báo khi có tin nhắn mới

---

## 🛠️ Công nghệ sử dụng
| Thành phần | Công nghệ |
|-----------|-----------|
| App | Android – Java, XML |
| Backend realtime | Firebase Firestore |
| Authentication | Firebase Auth |
| Notifications | FCM + Node.js Server |
| Media Hosting | Cloudinary |
| Libraries | Glide, ImagePicker, OkHttp |

---

## 🚀 Quick Start
**Dùng chung 1 Firebase Project + 1 Cloudinary Config**

### 1️⃣ Yêu cầu môi trường
- Android Studio (mới nhất)
- JDK 11+
- Node.js 16+
- Thiết bị thật hoặc Emulator

### 2️⃣ Clone dự án
```bash
git clone https://github.com/duyhub103/chat-realtime-app.git
cd chat-realtime-app
```

### 3️⃣ Cấu hình Firebase (quan trọng ⚠️)
| File | Dùng cho | Đặt tại |
|------|----------|---------|
| `google-services.json` | Android app | `app/` |
| `serviceAccountKey.json` | FCM server | `fcm-server/` |

📩 Liên hệ **haogduy12345@gmail.com** để lấy 2 file này.

### 4️⃣ Chạy Android App
1. Mở Android Studio
2. *File > Open* → chọn thư mục dự án
3. Chờ Gradle Sync
4. Nhấn ▶ để chạy ứng dụng

### 5️⃣ Chạy FCM Notification Server (Node.js)
```bash
cd fcm-server
npm install
npm start
```
Server chạy tại: **http://localhost:10000**

Bạn có thể deploy lên Render/Heroku.

---

## 📂 Cấu trúc dự án
```
chat-realtime-app/
├── app/
│   ├── src/main/java/...      # Mã nguồn Android Java
│   ├── src/main/res/          # Layout, drawable, icons
│   └── google-services.json   # (tự thêm)
│
├── fcm-server/
│   ├── server.js              # FCM Node server
│   ├── serviceAccountKey.json # (tự thêm)
│   └── package.json
│
└── README.md
```

---

## 🔑 Cấu hình API Key
### 📸 Cloudinary
Trong `ProfileFragment.java`:
| Key | Value |
|------|--------|
| Cloud Name | `dq6ygkf8k` |
| Upload Preset | `unsigned_chat_avatar` |

### 🔗 API gửi tin nhắn
Trong `ChatActivity.java`:
```
https://chat-realtime-app-mgyn.onrender.com/send
```

---

## 🤝 Quy tắc làm việc nhóm (Git)
- Tạo branch mới khi làm tính năng:
```bash
git checkout -b feature/ten-tinh-nang
```
- Commit:
```bash
git commit -m "Xong login email"
```
- Push:
```bash
git push origin feature/ten-tinh-nang
```
Sau đó lên GitHub tạo **Pull Request → Review → Merge**.

---

## 📄 License
MIT License — No copyright.

Phát triển bởi **Nhóm 5 (Duy - Tùng)**

Môn học: *Phát triển ứng dụng di động — Mobile Application Development*

Phiên bản: **1.0.0**

