package typingapp.controller;

import java.util.Objects;

import typingapp.service.LoginService;

public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        Objects.requireNonNull(loginService, "loginService must not be null");
        this.loginService = loginService;
    }

    public LoginService.LoginResult login(String userName, String password) {
        Objects.requireNonNull(userName, "userName must not be null");
        Objects.requireNonNull(password, "password must not be null");

        if (userName.isBlank()) {
            throw new IllegalArgumentException("ユーザー名は空にできません");
        }
        if (password.isBlank()) {
            throw new IllegalArgumentException("パスワードは空にできません");
        }

        try {
            return loginService.login(userName, password);
        } catch (Exception e) {
            System.err.println("ログイン処理エラー: " + e.getMessage());
            throw new RuntimeException("ログイン処理に失敗しました", e);
        }
    }

    public boolean register(String userName, String password) {
        Objects.requireNonNull(userName, "userName must not be null");
        Objects.requireNonNull(password, "password must not be null");

        if (userName.isBlank()) {
            throw new IllegalArgumentException("ユーザー名は空にできません");
        }
        if (password.isBlank()) {
            throw new IllegalArgumentException("パスワードは空にできません");
        }

        // パスワード強度チェック（推奨）
        if (password.length() < 6) {
            throw new IllegalArgumentException("パスワードは6文字以上である必要があります");
        }

        try {
            return loginService.saveUser(userName, password);
        } catch (Exception e) {
            System.err.println("ユーザー登録エラー: " + e.getMessage());
            throw new RuntimeException("ユーザー登録に失敗しました", e);
        }
    }
}