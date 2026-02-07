# Changelog

Tất cả các thay đổi đáng chú ý của dự án sẽ được ghi lại trong file này.

Định dạng dựa trên [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
và dự án tuân theo [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [3.0.0] - 2026-02-06

### 🚀 Changed
- **BREAKING**: Nâng cấp lên Mindustry v154 (từ v136)
- Cập nhật tác giả thành KhanhRomVN
- Cải thiện mô tả mod với thông tin về tính năng đồng bộ

### 📚 Added
- Thêm README.md chuyên nghiệp với badges và documentation đầy đủ
- Thêm LICENSE (MIT)
- Thêm CHANGELOG.md (file này)
- Thêm CONTRIBUTING.md với hướng dẫn đóng góp
- Thêm .editorconfig cho coding standards
- Thêm thư mục docs/ với tài liệu kỹ thuật:
  - API.md - Documentation về packet handlers
  - SETTINGS.md - Hướng dẫn chi tiết về settings
- Cải thiện .gitignore với các patterns mới

### 🔧 Fixed
- Cập nhật dependencies trong build.gradle lên v155
- Đảm bảo tương thích với Mindustry API mới nhất

---

## [2.0.0] - Previous Release

### Added
- Tính năng pause multiplayer cơ bản
- Toast notifications
- Settings panel với các tùy chọn:
  - Toasts
  - Allow any player
  - Sync on pause/unpause
  - Schedule sync
- Packet handlers cho client-server communication

### Features
- Cho phép non-host players pause game
- Tự động đồng bộ state giữa clients
- Admin controls cho pause permissions

---

## Legend

- 🚀 Changed - Thay đổi trong tính năng hiện có
- 📚 Added - Tính năng mới
- 🔧 Fixed - Bug fixes
- 🗑️ Removed - Tính năng bị xóa
- ⚠️ Deprecated - Tính năng sẽ bị xóa trong tương lai
- 🔒 Security - Vá lỗi bảo mật
