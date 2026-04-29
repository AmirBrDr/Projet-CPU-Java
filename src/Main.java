import interfaceutilisateur.SimulateurGUI;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SimulateurGUI gui = new SimulateurGUI();
            gui.setVisible(true);
        });
    }
}
