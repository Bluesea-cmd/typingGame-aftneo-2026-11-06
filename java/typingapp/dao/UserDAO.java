package typingapp.dao;

import typingapp.model.User;
import typingapp.util.DatabaseConnection;

import java.sql.*;
import java.util.Optional;

public class UserDAO {
	
    // mysqlに登録しているユーザー情報の照合し、合っていればログインできるようにする
    public Optional<User> findByUsername(String username) {

        String sql = """
			SELECT *
			FROM typing_app.userlogin
			WHERE userName = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

               ps.setString(1, username);

               try (ResultSet rs = ps.executeQuery()) {
	               if (rs.next()) {
	                   return Optional.of(
	                       new User(
	                           rs.getString("userName"),
	                           rs.getString("password") // ← ハッシュ
	                       )
	                   );
	               }
               }
           } catch (SQLException e) {
            throw new RuntimeException("ログイン認証に失敗しました", e);
        }

        return Optional.empty();
    }
    
    // ユーザー名が存在するか確認する
    public boolean existsByUsername(String username) {

        String sql = "SELECT 1 FROM typing_app.userlogin WHERE userName=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new RuntimeException("ユーザー検索に失敗しました", e);
        }
    }
    
    public boolean register(String username, String hashedPassword) {

    	// 重複チェック
        if (existsByUsername(username)) {
            throw new RuntimeException("ユーザー名は既に使用されています: " + username);
        }
        
        String sql = """
            INSERT INTO typing_app.userlogin
            (userName, password, createdDate)
            VALUES (?, ?, NOW())
        """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

               ps.setString(1, username);
               ps.setString(2, hashedPassword);

               int rowsAffected = ps.executeUpdate();
               
               if (rowsAffected > 0) {
                   System.out.println("ユーザー登録成功: " + username);
                   return true;
               }
               return false;

           } catch (SQLException e) {
               throw new RuntimeException("ユーザー登録失敗", e);
           }
       }
}
