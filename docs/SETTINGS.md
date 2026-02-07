# Settings Guide

Hướng dẫn chi tiết về các settings có sẵn trong Multiplayer Pause Mod.

## 📍 Truy cập Settings

1. Mở Mindustry
2. Vào **Settings** (⚙️)
3. Tìm category **"Multiplayer Pause"** (icon pause)

---

## ⚙️ Available Settings

### 🔔 Toasts

**Tên setting**: `multiplayerpause-toasts`  
**Loại**: Checkbox  
**Mặc định**: ✅ Enabled

#### Mô tả
Hiển thị toast notification khi có người pause/unpause game.

#### Toast Format
```
<PlayerName> paused the game.
<PlayerName> unpaused the game.
```

#### Khi nào nên bật?
- ✅ Muốn biết ai đã pause/unpause
- ✅ Chơi với nhiều người
- ✅ Cần theo dõi hoạt động pause

#### Khi nào nên tắt?
- ❌ Toast gây phiền nhiễu
- ❌ Chơi solo với một người khác
- ❌ Đã biết ai sẽ pause

#### Screenshot
```
┌─────────────────────────────────┐
│ KhanhRomVN paused the game.     │
└─────────────────────────────────┘
```

---

### 🔓 Allow Any

**Tên setting**: `multiplayerpause-allowany`  
**Loại**: Checkbox  
**Mặc định**: ❌ Disabled

#### Mô tả
Cho phép **bất kỳ người chơi nào** pause game, không chỉ admin.

#### Quyền Pause

| Setting | Admin | Non-Admin |
|---------|-------|-----------|
| ❌ Disabled | ✅ Có thể pause | ❌ Không thể pause |
| ✅ Enabled | ✅ Có thể pause | ✅ Có thể pause |

#### Khi nào nên bật?
- ✅ Chơi với bạn bè tin tưởng
- ✅ Server private/co-op
- ✅ Muốn mọi người đều có quyền pause

#### Khi nào nên tắt?
- ❌ Server public
- ❌ Lo ngại bị abuse (spam pause)
- ❌ Chỉ muốn admin/host kiểm soát

> **⚠️ Cảnh báo**: Bật setting này trên public server có thể bị lạm dụng!

---

### 🔄 Sync on Pause

**Tên setting**: `multiplayerpause-synconpause`  
**Loại**: Checkbox  
**Mặc định**: ❌ Disabled

#### Mô tả
Tự động chạy lệnh `/sync` khi game được pause.

#### Cơ chế hoạt động
```
Game paused
    ↓
Auto execute: /sync
    ↓
Server syncs game state to all clients
```

#### Khi nào nên bật?
- ✅ Gặp desync thường xuyên
- ✅ Chơi với mods khác có thể gây desync
- ✅ Network không ổn định
- ✅ Muốn đảm bảo consistency khi pause

#### Khi nào nên tắt?
- ❌ Network tốt, ít desync
- ❌ Sync gây lag
- ❌ Pause/unpause thường xuyên (tránh spam sync)

#### Performance Impact
- **Bandwidth**: Tăng nhẹ (sync data)
- **CPU**: Minimal
- **Latency**: Có thể tăng 100-500ms khi sync

> **💡 Tip**: Chỉ bật nếu thực sự cần. Sync không cần thiết có thể gây lag.

---

### 🔄 Sync on Unpause

**Tên setting**: `multiplayerpause-synconunpause`  
**Loại**: Checkbox  
**Mặc định**: ❌ Disabled

#### Mô tả
Tự động chạy lệnh `/sync` khi game được unpause.

#### Cơ chế hoạt động
```
Game unpaused
    ↓
Auto execute: /sync
    ↓
Server syncs game state to all clients
```

#### Khi nào nên bật?
- ✅ Muốn đảm bảo mọi người đồng bộ trước khi tiếp tục
- ✅ Gặp desync sau khi unpause
- ✅ Chơi competitive (cần fairness)

#### Khi nào nên tắt?
- ❌ Muốn resume nhanh
- ❌ Sync gây delay khó chịu
- ❌ Network tốt

#### So sánh với Sync on Pause

| Timing | Sync on Pause | Sync on Unpause |
|--------|---------------|-----------------|
| **Khi nào sync** | Ngay khi pause | Ngay khi unpause |
| **Use case** | Đảm bảo state khi dừng | Đảm bảo state khi tiếp tục |
| **Ưu tiên** | Ít quan trọng hơn | Quan trọng hơn |

> **💡 Recommendation**: Nếu chỉ bật một, chọn **Sync on Unpause**.

---

### 📅 Schedule Sync

**Tên setting**: `multiplayerpause-schedulesync`  
**Loại**: Checkbox  
**Mặc định**: ❌ Disabled

#### Mô tả
Lên lịch sync nếu sync gần đây đã được thực hiện (trong vòng 5.1 giây).

#### Cơ chế hoạt động

