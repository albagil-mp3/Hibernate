package com.billing.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.MouseInfo;

/**
 * Shared UI constants (colors, fonts) used by GUI classes.
 */
public final class UIConstants {
    public static final Color PURPLE_DARK = new Color(0x4A235A);
    public static final Color PURPLE_MAIN = new Color(0x7D3C98);
    public static final Color LAVENDER_LIGHT = new Color(0xF4F0FA);
    public static final Color ANTHRACITE = new Color(0x2C3E50);
    public static final Color TURQUOISE_ACCENT = new Color(0x1ABC9C);
    public static final Color BONE_WHITE = new Color(0xFAF9F6);

    public static final Font UI_FONT = new Font("Calibri", Font.PLAIN, 12);
    public static final Font TITLE_FONT = new Font("Calibri", Font.BOLD, 18);

    private UIConstants() { /* utility */ }

    // Reusable button styling (filled buttons with hover/pressed feedback)
    public static void styleButton(JButton btn, Color bg, Color fg, Dimension preferredSize) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFont(UI_FONT);
        if (preferredSize != null) btn.setPreferredSize(preferredSize);

        final Color base = bg;
        final Color hover = adjustBrightness(base, 1.08f);
        final Color pressed = adjustBrightness(base, 0.92f);

        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(hover); }
            @Override public void mouseExited(MouseEvent e)  { btn.setBackground(base);  }
            @Override public void mousePressed(MouseEvent e) { btn.setBackground(pressed); }
            @Override public void mouseReleased(MouseEvent e) {
                // restore hover if cursor still over button
                Point p = MouseInfo.getPointerInfo().getLocation();
                SwingUtilities.convertPointFromScreen(p, btn);
                if (btn.contains(p)) btn.setBackground(hover); else btn.setBackground(base);
            }
        });
    }

    // Shortcut used for dialog buttons (small size preset)
    public static void styleDialogButton(JButton btn, Color bg, Color fg) {
        styleButton(btn, bg, fg, new Dimension(80, 30));
    }

    // Creates a small painted arrow icon used in table headers
    public static Icon createSortArrowIcon(int size, boolean up, Color color) {
        return new Icon() {
            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                int midX = x + size/2;
                int[] xs, ys;
                if (up) {
                    xs = new int[]{x, x + size, midX};
                    ys = new int[]{y + size, y + size, y};
                } else {
                    xs = new int[]{x, x + size, midX};
                    ys = new int[]{y, y, y + size};
                }
                g2.fillPolygon(xs, ys, 3);
                g2.dispose();
            }
        };
    }

    // Utility to adjust brightness using HSB
    public static Color adjustBrightness(Color color, float factor) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        float newB = Math.min(1f, hsb[2] * factor);
        int rgb = Color.HSBtoRGB(hsb[0], hsb[1], newB);
        return new Color((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF);
    }

    // Utility to make JScrollPane mouse-wheel scrolling more responsive and consistent
    public static void enhanceScroll(JScrollPane scrollPane) {
        if (scrollPane == null) return;
        // avoid applying twice
        if (Boolean.TRUE.equals(scrollPane.getClientProperty("enhancedScroll"))) return;

        JScrollBar vbar = scrollPane.getVerticalScrollBar();
        // base unit (pixels) — reasonable default for most content
        final int unit = Math.max(16, vbar.getUnitIncrement());
        vbar.setUnitIncrement(unit * 2);    // faster per notch
        vbar.setBlockIncrement(unit * 10);  // page jump

        // direct mouse-wheel handling for consistent behavior across L&F
        scrollPane.getViewport().addMouseWheelListener(e -> {
            int rotation = e.getWheelRotation(); // positive = scroll down
            // scale per-notch movement (pixels)
            int delta = rotation * unit * 3;
            vbar.setValue(vbar.getValue() + delta);
            e.consume();
        });

        scrollPane.putClientProperty("enhancedScroll", Boolean.TRUE);
    }

    // Add maximize / restore control to a dialog header (reusable)
    public static void addMaxRestoreControl(JDialog dialog, JPanel headerPanel) {
        if (dialog == null || headerPanel == null) return;
        JRootPane root = dialog.getRootPane();
        if (Boolean.TRUE.equals(root.getClientProperty("hasMaxRestore"))) return;

        JButton maxRestore = new JButton("Maximize");
        styleDialogButton(maxRestore, PURPLE_MAIN, BONE_WHITE);
        maxRestore.setPreferredSize(new Dimension(90, 28));

        headerPanel.add(maxRestore, BorderLayout.EAST);

        maxRestore.addActionListener(ev -> {
            Boolean isMax = Boolean.TRUE.equals(root.getClientProperty("maximized"));
            if (!isMax) {
                // store previous bounds and maximize to available screen area
                root.putClientProperty("previousBounds", dialog.getBounds());
                Rectangle maxBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
                dialog.setBounds(maxBounds);
                maxRestore.setText("Restore");
                root.putClientProperty("maximized", Boolean.TRUE);
            } else {
                Rectangle prev = (Rectangle) root.getClientProperty("previousBounds");
                if (prev != null) dialog.setBounds(prev);
                maxRestore.setText("Maximize");
                root.putClientProperty("maximized", Boolean.FALSE);
            }
            dialog.validate();
        });

        root.putClientProperty("hasMaxRestore", Boolean.TRUE);
    }
}