package typingapp.service;

import typingapp.dao.UserDAO;
import typingapp.model.User;

import org.mindrot.jbcrypt.BCrypt;

import java.util.Objects;
import java.util.Optional;

import java.util.logging.Logger;

public class LoginService {

    private final UserDAO userDAO;
    private static final Logger logger = Logger.getLogger(LoginService.class.getName());
    
    public enum LoginResult {
        SUCCESS,
        USER_NOT_FOUND,
        WRONG_PASSWORD,
        INVALID_INPUT,
        SYSTEM_ERROR
    }
    
    public LoginService(UserDAO userDAO) {
        Objects.requireNonNull(userDAO, "userDAO must not be null");
        this.userDAO = userDAO;
    }
    
    public LoginResult login(String username, String password) {
        Objects.requireNonNull(username, "username must not be null");
        Objects.requireNonNull(password, "password must not be null");
        
        
        if (username.isBlank() || password.isBlank()) {
            return LoginResult.INVALID_INPUT;
        }
        
        try {
	        Optional<User> userOpt = userDAO.findByUsername(username);
	
	        // ユーザー不存在
	        if (userOpt.isEmpty()) {
	        	System.out.println("ユーザーが見つかりません: " + username);
	            return LoginResult.USER_NOT_FOUND;
	        }
	
	        User user = userOpt.get();
	        
	        // ハッシュ比較
	        // パスワード確認
	        if (!BCrypt.checkpw(password, user.password())) {
	        	System.out.println("パスワードが間違っています: " + username);
	            return LoginResult.WRONG_PASSWORD;
	        }
	        
            System.out.println("ログイン成功: " + username);
	        return LoginResult.SUCCESS;
	        
        } catch (Exception e) {
        	logger.severe("LOGIN SYSTEM ERROR");
        	e.printStackTrace();
        	return LoginResult.SYSTEM_ERROR;
        }
    }
    
    public boolean saveUser(String username, String rawPassword) {
        Objects.requireNonNull(username, "username must not be null");
        Objects.requireNonNull(rawPassword, "rawPassword must not be null");
        
        username = username.trim();
        
        // ユーザー名の長さチェック
        if (username.length() < 4 || username.length() > 20) {
            throw new IllegalArgumentException("ユーザー名は4〜20文字である必要があります");
        }

        // パスワード強度チェック
        if (rawPassword.length() < 6) {
            throw new IllegalArgumentException("パスワードは6文字以上である必要があります");
        }

        try {
            // 重複チェック
            if (userDAO.existsByUsername(username)) {
                System.out.println("ユーザー名は既に使用されています: " + username);
                return false;
            }

            // パスワードをハッシュ化
            String hashed = BCrypt.hashpw(rawPassword, BCrypt.gensalt());

            // ユーザー登録
            boolean success = userDAO.register(username, hashed);

            // ★重要修正：DB結果を必ず確認
            if (!success) {
                System.out.println("DB登録失敗: " + username);
                return false;
            }

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
