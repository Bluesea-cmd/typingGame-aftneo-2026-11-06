package typingapp.model;

public record User(
        String username,
        String password
) {
    public User {
        if (username == null || username.isBlank()) { throw new IllegalArgumentException("ユーザー名は空にできません"); }
        if (password == null || password.isBlank()) { throw new IllegalArgumentException("パスワードハッシュは空にできません"); }
    }
}
