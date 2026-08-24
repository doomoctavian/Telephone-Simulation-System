import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class UITheme {

    private UITheme() {}

    public static final Color BG            = new Color(0xF4, 0xF6, 0xFB);
    public static final Color PANEL_BG      = Color.WHITE;
    public static final Color PRIMARY       = new Color(0x2F, 0x6F, 0xED);
    public static final Color PRIMARY_DARK  = new Color(0x1E, 0x4F, 0xC4);
    public static final Color ACCENT        = new Color(0x22, 0xA0, 0x6B);
    public static final Color TEXT_DARK     = new Color(0x1F, 0x29, 0x37);
    public static final Color TEXT_MUTED    = new Color(0x64, 0x74, 0x8B);
    public static final Color BORDER        = new Color(0xD5, 0xDC, 0xE8);

    public static final Color FREE_BG       = new Color(0xE3, 0xF7, 0xE9);
    public static final Color FREE_FG       = new Color(0x15, 0x80, 0x3D);
    public static final Color BUSY_BG       = new Color(0xFD, 0xE7, 0xE7);
    public static final Color BUSY_FG       = new Color(0xC4, 0x2A, 0x2A);

    public static final Color RUN_BG        = new Color(0x22, 0xA0, 0x6B);
    public static final Color PAUSE_BG      = new Color(0xE0, 0x8E, 0x1D);
    public static final Color RESTART_BG    = new Color(0x64, 0x74, 0x8B);

    // ---------- Fonts ----------
    public static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_SUB     = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_LABEL   = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_FIELD   = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_CLOCK   = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_BUTTON  = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_MONO    = new Font("Consolas", Font.PLAIN, 13);

    public static JPanel titledPanel(String title, int x, int y, int w, int h) {
        JPanel panel = new JPanel();
        panel.setBackground(PANEL_BG);
        TitledBorder tb = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER, 1, true), title,
                TitledBorder.LEADING, TitledBorder.TOP);
        tb.setTitleFont(FONT_LABEL);
        tb.setTitleColor(PRIMARY_DARK);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(2, 2, 2, 2), tb));
        panel.setBounds(x, y, w, h);
        return panel;
    }

    public static JTextField valueField() {
        JTextField f = new JTextField();
        f.setEditable(false);
        f.setHorizontalAlignment(JTextField.CENTER);
        f.setFont(FONT_FIELD);
        f.setForeground(TEXT_DARK);
        f.setBackground(new Color(0xF7, 0xF9, 0xFD));
        f.setBorder(BorderFactory.createLineBorder(BORDER));
        return f;
    }

    /** Colors a line-status field to clearly show FREE (green) vs BUSY (red). */
    public static void paintLineStatus(JTextField f, int status) {
        boolean busy = status != 0;
        f.setText(busy ? "BUSY" : "FREE");
        f.setBackground(busy ? BUSY_BG : FREE_BG);
        f.setForeground(busy ? BUSY_FG : FREE_FG);
    }

    /** A flat, colored button with a hover effect for a nicer, clickable feel. */
    public static JButton button(String text, Color base) {
        JButton b = new JButton(text);
        b.setFont(FONT_BUTTON);
        b.setForeground(Color.WHITE);
        b.setBackground(base);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        Color hover = base.brighter();
        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { b.setBackground(hover); }
            @Override public void mouseExited(MouseEvent e) { b.setBackground(base); }
        });
        return b;
    }

    public static void styleFrame(JFrame frame) {
        frame.getContentPane().setBackground(BG);
    }

    public static void styleLog(JTextArea logArea) {
        logArea.setFont(FONT_MONO);
        logArea.setBackground(new Color(0xFC, 0xFD, 0xFE));
        logArea.setForeground(TEXT_DARK);
        logArea.setMargin(new Insets(6, 8, 6, 8));
    }
}
