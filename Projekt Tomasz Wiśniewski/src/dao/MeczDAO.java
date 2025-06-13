package dao;

import model.Mecz;
import database.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// klasa DAO odpowiedzialna tylko za operacje na tabeli 'gracz mecz'
public class MeczDAO {

    public static int insert(Mecz m) {
        String sql = "INSERT INTO mecz (druzyna1_id, druzyna2_id, mapa, wynik1, wynik2, zwyciezca_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, m.getDruzyna1Id());
            ps.setInt(2, m.getDruzyna2Id());
            ps.setString(3, m.getMapa());
            ps.setInt(4, m.getWynik1());
            ps.setInt(5, m.getWynik2());
            ps.setInt(6, m.getZwyciezcaId());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return -1;
    }

    public static List<Mecz> getAll() {
        List<Mecz> mecze = new ArrayList<>();
        String sql = "SELECT * FROM mecz ORDER BY id ASC";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                mecze.add(new Mecz(
                        rs.getInt("druzyna1_id"),
                        rs.getInt("druzyna2_id"),
                        rs.getString("mapa"),
                        rs.getInt("wynik1"),
                        rs.getInt("wynik2"),
                        rs.getInt("zwyciezca_id")
                ));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return mecze;
    }

    public static void deleteAll() {
        try (Connection conn = DatabaseManager.connect();
             Statement st = conn.createStatement()) {
            st.executeUpdate("DELETE FROM mecz");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}