import javax.swing.*;
import java.awt.*;

public class WelcomeScreen extends JFrame {

    public WelcomeScreen() {
        super("Telephone Simulation System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 380);
        setLocationRelativeTo(null);
        setLayout(null);
        UITheme.styleFrame(this);

        JLabel titleLabel = new JLabel("Telephone Simulation System", SwingConstants.CENTER);
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(UITheme.PRIMARY_DARK);
        titleLabel.setBounds(20, 40, 480, 34);
        add(titleLabel);

        JLabel nameLabel = new JLabel("Name: Samir Panta", SwingConstants.CENTER);
        nameLabel.setFont(UITheme.FONT_SUB);
        nameLabel.setForeground(UITheme.TEXT_DARK);
        nameLabel.setBounds(20, 150, 480, 22);
        add(nameLabel);

        JLabel crnLabel = new JLabel("CRN: 023-366", SwingConstants.CENTER);
        crnLabel.setFont(UITheme.FONT_SUB);
        crnLabel.setForeground(UITheme.TEXT_DARK);
        crnLabel.setBounds(20, 178, 480, 22);
        add(crnLabel);

        JLabel pickLabel = new JLabel("Choose a simulation mode:", SwingConstants.CENTER);
        pickLabel.setFont(UITheme.FONT_LABEL);
        pickLabel.setForeground(UITheme.TEXT_MUTED);
        pickLabel.setBounds(20, 235, 480, 22);
        add(pickLabel);

        JButton lostCallButton = UITheme.button("LOST CALL", UITheme.PRIMARY);
        lostCallButton.setBounds(90, 275, 150, 42);
        lostCallButton.addActionListener(e -> new LostCall().setVisible(true));
        add(lostCallButton);

        JButton delayedCallButton = UITheme.button("DELAYED CALL", UITheme.ACCENT);
        delayedCallButton.setBounds(280, 275, 150, 42);
        delayedCallButton.addActionListener(e -> new DelayedCall().setVisible(true));
        add(delayedCallButton);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WelcomeScreen().setVisible(true));
    }
}