```java
if (timeSinceLastSync > 5.1s) {
    // Sync ngay lập tức
    /sync
} else if (scheduleSyncEnabled) {
    // Lên lịch sync sau (5.1s - timeSinceLastSync)
    schedule(/sync, delay)
}
```

#### Ví dụ Timeline

```
T=0s:   Sync #1 (manual)
T=2s:   Pause (sync on pause enabled)
        → Sync cooldown: 3.1s còn lại
        → Schedule sync at T=5.1s
T=5.1s: Sync #2 (scheduled)
```

#### Khi nào nên bật?
- ✅ Đã bật Sync on Pause/Unpause
- ✅ Pause/unpause liên tục
- ✅ Muốn tránh spam sync
- ✅ Muốn sync eventually nhưng không ngay lập tức

#### Khi nào nên tắt?
- ❌ Không bật auto-sync
- ❌ Muốn sync ngay lập tức mọi lúc
- ❌ Không pause thường xuyên

#### Sync Cooldown

**Cooldown**: 5.1 giây (5100ms)

**Lý do**: Tránh spam `/sync` command, có thể gây:
- Server lag
- Network congestion
- Kick do spam detection

---

## 🎯 Recommended Configurations

### Configuration 1: Casual Co-op
**Use case**: Chơi với bạn bè, không competitive

```
✅ Toasts: Enabled
✅ Allow Any: Enabled
❌ Sync on Pause: Disabled
❌ Sync on Unpause: Disabled
❌ Schedule Sync: Disabled
```

**Lý do**: Mọi người đều có thể pause, không cần sync vì network tốt.

---

### Configuration 2: Competitive/PvP
**Use case**: Chơi competitive, cần fairness

```
✅ Toasts: Enabled
❌ Allow Any: Disabled (chỉ admin)
❌ Sync on Pause: Disabled
✅ Sync on Unpause: Enabled
✅ Schedule Sync: Enabled
```

**Lý do**: Chỉ admin pause, sync khi unpause để đảm bảo fairness.

---

### Configuration 3: High Desync Environment
**Use case**: Nhiều mods, network không ổn định

```
✅ Toasts: Enabled
❌ Allow Any: Disabled
✅ Sync on Pause: Enabled
✅ Sync on Unpause: Enabled
✅ Schedule Sync: Enabled
```

**Lý do**: Sync aggressive để chống desync.

---

### Configuration 4: Public Server
**Use case**: Server public, nhiều người lạ

```
✅ Toasts: Enabled
❌ Allow Any: Disabled
❌ Sync on Pause: Disabled
❌ Sync on Unpause: Disabled
❌ Schedule Sync: Disabled
```

**Lý do**: Chỉ admin pause, không auto-sync để tránh abuse.

---

### Configuration 5: Minimal Notifications
**Use case**: Không muốn bị làm phiền

```
❌ Toasts: Disabled
✅ Allow Any: Enabled
❌ Sync on Pause: Disabled
❌ Sync on Unpause: Disabled
❌ Schedule Sync: Disabled
```

**Lý do**: Tắt toasts, mọi người tự pause khi cần.

---

## 🔧 Advanced Tips

### Tip 1: Sync Cooldown Management

Nếu bạn cần sync thủ công:
```
/sync
```

Nếu gặp lỗi "sync too fast", đợi 5.1 giây.

### Tip 2: Testing Settings

Để test settings:
1. Bật setting
2. Pause/unpause vài lần
3. Quan sát behavior
4. Điều chỉnh nếu cần

### Tip 3: Server vs Client Settings

| Setting | Áp dụng cho |
|---------|-------------|
| Toasts | Client (mỗi người tự chọn) |
| Allow Any | **Server** (host quyết định) |
| Sync settings | Client (mỗi người tự chọn) |

> **⚠️ Quan trọng**: "Allow Any" chỉ có hiệu lực trên **server/host**!

---

## 🐛 Troubleshooting

### Toast không hiển thị
1. ✅ Check "Toasts" setting đã bật
2. ✅ Verify mod đã cài đúng
3. ✅ Restart game

### Không thể pause (non-admin)
1. ✅ Yêu cầu host bật "Allow Any"
2. ✅ Hoặc xin admin role

### Sync không hoạt động
1. ✅ Check setting đã bật
2. ✅ Verify không trong sync cooldown
3. ✅ Check console logs

### Lag khi sync
1. ❌ Tắt auto-sync
2. ✅ Chỉ sync thủ công khi cần
3. ✅ Upgrade network

---

## 📊 Settings Comparison Table

| Setting | Impact | Performance | Recommended |
|---------|--------|-------------|-------------|
| **Toasts** | Low | None | ✅ Yes |
| **Allow Any** | High | None | ⚠️ Depends |
| **Sync on Pause** | Medium | Low | ⚠️ If needed |
| **Sync on Unpause** | Medium | Low | ✅ If desync |
| **Schedule Sync** | Low | Very Low | ✅ With auto-sync |

---

## 📚 See Also

- [API Documentation](API.md) - Technical details
- [README](../README.md) - General information
- [Contributing](../CONTRIBUTING.md) - How to contribute
