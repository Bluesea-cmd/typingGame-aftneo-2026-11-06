package typingapp.dao;

import typingapp.model.Score;
import typingapp.util.DatabaseConnection;

import java.sql.*;

public class ScoreDAO {

    //; スコアをデータベースに保存する
    	private static final String INSERT_SCORE_SQL = """
    		INSERT INTO scores
    		(player_name, level, score,
    		correctCount, nearCount, incorrectCount, timeoutCount,
    		correctCharCount, wrongCharCount, averageTypingSpeed, timeRemaining, workDate)
    		VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
       	""";
    	
        public boolean save(Score score) {
        // mysqlに接続して、データを保管する
            try (Connection conn = DatabaseConnection.getConnection();
        		PreparedStatement stmt =   conn.prepareStatement(INSERT_SCORE_SQL)) {
                stmt.setString(1, score.userName());  			// ユーザー名を保存
                stmt.setString(2, score.difficulty());			// 選択した難易度を保存
                stmt.setInt(3, score.finalScore());				//　最終スコアを保存
                stmt.setInt(4, score.correctCount());			//　正しく入力できた数を保存
                stmt.setInt(5, score.nearCount());				// 惜しい単語を保存
                stmt.setInt(6, score.incorrectCount());			// 不正解単語を保存
                stmt.setInt(7, score.timeoutCount());			// タイムアウト数を保存
                stmt.setInt(8, score.correctCharCount());
                stmt.setInt(9, score.wrongCharCount());
                stmt.setDouble(10, score.averageTypingSpeed());	// 平均キータイプ数を保存
                stmt.setInt(11, score.timeRemaining());
                stmt.setTimestamp(12, Timestamp.valueOf(score.currentDate()));

                int rowsAffected = stmt.executeUpdate();		// これがないと保存されない
                
                if (rowsAffected > 0) {
                    System.out.println("スコアがデータベースに保存されました。");
                    return true;
                }
                return false;
                
        } catch (SQLException e) {
            throw new RuntimeException("スコア保存失敗", e);
        }
    }
    
}
