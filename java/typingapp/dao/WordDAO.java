package typingapp.dao;

import typingapp.model.TypingWord;
import typingapp.util.DatabaseConnection;

import java.sql.*;
import java.util.*;

import java.util.logging.Logger;

public class WordDAO {
    private static final Logger logger = Logger.getLogger(WordDAO.class.getName());

	// word hiragana romaji_moraをtyping_wordsから
	// 難易度に応じてランダムな文字を1つ取得するための変数
	private static final String FIND_SQL = """
		    SELECT word, hiragana, romaji_mora
		    FROM typing_words
		    WHERE level = ?
		""";
    
	private static final String FIND_BY_ID_SQL =
		    "SELECT * FROM typing_words WHERE id = ?";
	
	private static final int TEST_WORD_ID = 23;
	
	public TypingWord findTestWord() {
	    return findById(TEST_WORD_ID);
	}
	
	public TypingWord findById(int wordId) {

	    try (Connection conn = DatabaseConnection.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(FIND_BY_ID_SQL)) {

	        stmt.setInt(1, wordId);

	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) {
	                return parseTypingWord(rs);
	            }
	        }

	    } catch (SQLException e) {
	        throw new RuntimeException(e);
	    }

	    return null;
	}
	
    // 件数取得
	public List<TypingWord> findByDifficulty(String difficulty) {
	    
		List<TypingWord> result = new ArrayList<>();
    	
    	// DatabeseConnectionによってmysqlからデータを習得する
        try (Connection conn = DatabaseConnection.getConnection();
        		PreparedStatement stmt = conn.prepareStatement(FIND_SQL)) {
        		
        		//	ランダムに取得する
        		stmt.setString(1, difficulty);
    		
        		//	クリエイターという単語の選択
				//	stmt.setInt(1, 16);
					
				//	Discordという単語の選択
				//	stmt.setInt(1, 41);
					
				//	LINEスタンプという単語の選択
				//	stmt.setInt(1, 65);
               
               
               try (ResultSet rs = stmt.executeQuery()) {
                   while (rs.next()) {
                       TypingWord word = parseTypingWord(rs);
                       if (word != null) {
                           result.add(word);
                       }
                   }
               }

               logger.info("難易度'" + difficulty + "'から" + result.size() + "件の単語を取得");


           } catch (SQLException e) {
               logger.severe("単語取得に失敗しました: " + e.getMessage());
               throw new RuntimeException("単語取得に失敗しました", e);
           }

           return result;
       }
    
    private TypingWord parseTypingWord(ResultSet rs) throws SQLException {
        String word = rs.getString("word");
        String hiragana = rs.getString("hiragana");
        String moraString = rs.getString("romaji_mora");

        // Nullチェック
        if (word == null || hiragana == null || moraString == null) {
            logger.warning("null値が含まれています: word=" + word 
                    + ", hiragana=" + hiragana + ", moraString=" + moraString);
            return null;
        }

        // 空文字列チェック
        if (word.trim().isEmpty() || hiragana.trim().isEmpty() || moraString.trim().isEmpty()) {
            logger.warning("空文字列が含まれています: word='" + word 
                    + "', hiragana='" + hiragana + "', moraString='" + moraString + "'");
            return null;
        }

        // モーラをリストに分割（カンマ区切り）
        List<String> moraList = Arrays.asList(moraString.split(","));

        return new TypingWord(word, hiragana, moraList);
    }
}
