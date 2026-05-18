package interfaceutilisateur;

import assembleur.Assembleur;
import assembleur.Programme;
import noyau.CPU;
import noyau.BanqueRegistres;
import noyau.Memoire;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.concurrent.ExecutionException;

/**
 * Interface graphique pour le simulateur de CPU et l'assembleur.
 *
 * @author Amirmahdi GHASEMI et Dorsa KHOSHNOOD
 * @version 1.0
 */
public class SimulateurGUI extends JFrame {
    private static final int TAILLE_MEMOIRE = 65536;
    private static final int TAILLE_PAGE_MEMOIRE = 256;

    private JTextArea editeurSource;
    private JTextArea numerosLignes;
    private JTextArea consoleLogs;
    private JTable tableRegistres;
    private JTable tableMemoire;
    private JLabel labelPC;
    private JLabel labelPlageMemoire;

    private JButton btnAssembler;
    private JButton btnExecuter;
    private JButton btnPasAPas;
    private JButton btnReinitialiser;
    private JButton btnMemoirePrecedente;
    private JButton btnMemoireSuivante;

    private CPU cpu;
    private Assembleur assembleur;
    private Programme programmeActuel;
    private int adresseDebutMemoireAffichee;

    public SimulateurGUI() {
        super("Simulateur CPU - Projet Carré Petit Utile");
        cpu = new CPU();
        assembleur = new Assembleur();

        initialiserUI();
        mettreAJourEtatCPU();
    }

