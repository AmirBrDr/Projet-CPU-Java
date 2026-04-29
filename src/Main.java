import interfaceutilisateur.SimulateurGUI;
import javax.swing.SwingUtilities;

/**
 * Point d'entrée principal de l'application Simulateur CPU.
 * <p>
 * Cette classe a pour unique responsabilité de lancer l'interface graphique (GUI)
 * de manière sécurisée via le thread de distribution d'événements Swing.
 *
 * @author Amirmahdi GHASEMI et Dorsa KHOSHNOOD
 * @version 1.0
 */
public class Main {
    /**
     * Méthode principale exécutée au lancement du programme.
     *
     * @param args les arguments de ligne de commande (non utilisés)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SimulateurGUI gui = new SimulateurGUI();
            gui.setVisible(true);
        });
    }
}
