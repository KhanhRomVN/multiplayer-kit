# Contributing to Multiplayer Pause Mod

Thank you for your interest in contributing to Multiplayer Pause Mod! 🎉

## 📋 Table of Contents

- [Code of Conduct](#code-of-conduct)
- [How to Contribute](#how-to-contribute)
- [Development Setup](#development-setup)
- [Coding Standards](#coding-standards)
- [Pull Request Process](#pull-request-process)
- [Bug Reports](#bug-reports)
- [Feature Requests](#feature-requests)

## Code of Conduct

This project adheres to principles of respect and professionalism. Please:
- Respect different opinions and viewpoints.
- Accept constructive feedback.
- Focus on what is best for the community.
- Show empathy towards other members.

## How to Contribute

### 1. Fork and Clone

```bash
# Fork repo on GitHub, then clone
git clone https://github.com/YOUR_USERNAME/multiplayer-pause.git
cd multiplayer-pause
```

### 2. Create a New Branch

```bash
git checkout -b feature/amazing-feature
# or
git checkout -b fix/bug-description
```

### 3. Make Changes

- Write clear, understandable code.
- Add comments where necessary.
- Follow [Coding Standards](#coding-standards).

### 4. Test Changes

```bash
# Build mod
./gradlew jar

# Test in Mindustry
# Copy build/libs/multiplayer-pause-pc-v2-v<version>.jar to mods folder
```

### 5. Commit

```bash
git add .
git commit -m "feat: add amazing feature"
```

**Commit Message Format:**
- `feat:` - New feature
- `fix:` - Bug fix
- `docs:` - Documentation changes
- `style:` - Code formatting (logic unchanged)
- `refactor:` - Code refactoring
- `test:` - Adding tests
- `chore:` - Maintenance tasks

### 6. Push and Create Pull Request

```bash
git push origin feature/amazing-feature
```

Then create a Pull Request on GitHub.

## Development Setup

### Requirements

- **Java**: JDK 8 or higher
- **Gradle**: Wrapper included (no need to install separately)
- **Mindustry**: v154+ for testing
- **IDE**: IntelliJ IDEA or Eclipse (recommended)

### Build Commands

```bash
# Build desktop version
./gradlew jar

# Build Android version (requires Android SDK)
./gradlew jarAndroid

# Build both
./gradlew deploy

# Clean build
./gradlew clean
```

### Testing

1. Build mod with `./gradlew jar`
2. Copy `build/libs/multiplayer-pause-pc-v2-v<version>.jar` to Mindustry mods folder.
3. Restart Mindustry.
4. Test in multiplayer mode.

## Coding Standards

### Java Style Guide

- **Indentation**: 4 spaces (no tabs)
- **Line length**: Maximum 120 characters
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

- Write comments for complex logic.
- Use JavaDoc for public methods.
- Explain **WHY**, not just **WHAT**.

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

1. **Update documentation** if needed.
2. **Update CHANGELOG.md** with your changes.
3. **Ensure builds successfully**: `./gradlew jar` has no errors.
4. **Test thoroughly** in multiplayer environment.
5. **Write clear PR description**:
   - Description of changes
   - Reason for changes
   - Screenshots/GIFs if UI changes
   - Test cases performed

### PR Template

```markdown
## Description
[Short description of changes]

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
[If UI changes]

## Checklist
- [ ] Code follows style guidelines
- [ ] Self-review completed
- [ ] Comments added for complex code
- [ ] Documentation updated
- [ ] CHANGELOG.md updated
- [ ] No new warnings
- [ ] Builds successfully
```

## Bug Reports

### Before Reporting

- Check [Issues](../../issues) to see if the bug has already been reported.
- Ensure you are using the latest version.
- Test with default mod configuration.

### Bug Report Template

```markdown
**Describe the bug**
[Clear and concise description of the bug]

**To Reproduce**
Steps to reproduce:
1. Go to '...'
2. Click on '...'
3. See error

**Expected behavior**
[Description of expected behavior]

**Screenshots**
[If available]

**Environment:**
 - Mindustry Version: [e.g. v155]
 - Mod Version: [e.g. 3.0]
 - OS: [e.g. Windows 10]
 - Multiplayer: [Host/Client]

**Additional context**
[More info about the bug]
```

## Feature Requests

### Feature Request Template

```markdown
**Is your feature request related to a problem?**
[Describe the problem, e.g. "I'm always frustrated when..."]

**Describe the solution you'd like**
[Clear description of desired feature]

**Describe alternatives you've considered**
[Alternative solutions you've considered]

**Additional context**
[Screenshots, mockups, or more info]
```

## Questions?

If you have questions, please:
- Open a [Discussion](../../discussions)
- Or create an [Issue](../../issues) with label `question`

---

**Thank you for contributing! 🚀**
