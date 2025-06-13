package core;

import model.Druzyna;
import model.Mecz;

import java.util.*;

// tutaj dziala logika turnieju
public class TournamentManager {
    private List<Druzyna> wszystkieDruzyny;
    private List<Druzyna> druzynyEtapu;
    private List<Mecz> aktualnePary;
    private Queue<String> etapy;
    private List<String> listaMap = Arrays.asList(
            "Ascent", "Bind", "Haven", "Split", "Icebox", "Breeze", "Fracture", "Pearl", "Lotus", "Sunset", "Abyss"
    );
    private Random random = new Random();
    private List<Druzyna> wolnyLos = new ArrayList<>();

    private Druzyna zwyciezca, drugieMiejsce, trzecieMiejsce, czwarteMiejsce;

    private List<Druzyna> finalisci = new ArrayList<>();

    public TournamentManager(List<Druzyna> druzyny) {
        this.wszystkieDruzyny = new ArrayList<>(druzyny);
        this.druzynyEtapu = new ArrayList<>(druzyny);
        this.aktualnePary = new ArrayList<>();
        this.etapy = new LinkedList<>();

        // W konstruktorze na podstawie liczby drużyn buduję całą kolejkę etapów turnieju. Dzięki temu program wie czy ma zacząć od ćwierćfinału czy od razu od półfinału.
        if (wszystkieDruzyny.size() >= 5) {
            etapy.add("Ćwierćfinał");
        }
        if (wszystkieDruzyny.size() >= 3) {
            etapy.add("Półfinał");
        }
        if (wszystkieDruzyny.size() >= 4) {
            etapy.add("Mecz o 3. miejsce");
        }
        etapy.add("Finał");

        rozpocznijEtap();
    }

    private void rozpocznijEtap() {
        // ta metoda przygotowuje jedną rundę i obsluguje wolny los czyli to ze druzyna przechodzi dalej przez brak pary.
        aktualnePary.clear();
        wolnyLos.clear();
        List<Druzyna> lista = new ArrayList<>(druzynyEtapu);
        Collections.shuffle(lista, random);
        while (lista.size() >= 2) {
            Druzyna d1 = lista.remove(0);
            Druzyna d2 = lista.remove(0);
            aktualnePary.add(new Mecz(d1.getId(), d2.getId(), losujMape(), 0, 0, 0));
        }
        if (!lista.isEmpty()) {
            wolnyLos.add(lista.get(0));
        }
    }

    public void rozpocznijNastepnyEtap() {
        // 12. obsluguje odpalanie nastepnych etapow projektu zalezenie od tego ilu graczy zostalo.
        String zakonczonyEtap = etapy.poll();

        List<Druzyna> winnerList = new ArrayList<>(wolnyLos);
        List<Druzyna> loserList = new ArrayList<>();

        for (Mecz m : aktualnePary) {
            if (m.getZwyciezcaId() != 0) {
                winnerList.add(znajdzDruzynePoId(m.getZwyciezcaId()));
                int przegranyId = (m.getDruzyna1Id() == m.getZwyciezcaId()) ? m.getDruzyna2Id() : m.getDruzyna1Id();
                loserList.add(znajdzDruzynePoId(przegranyId));
            }
        }

        if ("Półfinał".equals(zakonczonyEtap)) {
            finalisci = new ArrayList<>(winnerList);
            if (!etapy.isEmpty() && "Mecz o 3. miejsce".equals(etapy.peek())) {
                druzynyEtapu = loserList;
            } else {
                druzynyEtapu = new ArrayList<>(finalisci);
                finalisci.clear();
            }
        } else if ("Mecz o 3. miejsce".equals(zakonczonyEtap)) {
            if (!winnerList.isEmpty()) trzecieMiejsce = winnerList.get(0);
            if (!loserList.isEmpty()) czwarteMiejsce = loserList.get(0);
            druzynyEtapu = new ArrayList<>(finalisci);
            finalisci.clear();
        } else if ("Finał".equals(zakonczonyEtap)) {
            if (!winnerList.isEmpty()) {
                zwyciezca = winnerList.get(0);
            }
            if (!loserList.isEmpty()) {
                drugieMiejsce = loserList.get(0);
            }
            druzynyEtapu.clear();
        } else {
            druzynyEtapu = winnerList;
        }

        if (druzynyEtapu.isEmpty() == false) {
            rozpocznijEtap();
        }
    }

    public String getNazwaEtapu() {
        if (zwyciezca != null) {
            return "Zakończony";
        }
        if (etapy.isEmpty()) {
            return "Gotowy do startu";
        }
        return etapy.peek();
    }

    public LinkedHashMap<String, String> podsumujWyniki() {
        LinkedHashMap<String, String> wyniki = new LinkedHashMap<>();
        if (zwyciezca != null)
            wyniki.put(zwyciezca.getNazwa(), "🥇 ZWYCIĘZCA TURNIEJU + Puchar");
        if (drugieMiejsce != null)
            wyniki.put(drugieMiejsce.getNazwa(), "🥈 Drugie miejsce + nagroda");
        if (trzecieMiejsce != null)
            wyniki.put(trzecieMiejsce.getNazwa(), "🥉 Trzecie miejsce");
        if (czwarteMiejsce != null)
            wyniki.put(czwarteMiejsce.getNazwa(), "🏅 Czwarte miejsce");

        Set<Integer> nagrodzeni = new HashSet<>();
        if (zwyciezca != null) nagrodzeni.add(zwyciezca.getId());
        if (drugieMiejsce != null) nagrodzeni.add(drugieMiejsce.getId());
        if (trzecieMiejsce != null) nagrodzeni.add(trzecieMiejsce.getId());
        if (czwarteMiejsce != null) nagrodzeni.add(czwarteMiejsce.getId());

        for (Druzyna d : wszystkieDruzyny) {
            if (!nagrodzeni.contains(d.getId())) {
                wyniki.put(d.getNazwa(), "🎁 Nagroda pocieszenia");
            }
        }
        return wyniki;
    }

    private Druzyna znajdzDruzynePoId(int id) {
        for (Druzyna team : wszystkieDruzyny) {
            if (team.getId() == id) {
                return team;
            }
        }
        return null;
    }

    private String losujMape() {
        int i = random.nextInt(listaMap.size());
        return listaMap.get(i);
    }

    public List<Mecz> podgladAktualnegoEtapu() {
        return new ArrayList<>(aktualnePary);
    }

    public List<String> getDostepneMapy() {
        return listaMap;
    }

    public Mecz pobierzKolejnyMecz() {
        for (Mecz m : aktualnePary) {
            if (m.getWynik1() == 0 && m.getWynik2() == 0) return m;
        }
        return null;
    }

    public void zapiszWynik(Mecz rozegrany) {
        for (Mecz m : aktualnePary) {
            if (m.getDruzyna1Id() == rozegrany.getDruzyna1Id() && m.getDruzyna2Id() == rozegrany.getDruzyna2Id()) {
                m.setWynik1(rozegrany.getWynik1());
                m.setWynik2(rozegrany.getWynik2());
                m.setZwyciezcaId(rozegrany.getZwyciezcaId());
            }
        }
    }

    public boolean etapZakonczony() {
        for (Mecz m : aktualnePary) {
            if (m.getWynik1() == 0 && m.getWynik2() == 0) {
                return false;
            }
        }
        return true;
    }

    public List<Druzyna> getWolnyLos() {
        return new ArrayList<>(wolnyLos);
    }
}