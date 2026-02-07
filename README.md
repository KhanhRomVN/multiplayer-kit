# Multiplayer Pause Mod

[![Mindustry Version](https://img.shields.io/badge/Mindustry-v154-blue.svg)](https://github.com/Anuken/Mindustry)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)
[![Version](https://img.shields.io/badge/Version-3.0-orange.svg)](CHANGELOG.md)

Một mod Mindustry cho phép người chơi không phải host cũng có thể tạm dừng (pause) game trong chế độ multiplayer.

## ✨ Tính năng

- 🎮 **Pause Multiplayer**: Cho phép bất kỳ người chơi nào (không chỉ host) tạm dừng game
- 🔔 **Toast Notifications**: Hiển thị thông báo khi ai đó pause/unpause game
- 🔐 **Kiểm soát quyền**: Host có thể giới hạn chỉ admin mới được pause
- 🔄 **Tự động đồng bộ**: Tùy chọn tự động sync khi pause/unpause để giảm desync
- ⚡ **Responsive**: Cập nhật trạng thái pause ngay lập tức cho tất cả clients

## 📋 Yêu cầu

- **Mindustry**: Phiên bản 154 trở lên
- **Cài đặt**: Cả host và người chơi muốn pause đều cần cài mod này

## 🚀 Cài đặt

### Cách 1: Từ file JAR
1. Tải file `pause-mod.jar` từ [Releases](../../releases)
2. Mở Mindustry
3. Vào **Mods** → **Open Mod Folder**
4. Copy file JAR vào thư mục mods
5. Restart Mindustry

### Cách 2: Build từ source
```bash
# Clone repository
git clone https://github.com/KhanhRomVN/multiplayer-pause.git
cd multiplayer-pause

# Build mod
./gradlew jar

# File output sẽ ở: build/libs/pause-modDesktop.jar
```

## 🎯 Sử dụng

1. **Pause game**: Nhấn phím pause (mặc định: `Space` hoặc `Pause Break`)
2. **Unpause**: Nhấn lại phím pause

### Cấu hình Settings

Vào **Settings** → **Multiplayer Pause** để điều chỉnh:

| Setting | Mô tả | Mặc định |
|---------|-------|----------|
| **Toasts** | Hiển thị thông báo khi pause/unpause | ✅ Bật |
| **Allow Any** | Cho phép bất kỳ ai pause (không chỉ admin) | ❌ Tắt |
| **Sync on Pause** | Tự động sync khi pause | ❌ Tắt |
| **Sync on Unpause** | Tự động sync khi unpause | ❌ Tắt |
| **Schedule Sync** | Lên lịch sync nếu sync gần đây đã thực hiện | ❌ Tắt |

> **Lưu ý**: Bật auto-sync có thể giúp giảm desync nhưng có thể gây lag nếu lạm dụng.

## 🔧 Cơ chế hoạt động

1. **Client gửi request**: Khi nhấn pause, client gửi packet `multiplayerpause-request` đến server
2. **Server xử lý**: Server kiểm tra quyền và toggle trạng thái pause
3. **Broadcast state**: Server gửi packet `multiplayerpause-updatestate` đến tất cả clients
4. **Clients cập nhật**: Mỗi client cập nhật UI và hiển thị toast notification

Chi tiết kỹ thuật xem tại [docs/API.md](docs/API.md)

## 📚 Tài liệu

- [API Documentation](docs/API.md) - Chi tiết về packet handlers và API
- [Settings Guide](docs/SETTINGS.md) - Hướng dẫn chi tiết về settings
- [Contributing](CONTRIBUTING.md) - Hướng dẫn đóng góp
- [Changelog](CHANGELOG.md) - Lịch sử thay đổi

## 🛠️ Development

### Build Requirements
- Java 8 hoặc cao hơn
- Gradle (wrapper included)

### Build Commands
```bash
# Build desktop version
./gradlew jar

# Build Android version (cần Android SDK)
./gradlew jarAndroid

# Build cả hai
./gradlew deploy
```

### Project Structure
```
multiplayer-pause/
├── src/
│   └── pauseMod/
│       └── Main.java          # Main mod class
├── assets/
│   └── bundles/
│       └── bundle.properties  # Localization
├── docs/                      # Documentation
├── build.gradle               # Build configuration
├── mod.hjson                  # Mod metadata
└── README.md                  # This file
```

## 🤝 Đóng góp

Contributions, issues và feature requests đều được chào đón!

Xem [CONTRIBUTING.md](CONTRIBUTING.md) để biết cách đóng góp.

## 📝 License

Dự án này được phân phối dưới [MIT License](LICENSE).

## 👤 Tác giả

**KhanhRomVN**

- GitHub: [@KhanhRomVN](https://github.com/KhanhRomVN)

## 🙏 Credits

- Mod gốc bởi [buthed010203](https://github.com/buthed010203)
- [Mindustry](https://github.com/Anuken/Mindustry) bởi Anuken

## ⭐ Support

Nếu mod này hữu ích, hãy cho một ⭐ trên GitHub!

---

<p align="center">Made with ❤️ for the Mindustry community</p>
