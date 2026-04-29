package interfaceutilisateur;

import assembleur.Assembleur;
import assembleur.Programme;
import noyau.CPU;
import noyau.BanqueRegistres;
import noyau.Memoire;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Interface graphique pour le simulateur de CPU et l'assembleur.
 *
 * @author Amirmahdi GHASEMI et Dorsa KHOSHNOOD
 * @version 1.0
 */
public class SimulateurGUI extends JFrame {

    private JTextArea editeurSource;
    private JTextArea consoleLogs;
    private JTable tableRegistres;
    private JTable tableMemoire;
    private JLabel labelPC;

    private JButton btnAssembler;
    private JButton btnExecuter;
    private JButton btnPasAPas;
    private JButton btnReinitialiser;

    private CPU cpu;
    private Assembleur assembleur;
    private Programme programmeActuel;

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
        JScrollPane scrollEditeur = new JScrollPane(editeurSource);
        scrollEditeur.setBorder(BorderFactory.createTitledBorder("Code Source Assembleur"));
        add(scrollEditeur, BorderLayout.WEST);

        // --- ZONE CENTRALE : Console et Boutons ---
        JPanel panelCentre = new JPanel(new BorderLayout());
        
        consoleLogs = new JTextArea(10, 40);
        consoleLogs.setEditable(false);
        consoleLogs.setFont(new Font("Monospaced", Font.PLAIN, 12));
        consoleLogs.setForeground(new Color(0, 100, 0));
        JScrollPane scrollConsole = new JScrollPane(consoleLogs);
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
        panelEtat.setPreferredSize(new Dimension(300, 0));
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
        scrollRegistres.setPreferredSize(new Dimension(300, 250));

        // Mémoire (affichage des 256 premiers octets)
        String[] colonnesMemoire = {"Adresse", "Valeur (Hex)"};
        DefaultTableModel modeleMemoire = new DefaultTableModel(colonnesMemoire, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tableMemoire = new JTable(modeleMemoire);
        for (int i = 0; i < 256; i++) {
            modeleMemoire.addRow(new Object[]{String.format("0x%04X", i), "0x00"});
        }
        JScrollPane scrollMemoire = new JScrollPane(tableMemoire);

        JSplitPane splitEtat = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollRegistres, scrollMemoire);
        splitEtat.setResizeWeight(0.5);
        panelEtat.add(splitEtat, BorderLayout.CENTER);

        add(panelEtat, BorderLayout.EAST);

        // --- ACTIONS DES BOUTONS ---
        btnAssembler.addActionListener(e -> assemblerProgramme());
        btnExecuter.addActionListener(e -> executerProgramme());
        btnPasAPas.addActionListener(e -> executerPasAPas());
        btnReinitialiser.addActionListener(e -> reinitialiser());
    }

    private void assemblerProgramme() {
        String source = editeurSource.getText().trim();
        if (source.isEmpty()) {
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
        try {
            cpu.executerProgramme();
            mettreAJourEtatCPU();
            logInfo("Exécution terminée.");
        } catch (Exception ex) {
            logErreur("Erreur à l'exécution : " + ex.getMessage());
            mettreAJourEtatCPU();
        }
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
            if (opcode == 5) { // 5 correspond théoriquement à l'instruction BREAK
                cpu.appliquerInstruction(cpu.decoderInstruction()); // pour traiter le break
                mettreAJourEtatCPU();
                logInfo("Instruction BREAK atteinte. Arrêt.");
                return;
            }

            cpu.executerInstruction();
            mettreAJourEtatCPU();
            logInfo("Exécution d'une instruction (PC = " + cpu.getPc() + ").");
        } catch (Exception ex) {
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

        // Mettre à jour la mémoire (affichage partiel 0-255)
        Memoire mem = cpu.getMemoire();
        DefaultTableModel modeleMemoire = (DefaultTableModel) tableMemoire.getModel();
        for (int i = 0; i < 256; i++) {
            byte val = mem.lireOctet(i);
            modeleMemoire.setValueAt(String.format("0x%02X", val), i, 1);
        }
    }

    private void logInfo(String message) {
        consoleLogs.append("[INFO] " + message + "\n");
    }

    private void logErreur(String message) {
        consoleLogs.append("[ERREUR] " + message + "\n");
    }
}
