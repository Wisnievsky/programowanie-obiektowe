package ui;

import core.TournamentManager;
import dao.DruzynaDAO;
import dao.GraczDAO;
import dao.MeczDAO;
import model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TournamentGUI extends JFrame {
    private DefaultListModel<Druzyna> druzynaListModel = new DefaultListModel<>();
    private JList<Druzyna> druzynaJList = new JList<>(druzynaListModel);
    private JTextArea graczeArea = new JTextArea();
    private JTextArea drabinkaArea = new JTextArea();
    private TournamentManager manager;
    private JButton btnNastepnyEtap;
    private final String baseTitle = "Turniej Valorant - GUI";

    public TournamentGUI() {
        super("Turniej Valorant - GUI");
        this.setLayout(new BorderLayout(15, 15));

        JPanel lewyPanel = new JPanel(new BorderLayout(5, 5));
        druzynaJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        druzynaJList.addListSelectionListener(e -> pokazGraczy());

        JScrollPane druzScroll = new JScrollPane(druzynaJList);
        druzScroll.setBorder(BorderFactory.createTitledBorder("Drużyny"));

        graczeArea.setEditable(false);
        JScrollPane graczScroll = new JScrollPane(graczeArea);
        graczScroll.setBorder(BorderFactory.createTitledBorder("Gracze drużyny"));

        lewyPanel.add(druzScroll, BorderLayout.NORTH);
        lewyPanel.add(graczScroll, BorderLayout.CENTER);
        lewyPanel.setPreferredSize(new Dimension(200, 400));

        JPanel prawyPanel = new JPanel();
        prawyPanel.setLayout(new GridLayout(0, 1, 5, 5));

        String[] przyciski = {
                "Dodaj drużynę", "Dodaj gracza", "Usuń gracza", "Usuń drużynę",
                "Rozpocznij turniej", "Dodaj wynik meczu", "Historia meczów", "Pokaż wyniki", "Resetuj turniej"
        };

        for (String name : przyciski) {
            JButton btn = new JButton(name);
            prawyPanel.add(btn);
            btn.addActionListener(this::obsluzPrzyciski);
        }

        btnNastepnyEtap = new JButton("Rozpocznij następny etap");
        btnNastepnyEtap.setVisible(false);
        btnNastepnyEtap.addActionListener(e -> rozpocznijNastepnyEtap());
        prawyPanel.add(btnNastepnyEtap);

        JPanel rightWrapper = new JPanel(new BorderLayout());
        rightWrapper.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        rightWrapper.add(prawyPanel, BorderLayout.CENTER);
        rightWrapper.setPreferredSize(new Dimension(180, 400));

        drabinkaArea.setEditable(false);
        drabinkaArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane centerScroll = new JScrollPane(drabinkaArea);
        centerScroll.setBorder(BorderFactory.createTitledBorder("Drabinka turnieju"));

        add(lewyPanel, BorderLayout.WEST);
        add(centerScroll, BorderLayout.CENTER);
        add(rightWrapper, BorderLayout.EAST);

        odswiezDruzyny();
        aktualizujTytulOkna();

        setSize(1050, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void pokazGraczy() {
        Druzyna d = druzynaJList.getSelectedValue();
        if (d == null) return;
        var gracze = GraczDAO.getByDruzynaId(d.getId());
        StringBuilder sb = new StringBuilder();
        for (Gracz g : gracze) {
            sb.append(" - ").append(g.getNick()).append("\n");
        }
        graczeArea.setText(sb.toString());
    }

    private void obsluzPrzyciski(ActionEvent e) {
        String cmd = ((JButton) e.getSource()).getText();
        switch (cmd) {
            case "Dodaj drużynę": {

                JTextField nazwaField = new JTextField();
                JTextField fieldDodatkowy = new JTextField();
                JLabel labelDodatkowy = new JLabel("Sponsor:");
                String[] typy = {"Profesjonalna", "Amatorska", "Uniwersytecka", "Firmowa"};
                JComboBox<String> typComboBox = new JComboBox<>(typy);

                typComboBox.addActionListener(ev -> {
                    String wybranyTyp = (String) typComboBox.getSelectedItem();
                    if (wybranyTyp == null) return;
                    switch (wybranyTyp) {
                        case "Profesjonalna": labelDodatkowy.setText("Sponsor:"); break;
                        case "Amatorska": labelDodatkowy.setText("Miasto:"); break;
                        case "Uniwersytecka": labelDodatkowy.setText("Uczelnia:"); break;
                        case "Firmowa": labelDodatkowy.setText("Firma:"); break;
                    }
                });

                JPanel panel = new JPanel(new GridLayout(0, 1));
                panel.add(new JLabel("Nazwa drużyny:"));
                panel.add(nazwaField);
                panel.add(new JLabel("Typ drużyny:"));
                panel.add(typComboBox);
                panel.add(labelDodatkowy);
                panel.add(fieldDodatkowy);

                int res = JOptionPane.showConfirmDialog(this, panel, "Dodaj nową drużynę",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (res == JOptionPane.OK_OPTION) {
                    String nazwa = nazwaField.getText();
                    String dodatkowe = fieldDodatkowy.getText();
                    String typ = (String) typComboBox.getSelectedItem();

                    if (nazwa != null && !nazwa.isBlank()) {
                        Druzyna team = null;
                        switch (typ) {
                            case "Profesjonalna":
                                team = new DruzynaProfesjonalna(0, nazwa, dodatkowe); break;
                            case "Amatorska":
                                team = new DruzynaAmatorska(0, nazwa, dodatkowe); break;
                            case "Uniwersytecka":
                                team = new DruzynaUniwersytecka(0, nazwa, dodatkowe); break;
                            case "Firmowa":
                                team = new DruzynaFirmowa(0, nazwa, dodatkowe); break;
                        }
                        if (team != null) {
                            DruzynaDAO.insert(team);
                            odswiezDruzyny();
                        }
                    }
                }
                break;
            }
            case "Dodaj gracza": {
                Druzyna d = druzynaJList.getSelectedValue();
                if (d == null) {
                    JOptionPane.showMessageDialog(this, "Najpierw wybierz drużynę z listy.", "Błąd", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (GraczDAO.getByDruzynaId(d.getId()).size() >= 6) {
                    JOptionPane.showMessageDialog(this, "Drużyna ma już 6 graczy.");
                    return;
                }
                String nick = JOptionPane.showInputDialog(this, "Nick gracza:");
                if (nick != null && !nick.isBlank()) {
                    GraczDAO.insert(new Gracz(0, nick, d.getId()));
                    pokazGraczy();
                }
                break;
            }
            case "Usuń gracza": {
                Druzyna d = druzynaJList.getSelectedValue();
                if (d == null) return;
                String nick = JOptionPane.showInputDialog(this, "Nick gracza do usunięcia:");
                if (nick != null) {
                    GraczDAO.getByDruzynaId(d.getId()).stream()
                            .filter(g -> g.getNick().equalsIgnoreCase(nick)).findFirst().ifPresent(g -> {
                                try (var conn = database.DatabaseManager.connect();
                                     var st = conn.prepareStatement("DELETE FROM gracz WHERE id = ?")) {
                                    st.setInt(1, g.getId());
                                    st.executeUpdate();
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            });
                    pokazGraczy();
                }
                break;
            }
            case "Usuń drużynę": {
                Druzyna d = druzynaJList.getSelectedValue();
                if (d != null) {
                    DruzynaDAO.delete(d.getId());
                    odswiezDruzyny();
                }
                break;
            }
            case "Rozpocznij turniej": {
                List<Druzyna> lista = new ArrayList<>();
                for (int i = 0; i < druzynaListModel.size(); i++) {
                    lista.add(druzynaListModel.get(i));
                }
                if (lista.size() < 3) {
                    JOptionPane.showMessageDialog(this, "Do rozpoczęcia turnieju potrzebne są co najmniej 3 drużyny!", "Za mało drużyn", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                manager = new TournamentManager(lista);
                pokazEtap();
                break;
            }
            case "Dodaj wynik meczu": {
                if (manager == null) return;
                Mecz m = manager.pobierzKolejnyMecz();
                if (m == null) {
                    if (manager.etapZakonczony() == true) {
                        drabinkaArea.append("\n\n✅ Etap \"" + manager.getNazwaEtapu() + "\" został zakończony.\nKliknij 'Rozpocznij następny etap', aby kontynuować.");
                        btnNastepnyEtap.setVisible(true);
                    }
                    return;
                }
                String nazwa1 = znajdzNazweDruzyny(m.getDruzyna1Id());
                String nazwa2 = znajdzNazweDruzyny(m.getDruzyna2Id());
                int w1 = pobierzLiczbe("Wynik dla drużyny '" + nazwa1 + "':");
                if (w1 == -1) return;
                int w2 = pobierzLiczbe("Wynik dla drużyny '" + nazwa2 + "':");
                if (w2 == -1) return;

                if (w1 == w2) {
                    JOptionPane.showMessageDialog(this, "Wynik nie może być remisowy!", "Błąd", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int zwyc = (w1 > w2) ? m.getDruzyna1Id() : m.getDruzyna2Id();
                Mecz pelny = new Mecz(m.getDruzyna1Id(), m.getDruzyna2Id(), m.getMapa(), w1, w2, zwyc);
                MeczDAO.insert(pelny);
                manager.zapiszWynik(pelny);

                drabinkaArea.append("\n" + nazwa1 + " vs " + nazwa2 + " | Mapa: " + m.getMapa() + " | Wynik: " + w1 + "-" + w2 + " | Wygrała: " + (w1 > w2 ? nazwa1 : nazwa2));
                break;
            }
            case "Historia meczów": {
                var lista = MeczDAO.getAll();
                StringBuilder sb = new StringBuilder("Historia wszystkich zapisanych meczów:\n\n");
                for (Mecz m : lista) {
                    String nazwa1 = znajdzNazweDruzyny(m.getDruzyna1Id());
                    String nazwa2 = znajdzNazweDruzyny(m.getDruzyna2Id());
                    String zwyciezca = znajdzNazweDruzyny(m.getZwyciezcaId());
                    sb.append(nazwa1).append(" vs ").append(nazwa2)
                            .append(" | Mapa: ").append(m.getMapa())
                            .append(" | Wynik: ").append(m.getWynik1()).append("-").append(m.getWynik2())
                            .append(" | Wygrała: ").append(zwyciezca).append("\n");
                }
                JTextArea area = new JTextArea(sb.toString());
                area.setEditable(false);
                JOptionPane.showMessageDialog(this, new JScrollPane(area), "Historia Meczów", JOptionPane.INFORMATION_MESSAGE);
                break;
            }
            case "Pokaż wyniki": {
                if (manager == null || !"Zakończony".equals(manager.getNazwaEtapu())) {
                    JOptionPane.showMessageDialog(this, "Turniej się jeszcze nie zakończył!", "Informacja", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                Map<String, String> wynik = manager.podsumujWyniki();
                StringBuilder sb = new StringBuilder("🏆 WYNIKI KOŃCOWE TURNIEJU 🏆\n\n");
                wynik.forEach((k, v) -> sb.append(k).append(" — ").append(v).append("\n"));
                JOptionPane.showMessageDialog(this, sb.toString(), "Wyniki Końcowe", JOptionPane.INFORMATION_MESSAGE);
                break;
            }
            case "Resetuj turniej": {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Czy na pewno chcesz usunąć WSZYSTKIE drużyny, graczy i mecze z bazy danych?\nTej operacji nie można cofnąć!", "Potwierdzenie resetu",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    MeczDAO.deleteAll();
                    GraczDAO.deleteAll();
                    DruzynaDAO.deleteAll();
                    odswiezDruzyny();
                    graczeArea.setText("");
                    drabinkaArea.setText("Turniej został zresetowany.\nMożna dodawać nowe drużyny.");
                    manager = null;
                    aktualizujTytulOkna();
                }
                break;
            }
        }
    }

    private void aktualizujTytulOkna() {
        if (manager != null) {
            String nazwaEtapu = manager.getNazwaEtapu();
            setTitle(baseTitle + " | Etap: " + nazwaEtapu);
        } else {
            setTitle(baseTitle + " | Oczekiwanie na start");
        }
    }

    private void rozpocznijNastepnyEtap() {
        if (manager != null) {
            manager.rozpocznijNastepnyEtap();
            pokazEtap();
            btnNastepnyEtap.setVisible(false);
        }
    }

    private void pokazEtap() {
        aktualizujTytulOkna();
        StringBuilder sb = new StringBuilder();

        if (manager == null) { return; }

        // ta metoda zbiera potrzebne informacje z TournamentManagera – jakie są pary kto ma wolny los.
        String nazwaEtapu = manager.getNazwaEtapu();
        if ("Zakończony".equals(nazwaEtapu)) {
            drabinkaArea.setText("🎉 Turniej został zakończony! 🎉\n\nKliknij 'Pokaż wyniki', aby zobaczyć ostateczną klasyfikację.");
            return;
        }

        sb.append("🟢 ROZPOCZYNAMY ETAP: ").append(nazwaEtapu).append("\n");
        sb.append("Wprowadź wyniki dla każdej pary, klikając 'Dodaj wynik meczu'.\n\n");
        sb.append("⚔️ Pary meczowe:\n");

        List<Mecz> pary = manager.podgladAktualnegoEtapu();
        if (pary.isEmpty()) {
            sb.append("Brak zaplanowanych meczy w tym etapie.\n");
        } else {
            for (Mecz m : pary) {
                String nazwa1 = znajdzNazweDruzyny(m.getDruzyna1Id());
                String nazwa2 = znajdzNazweDruzyny(m.getDruzyna2Id());
                sb.append(" - ").append(nazwa1).append(" vs ").append(nazwa2)
                        .append(" | Mapa: ").append(m.getMapa()).append("\n");
            }
        }

        List<Druzyna> wolnyLos = manager.getWolnyLos();
        if (!wolnyLos.isEmpty()) {
            sb.append("\n");
            for(Druzyna d : wolnyLos) {
                sb.append("➡️ Drużyna '").append(d.getNazwa()).append("' przechodzi dalej (wolny los).\n");
            }
        }

        sb.append("\n\n🌍 Dostępne mapy w puli:\n");
        for (String mapa : manager.getDostepneMapy()) {
            sb.append("- ").append(mapa).append("\n");
        }

        drabinkaArea.setText(sb.toString());
    }

    private void odswiezDruzyny() {
        // metoda czyści listę drużyn w GUI i wczytuje ją na nowo z bazy danych. Wywołuję ją po każdej zmianie (dodaniu usunięciu).
        druzynaListModel.clear();
        List<Druzyna> wszystkie = DruzynaDAO.getAll();
        wszystkie.forEach(druzynaListModel::addElement);
    }

    private String znajdzNazweDruzyny(int id) {
        for (int i = 0; i < druzynaListModel.size(); i++) {
            if (druzynaListModel.get(i).getId() == id) {
                return druzynaListModel.get(i).getNazwa();
            }
        }
        return "Nieznana Drużyna (ID: " + id + ")";
    }

    private int pobierzLiczbe(String wiadomosc) {
        while (true) {
            String input = JOptionPane.showInputDialog(this, wiadomosc);
            if (input == null) return -1;
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Podaj poprawną liczbę!", "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}