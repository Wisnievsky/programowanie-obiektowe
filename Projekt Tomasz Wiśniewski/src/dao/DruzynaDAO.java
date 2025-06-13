package dao;

import model.*;
import database.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DruzynaDAO {

    public static int insert(Druzyna d) {
        String sql = "INSERT INTO druzyna (nazwa, typ, sponsor, miasto, uczelnia, firma) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, d.getNazwa());
            ps.setString(2, d.pobierzTyp());

            // Tutaj musiałem użyć instanceof żeby sprawdzić z jakim typem drużyny mam do czynienia i które kolumny w bazie wypełnić.
            if (d instanceof DruzynaProfesjonalna) {
                ps.setString(3, ((DruzynaProfesjonalna) d).getSponsor());
                ps.setNull(4, Types.VARCHAR);
                ps.setNull(5, Types.VARCHAR);
                ps.setNull(6, Types.VARCHAR);
            } else if (d instanceof DruzynaAmatorska) {
                ps.setNull(3, Types.VARCHAR);
                ps.setString(4, ((DruzynaAmatorska) d).getMiasto());
                ps.setNull(5, Types.VARCHAR);
                ps.setNull(6, Types.VARCHAR);
            } else if (d instanceof DruzynaUniwersytecka) {
                ps.setNull(3, Types.VARCHAR);
                ps.setNull(4, Types.VARCHAR);
                ps.setString(5, ((DruzynaUniwersytecka) d).getUczelnia());
                ps.setNull(6, Types.VARCHAR);
            } else if (d instanceof DruzynaFirmowa) {
                ps.setNull(3, Types.VARCHAR);
                ps.setNull(4, Types.VARCHAR);
                ps.setNull(5, Types.VARCHAR);
                ps.setString(6, ((DruzynaFirmowa) d).getFirma());
            }

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) d.setId(rs.getInt(1));
            }
            return d.getId();
        } catch (SQLException ex) {
            System.err.println("Błąd przy wstawianiu drużyny: " + ex.getMessage());
            return -1;
        }
    }

    public static List<Druzyna> getAll() {
        List<Druzyna> listaDruzyn = new ArrayList<>();
        String sql = "SELECT id, nazwa, typ, sponsor, miasto, uczelnia, firma FROM druzyna";

        try (Connection connection = DatabaseManager.connect();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next())
            {
                String typ = rs.getString("typ");
                int id = rs.getInt("id");
                String nazwa = rs.getString("nazwa");

                if ("Profesjonalna".equals(typ)) {
                    listaDruzyn.add(new DruzynaProfesjonalna(id, nazwa, rs.getString("sponsor")));
                } else if ("Amatorska".equals(typ)) {
                    listaDruzyn.add(new DruzynaAmatorska(id, nazwa, rs.getString("miasto")));
                } else if ("Uniwersytecka".equals(typ)) {
                    listaDruzyn.add(new DruzynaUniwersytecka(id, nazwa, rs.getString("uczelnia")));
                } else if ("Firmowa".equals(typ)) {
                    listaDruzyn.add(new DruzynaFirmowa(id, nazwa, rs.getString("firma")));
                }
            }
        } catch (SQLException ex) {
            System.err.println("Błąd przy odczycie drużyn: " + ex.getMessage());
        }
        return listaDruzyn;
    }

    public static void delete(int teamId) {
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM druzyna WHERE id=?")) {
            ps.setInt(1, teamId);
            ps.executeUpdate();
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    public static void deleteAll() {
        try (var conn = DatabaseManager.connect();
             var st = conn.createStatement()) {
            st.executeUpdate("DELETE FROM druzyna");
        } catch (SQLException ex) { ex.printStackTrace(); }
    }
}