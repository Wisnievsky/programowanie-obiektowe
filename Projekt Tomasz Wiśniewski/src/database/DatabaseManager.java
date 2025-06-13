package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:turniej.db";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static boolean initDatabase() {
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            // W tej metodzie umieściłem wszystkie zapytania tworzące tabele. Używam 'CREATE TABLE IF NOT EXISTS' dzięki czemu aplikację można bezpiecznie uruchamiać wielokrotnie bez błędów.
            String sqlDruzyna = "CREATE TABLE IF NOT EXISTS druzyna (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nazwa TEXT NOT NULL, typ TEXT, " +
                    "sponsor TEXT, miasto TEXT, uczelnia TEXT, firma TEXT)";
            stmt.execute(sqlDruzyna);

            stmt.execute("CREATE TABLE IF NOT EXISTS gracz (id INTEGER PRIMARY KEY AUTOINCREMENT, nick TEXT, zmiennik BOOLEAN, druzyna_id INTEGER)");
            stmt.execute("CREATE TABLE IF NOT EXISTS mecz (id INTEGER PRIMARY KEY AUTOINCREMENT, druzyna1_id INTEGER, druzyna2_id INTEGER, mapa TEXT, wynik1 INTEGER, wynik2 INTEGER, zwyciezca_id INTEGER)");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}