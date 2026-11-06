package typingapp.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final Dotenv dotenv = Dotenv.load();
    private static final HikariConfig config = new HikariConfig();
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);
    private static final HikariDataSource ds;

    static {
        // --- 環境変数読込 ---
        String DB_URL = dotenv.get("DB_URL");
        String DB_USER = dotenv.get("DB_USER");
        String DB_PASSWORD = dotenv.get("DB_PASSWORD");
        
        System.out.println("DB_URL = " + DB_URL);
        System.out.println("DB_USER = " + DB_USER);
        
        if (DB_URL == null || DB_USER == null || DB_PASSWORD == null) {
            throw new IllegalStateException(".env ファイルに DB_URL / DB_USER / DB_PASSWORD が設定されていません。");
        }

        // --- HikariCP設定 ---
        config.setJdbcUrl(DB_URL);
        config.setUsername(DB_USER);
        config.setPassword(DB_PASSWORD);

        // 軽量アプリ向け最適設定
        config.setMaximumPoolSize(5);    	// 最大接続数（同時5接続まで）
        config.setMinimumIdle(1);        	// 最小アイドル接続
        config.setIdleTimeout(120000);    	// アイドル接続の最大待機時間（ms）
        config.setConnectionTimeout(10000); // 接続取得の最大待機時間（ms）
        config.setMaxLifetime(1800000);   	// 接続の最大寿命（30分）

        // MySQL向けの推奨設定（PreparedStatementキャッシュ）
        config.addDataSourceProperty("cachePrepStmts", "true");			// キャッシュを再利用する
        config.addDataSourceProperty("prepStmtCacheSize", "250");		// sql文の初期保存数
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");	// sql文の最長文字数 

        ds = new HikariDataSource(config);
    }

    // --- インスタンス化防止 ---(想定外の動きになるため、防止)
    private DatabaseConnection() {}

    // --- 接続取得 ---
    public static Connection getConnection() {
        try {
            return ds.getConnection();
        } catch (SQLException e) {
            logger.error("DB接続失敗", e);
            throw new IllegalStateException("データベースに接続できません。");
        }
    }
    
    // --- アプリ終了時に呼ぶ ---
    public static void shutdown() {
        if (ds != null && !ds.isClosed()) {
            ds.close();
            logger.info("データベース接続プールをシャットダウンしました");
        }
    }

    
}