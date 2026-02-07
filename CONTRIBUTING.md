# Contributing to Multiplayer Pause Mod

Cảm ơn bạn đã quan tâm đến việc đóng góp cho Multiplayer Pause Mod! 🎉

## 📋 Mục lục

- [Code of Conduct](#code-of-conduct)
- [Làm thế nào để đóng góp](#làm-thế-nào-để-đóng-góp)
- [Development Setup](#development-setup)
- [Coding Standards](#coding-standards)
- [Pull Request Process](#pull-request-process)
- [Báo cáo Bug](#báo-cáo-bug)
- [Đề xuất tính năng](#đề-xuất-tính-năng)

## Code of Conduct

Dự án này tuân theo nguyên tắc tôn trọng và chuyên nghiệp. Vui lòng:
- Tôn trọng ý kiến và quan điểm khác nhau
- Chấp nhận phản hồi mang tính xây dựng
- Tập trung vào điều tốt nhất cho cộng đồng
- Thể hiện sự đồng cảm với các thành viên khác

## Làm thế nào để đóng góp

### 1. Fork và Clone

```bash
# Fork repo trên GitHub, sau đó clone
git clone https://github.com/YOUR_USERNAME/multiplayer-pause.git
cd multiplayer-pause
```

### 2. Tạo Branch mới

```bash
git checkout -b feature/amazing-feature
# hoặc
git checkout -b fix/bug-description
```

### 3. Thực hiện thay đổi

- Viết code rõ ràng, dễ hiểu
- Thêm comments khi cần thiết
- Tuân theo [Coding Standards](#coding-standards)

### 4. Test thay đổi

```bash
# Build mod
./gradlew jar

# Test trong Mindustry
# Copy build/libs/pause-modDesktop.jar vào thư mục mods
```

### 5. Commit

```bash
git add .
git commit -m "feat: add amazing feature"
```

**Commit Message Format:**
- `feat:` - Tính năng mới
- `fix:` - Bug fix
- `docs:` - Thay đổi documentation
- `style:` - Code formatting (không ảnh hưởng logic)
- `refactor:` - Code refactoring
- `test:` - Thêm tests
- `chore:` - Maintenance tasks

### 6. Push và tạo Pull Request

```bash
git push origin feature/amazing-feature
```

Sau đó tạo Pull Request trên GitHub.

## Development Setup

### Requirements

- **Java**: JDK 8 hoặc cao hơn
- **Gradle**: Wrapper included (không cần cài riêng)
- **Mindustry**: v154+ để test
- **IDE**: IntelliJ IDEA hoặc Eclipse (khuyến nghị)

### Build Commands

```bash
# Build desktop version
./gradlew jar

# Build Android version (cần Android SDK)
./gradlew jarAndroid

# Build cả hai
./gradlew deploy

# Clean build
./gradlew clean
```

### Testing

1. Build mod với `./gradlew jar`
2. Copy `build/libs/pause-modDesktop.jar` vào thư mục mods của Mindustry
3. Restart Mindustry
4. Test trong multiplayer mode

## Coding Standards

### Java Style Guide

- **Indentation**: 4 spaces (không dùng tabs)
- **Line length**: Tối đa 120 characters
- **Naming conventions**:
  - Classes: `PascalCase`
  - Methods/Variables: `camelCase`
  - Constants: `UPPER_SNAKE_CASE`
  - Packages: `lowercase`

### Code Quality

```java
// ✅ Good
public void showToast(Player player, boolean paused) {
    if (!Core.settings.getBool("multiplayerpause-toasts")) return;
    
    String message = Strings.format("@ @ the game.", 
        player == null ? "[lightgray]Unknown player[]" : Strings.stripColors(player.name), 
        paused ? "paused" : "unpaused"
    );
    Menus.infoToast(message, 2f);
}

// ❌ Bad
public void showToast(Player p,boolean paused){
if(!Core.settings.getBool("multiplayerpause-toasts"))return;
Menus.infoToast(Strings.format("@ @ the game.",p==null?"[lightgray]Unknown player[]":Strings.stripColors(p.name),paused?"paused":"unpaused"),2f);
}
```

### Comments

- Viết comments cho logic phức tạp
- Sử dụng JavaDoc cho public methods
- Giải thích **WHY**, không chỉ **WHAT**

```java
/**
 * Sends pause state update to all clients.
 * This is necessary to avoid waiting for the next stateSnapshot,
 * which reduces desync and allows for immediate toast notifications.
 */
void broadcastPauseState(Player player, boolean paused) {
    // Implementation
}
```

## Pull Request Process

1. **Update documentation** nếu cần
2. **Update CHANGELOG.md** với thay đổi của bạn
3. **Ensure builds successfully**: `./gradlew jar` không có lỗi
4. **Test thoroughly** trong multiplayer environment
5. **Write clear PR description**:
   - Mô tả thay đổi
   - Lý do thay đổi
   - Screenshots/GIFs nếu có UI changes
   - Test cases đã thực hiện

### PR Template

```markdown
## Description
[Mô tả ngắn gọn về thay đổi]

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Testing
- [ ] Tested in singleplayer
- [ ] Tested in multiplayer (as host)
- [ ] Tested in multiplayer (as client)
- [ ] Tested with different settings combinations

## Screenshots
[Nếu có UI changes]

## Checklist
- [ ] Code follows style guidelines
- [ ] Self-review completed
- [ ] Comments added for complex code
- [ ] Documentation updated
- [ ] CHANGELOG.md updated
- [ ] No new warnings
- [ ] Builds successfully
```

## Báo cáo Bug

### Trước khi báo cáo

- Kiểm tra [Issues](../../issues) xem bug đã được báo cáo chưa
- Đảm bảo bạn đang dùng phiên bản mới nhất
- Test với mod configuration mặc định

### Bug Report Template

```markdown
**Describe the bug**
[Mô tả rõ ràng và ngắn gọn về bug]

**To Reproduce**
Steps to reproduce:
1. Go to '...'
2. Click on '...'
3. See error

**Expected behavior**
[Mô tả hành vi mong đợi]

**Screenshots**
[Nếu có]

**Environment:**
 - Mindustry Version: [e.g. v155]
 - Mod Version: [e.g. 3.0]
 - OS: [e.g. Windows 10]
 - Multiplayer: [Host/Client]

**Additional context**
[Thông tin thêm về bug]
```

## Đề xuất tính năng

### Feature Request Template

```markdown
**Is your feature request related to a problem?**
[Mô tả vấn đề, e.g. "I'm always frustrated when..."]

**Describe the solution you'd like**
[Mô tả rõ ràng về tính năng mong muốn]

**Describe alternatives you've considered**
[Các giải pháp thay thế bạn đã xem xét]

**Additional context**
[Screenshots, mockups, hoặc thông tin thêm]
```

## Questions?

Nếu có câu hỏi, vui lòng:
- Mở [Discussion](../../discussions)
- Hoặc tạo [Issue](../../issues) với label `question`

---

**Cảm ơn bạn đã đóng góp! 🚀**
