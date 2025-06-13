package dao;

import model.Gracz;
import database.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// klasa DAO odpowiedzialna tylko za operacje na tabeli 'gracz mecz'
public class GraczDAO {

    public static int insert(Gracz g) {
        String sql = "INSERT INTO gracz (nick, druzyna_id) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, g.getNick());
            ps.setInt(2, g.getDruzynaId());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    g.setId(rs.getInt(1));
                }
            }
            return g.getId();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return -1;
        }
    }

    public static List<Gracz> getByDruzynaId(int druzynaId) {
        List<Gracz> gracze = new ArrayList<>();
        String sql = "SELECT * FROM gracz WHERE druzyna_id=?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, druzynaId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                gracze.add(new Gracz(rs.getInt("id"), rs.getString("nick"), druzynaId));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return gracze;
    }

    public static void deleteAll() {
        try (Connection conn = DatabaseManager.connect();
             Statement st = conn.createStatement()) {
            st.executeUpdate("DELETE FROM gracz");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}