    private void initialiserUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- ZONE GAUCHE : Éditeur de code source ---
        editeurSource = new JTextArea(20, 30);
        editeurSource.setFont(new Font("Monospaced", Font.PLAIN, 14));
        editeurSource.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                mettreAJourNumerosLignes();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                mettreAJourNumerosLignes();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                mettreAJourNumerosLignes();
            }
        });

        numerosLignes = new JTextArea("1");
        numerosLignes.setEditable(false);
        numerosLignes.setFocusable(false);
        numerosLignes.setFont(editeurSource.getFont());
        numerosLignes.setBackground(new Color(240, 240, 240));
        numerosLignes.setForeground(Color.GRAY);
        numerosLignes.setMargin(new Insets(0, 6, 0, 6));

        JScrollPane scrollEditeur = new JScrollPane(editeurSource);
        scrollEditeur.setRowHeaderView(numerosLignes);
        scrollEditeur.setBorder(BorderFactory.createTitledBorder("Code Source Assembleur"));
        add(scrollEditeur, BorderLayout.WEST);

        // --- ZONE CENTRALE : Console et Boutons ---
        JPanel panelCentre = new JPanel(new BorderLayout());
        
        consoleLogs = new JTextArea(8, 28);
        consoleLogs.setEditable(false);
        consoleLogs.setFont(new Font("Monospaced", Font.PLAIN, 12));
        consoleLogs.setForeground(new Color(0, 100, 0));
        JScrollPane scrollConsole = new JScrollPane(consoleLogs);
        scrollConsole.setPreferredSize(new Dimension(320, 0));
        scrollConsole.setBorder(BorderFactory.createTitledBorder("Console / Logs"));
        panelCentre.add(scrollConsole, BorderLayout.CENTER);

        JPanel panelBoutons = new JPanel(new FlowLayout());
        btnAssembler = new JButton("Assembler");
        btnExecuter = new JButton("Exécuter tout");
        btnPasAPas = new JButton("Pas à pas");
        btnReinitialiser = new JButton("Réinitialiser");

        panelBoutons.add(btnAssembler);
        panelBoutons.add(btnExecuter);
        panelBoutons.add(btnPasAPas);
        panelBoutons.add(btnReinitialiser);
        panelCentre.add(panelBoutons, BorderLayout.SOUTH);

        add(panelCentre, BorderLayout.CENTER);

        // --- ZONE DROITE : État du CPU ---
        JPanel panelEtat = new JPanel(new BorderLayout());
        panelEtat.setPreferredSize(new Dimension(460, 0));
        panelEtat.setBorder(BorderFactory.createTitledBorder("État du CPU"));

        labelPC = new JLabel("PC : 0x0000");
        labelPC.setFont(new Font("Monospaced", Font.BOLD, 14));
        labelPC.setHorizontalAlignment(SwingConstants.CENTER);
        panelEtat.add(labelPC, BorderLayout.NORTH);

        // Registres
        String[] colonnesRegistres = {"Registre", "Hex", "Décimal"};
        DefaultTableModel modeleRegistres = new DefaultTableModel(colonnesRegistres, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tableRegistres = new JTable(modeleRegistres);
        for (int i = 0; i < 16; i++) {
            modeleRegistres.addRow(new Object[]{"R" + i, "0x00", "0"});
        }
        JScrollPane scrollRegistres = new JScrollPane(tableRegistres);
        scrollRegistres.setPreferredSize(new Dimension(460, 250));

        // Mémoire (affichage des 256 premiers octets)
        String[] colonnesMemoire = {"Adresse (Hex)", "Adresse (Décimal)", "Valeur (Hex)", "Valeur (Décimal)"};
        DefaultTableModel modeleMemoire = new DefaultTableModel(colonnesMemoire, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tableMemoire = new JTable(modeleMemoire);
        for (int i = 0; i < TAILLE_PAGE_MEMOIRE; i++) {
            modeleMemoire.addRow(new Object[]{String.format("0x%04X", i), String.valueOf(i), "0x00", "0"});
        }
        JScrollPane scrollMemoire = new JScrollPane(tableMemoire);
        JPanel panelMemoire = new JPanel(new BorderLayout());
        panelMemoire.add(scrollMemoire, BorderLayout.CENTER);

        JPanel panelNavigationMemoire = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnMemoirePrecedente = new JButton("< Précédent");
        btnMemoireSuivante = new JButton("Suivant >");
        labelPlageMemoire = new JLabel();

        panelNavigationMemoire.add(btnMemoirePrecedente);
        panelNavigationMemoire.add(labelPlageMemoire);
        panelNavigationMemoire.add(btnMemoireSuivante);
        panelMemoire.add(panelNavigationMemoire, BorderLayout.SOUTH);

        JSplitPane splitEtat = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollRegistres, panelMemoire);
        splitEtat.setResizeWeight(0.5);
        panelEtat.add(splitEtat, BorderLayout.CENTER);

        add(panelEtat, BorderLayout.EAST);

        // --- ACTIONS DES BOUTONS ---
        btnAssembler.addActionListener(e -> assemblerProgramme());
        btnExecuter.addActionListener(e -> executerProgramme());
        btnPasAPas.addActionListener(e -> executerPasAPas());
        btnReinitialiser.addActionListener(e -> reinitialiser());
        btnMemoirePrecedente.addActionListener(e -> changerPageMemoire(-TAILLE_PAGE_MEMOIRE));
        btnMemoireSuivante.addActionListener(e -> changerPageMemoire(TAILLE_PAGE_MEMOIRE));
    }

    private void changerPageMemoire(int decalage) {
        int nouvelleAdresse = adresseDebutMemoireAffichee + decalage;
        if (nouvelleAdresse < 0) {
            nouvelleAdresse = 0;
        }
        int dernierePage = TAILLE_MEMOIRE - TAILLE_PAGE_MEMOIRE;
        if (nouvelleAdresse > dernierePage) {
            nouvelleAdresse = dernierePage;
        }

        adresseDebutMemoireAffichee = nouvelleAdresse;
        mettreAJourEtatCPU();
    }

    private void mettreAJourNumerosLignes() {
        int nombreLignes = editeurSource.getLineCount();
        int largeur = String.valueOf(nombreLignes).length();
        StringBuilder texteNumeros = new StringBuilder();

        for (int i = 1; i <= nombreLignes; i++) {
            texteNumeros.append(String.format("%" + largeur + "d", i));
            if (i < nombreLignes) {
                texteNumeros.append(System.lineSeparator());
            }
        }

        numerosLignes.setText(texteNumeros.toString());
    }

    private void assemblerProgramme() {
        String source = editeurSource.getText();
        if (source.trim().isEmpty()) {
            logErreur("Le code source est vide !");
            return;
        }

        try {
            programmeActuel = assembleur.assembler(source);
            logInfo("Assemblage réussi (" + programmeActuel.getInstructions().size() + " instructions).");
            
            // Charger dans le CPU
            cpu.chargerProgramme(programmeActuel);
            mettreAJourEtatCPU();
            logInfo("Programme chargé en mémoire.");
        } catch (Exception ex) {
            logErreur("Erreur d'assemblage : " + ex.getMessage());
        }
    }

    private void executerProgramme() {
        if (programmeActuel == null) {
            logErreur("Veuillez d'abord assembler le programme.");
            return;
        }

        setBoutonsExecutionActifs(false);
        logInfo("Exécution lancée.");

        SwingWorker<Void, Void> workerExecution = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                cpu.executerProgramme();
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    logInfo("Exécution terminée.");
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    cpu.arreter();
                    logErreur("Exécution interrompue.");
                } catch (ExecutionException ex) {
                    cpu.arreter();
                    logErreur("Erreur à l'exécution : " + ex.getCause().getMessage());
                } finally {
                    mettreAJourEtatCPU();
                    setBoutonsExecutionActifs(true);
                }
            }
        };

        workerExecution.execute();
    }

    private void setBoutonsExecutionActifs(boolean actif) {
        btnAssembler.setEnabled(actif);
        btnExecuter.setEnabled(actif);
        btnPasAPas.setEnabled(actif);
        btnReinitialiser.setEnabled(actif);
    }

    private void executerPasAPas() {
        if (programmeActuel == null) {
            logErreur("Veuillez d'abord assembler le programme.");
            return;
        }
        try {
            // Empêcher l'exécution si on a atteint une instruction invalide ou fin de mémoire
            int adresseActuelle = cpu.getPc();
            if (adresseActuelle >= 65536) {
                logErreur("Fin de la mémoire atteinte.");
                return;
            }
            byte opcode = cpu.getMemoire().lireOctet(adresseActuelle);
            if (opcode == 0) { // 0 correspond à l'instruction BREAK
                cpu.appliquerInstruction(cpu.decoderInstruction()); // pour traiter le break
                mettreAJourEtatCPU();
                logInfo("Instruction BREAK atteinte. Arrêt.");
                return;
            }

            cpu.executerInstruction();
            mettreAJourEtatCPU();
            logInfo("Exécution d'une instruction (PC = " + cpu.getPc() + ").");
        } catch (Exception ex) {
            cpu.arreter();
            logErreur("Erreur à l'exécution pas à pas : " + ex.getMessage());
            mettreAJourEtatCPU();
        }
    }

    private void reinitialiser() {
        cpu.reinitialiser();
        programmeActuel = null;
        mettreAJourEtatCPU();
        consoleLogs.setText("");
        logInfo("CPU et mémoire réinitialisés.");
    }

    private void mettreAJourEtatCPU() {
        // Mettre à jour le PC
        labelPC.setText(String.format("PC : 0x%04X", cpu.getPc()));

        // Mettre à jour les registres
        BanqueRegistres br = cpu.getBanqueRegistres();
        DefaultTableModel modeleRegistres = (DefaultTableModel) tableRegistres.getModel();
        for (int i = 0; i < 16; i++) {
            byte val = br.lireRegistre(i);
            modeleRegistres.setValueAt(String.format("0x%02X", val), i, 1);
            modeleRegistres.setValueAt(String.valueOf(val), i, 2);
        }

        // Mettre à jour la mémoire par pages de 256 adresses.
        Memoire mem = cpu.getMemoire();
        DefaultTableModel modeleMemoire = (DefaultTableModel) tableMemoire.getModel();
        for (int i = 0; i < TAILLE_PAGE_MEMOIRE; i++) {
            int adresse = adresseDebutMemoireAffichee + i;
            byte val = mem.lireOctet(adresse);
            modeleMemoire.setValueAt(String.format("0x%04X", adresse), i, 0);
            modeleMemoire.setValueAt(String.valueOf(adresse), i, 1);
            modeleMemoire.setValueAt(String.format("0x%02X", val), i, 2);
            modeleMemoire.setValueAt(String.valueOf(val & 0xFF), i, 3);
        }

        int adresseFinMemoireAffichee = adresseDebutMemoireAffichee + TAILLE_PAGE_MEMOIRE - 1;
        labelPlageMemoire.setText(String.format(
                "0x%04X - 0x%04X",
                adresseDebutMemoireAffichee,
                adresseFinMemoireAffichee
        ));
        btnMemoirePrecedente.setEnabled(adresseDebutMemoireAffichee > 0);
        btnMemoireSuivante.setEnabled(adresseFinMemoireAffichee < TAILLE_MEMOIRE - 1);
    }

    private void logInfo(String message) {
        consoleLogs.append("[INFO] " + message + "\n");
    }

    private void logErreur(String message) {
        consoleLogs.append("[ERREUR] " + message + "\n");
    }
}
