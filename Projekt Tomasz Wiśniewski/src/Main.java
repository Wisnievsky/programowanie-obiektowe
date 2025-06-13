import database.DatabaseManager;
import ui.TournamentGUI;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Uruchamiam inicjalizację bazy danych przed startem GUI. Chcę mieć pewność że wszystkie tabele są gotowe zanim aplikacja zacznie z nich korzystać.
        DatabaseManager.initDatabase();

        // Interfejs graficzny odpalam w specjalnym wątku dla Swinga.
        SwingUtilities.invokeLater(TournamentGUI::new);
    }
}