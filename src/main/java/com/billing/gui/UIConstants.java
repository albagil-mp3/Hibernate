package com.billing.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

/**
 * Shared UI constants (colors, fonts) used by GUI classes.
 */
public final class UIConstants {
    // Detailed module palettes (background / text / icon / border)
    // Clients - Professional blue
    public static final Color MODULE_CLIENTS_BG = new Color(0xE3F2FD);
    public static final Color MODULE_CLIENTS_TEXT = new Color(0x1565C0);
    public static final Color MODULE_CLIENTS_ICON = new Color(0x1976D2);
    public static final Color MODULE_CLIENTS_BORDER = new Color(0x90CAF9);

    // Products - Green growth
    public static final Color MODULE_PRODUCTS_BG = new Color(0xE8F5E9);
    public static final Color MODULE_PRODUCTS_TEXT = new Color(0x2E7D32);
    public static final Color MODULE_PRODUCTS_ICON = new Color(0x4CAF50);
    public static final Color MODULE_PRODUCTS_BORDER = new Color(0xA5D6A7);

    // Suppliers - Orange energy
    public static final Color MODULE_SUPPLIERS_BG = new Color(0xFFF3E0);
    public static final Color MODULE_SUPPLIERS_TEXT = new Color(0xEF6C00);
    public static final Color MODULE_SUPPLIERS_ICON = new Color(0xFF9800);
    public static final Color MODULE_SUPPLIERS_BORDER = new Color(0xFFCC80);

    // Invoices - PProfessional purple
    public static final Color MODULE_INVOICES_BG = new Color(0xF3E5F5);
    public static final Color MODULE_INVOICES_TEXT = new Color(0x7B1FA2);
    public static final Color MODULE_INVOICES_ICON = new Color(0x9C27B0);
    public static final Color MODULE_INVOICES_BORDER = new Color(0xCE93D8);

    // Semantic palettes
    // SUCCESS (Green)
    public static final Color SUCCESS_DARK = new Color(0x2E7D32);
    public static final Color SUCCESS = new Color(0x4CAF50);
    public static final Color SUCCESS_LIGHT = new Color(0x81C784);
    public static final Color SUCCESS_BG = new Color(0xE8F5E9);

    // DANGER (Red)
    public static final Color DANGER_DARK = new Color(0xC62828);
    public static final Color DANGER = new Color(0xF44336);
    public static final Color DANGER_LIGHT = new Color(0xEF9A9A);
    public static final Color DANGER_BG = new Color(0xFFEBEE);

    // INFO (Light blue)
    public static final Color INFO_DARK = new Color(0x00838F);
    public static final Color INFO = new Color(0x00BCD4);
    public static final Color INFO_LIGHT = new Color(0x80DEEA);
    public static final Color INFO_BG = new Color(0xE0F7FA);

    // Common fonts
    public static final Font UI_FONT = new Font("Calibri", Font.PLAIN, 12);
    public static final Font TITLE_FONT = new Font("Calibri", Font.BOLD, 18);
    
    // Font aliases for compatibility
    public static final Font DEFAULT_FONT = UI_FONT;
    public static final Font BUTTON_FONT = new Font("Calibri", Font.BOLD, 12);

    // Global button size (uniform for all button types)
    public static final java.awt.Dimension BUTTON_SIZE = new Dimension(160, 40);

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
        styleButton(btn, bg, fg, BUTTON_SIZE);
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

    /**
     * Load an image icon from resources and scale it to size.
     */
    public static Icon loadIcon(String resourcePath, int size) {
        java.net.URL url = UIConstants.class.getResource(resourcePath);
        if (url == null) {
            // Resource missing: return a transparent placeholder to avoid NPEs
            BufferedImage placeholder = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            return new ImageIcon(placeholder);
        }
        ImageIcon ii = new ImageIcon(url);
        Image img = ii.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    /**
     * Scale an icon to a given size.
     */
    public static Icon scaleIcon(Icon icon, int size) {
        if (icon == null) return null;
        if (icon instanceof ImageIcon ii) {
            Image img = ii.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        }
        return icon;
    }

    // ---------- Button factories (primary / secondary / danger / success) ----------
    public static JButton createPrimaryButton(String text, Icon icon) {
        // Primary
        Color primaryBg = new Color(0x2196F3);
        Color primaryHover = new Color(0x0B7DDA);
        Color primaryPressed = adjustBrightness(primaryBg, 0.88f);
        Color primaryColor = new Color(0x2196F3);

        RoundedButton b = new RoundedButton(text != null ? text.toUpperCase() : null, scaleIcon(icon, 18), 8, primaryColor, Color.WHITE, true, true, primaryHover, primaryPressed);
        applyCommonButtonProps(b, UI_FONT.deriveFont(Font.BOLD, 14f), new Insets(6, 24, 6, 24), 12);
        return b;
    }

    public static JButton createSecondaryButton(String text, Icon icon) {
        // Secondary
        Color secBg = new Color(0x6B7280);
        Color secHover = new Color(0x4B5563);
        Color secPressed = adjustBrightness(secBg, 0.88f);

        RoundedButton b = new RoundedButton(text != null ? text.toUpperCase() : null, scaleIcon(icon, 18), 8, secBg, Color.WHITE, true, true, secHover, secPressed);
        applyCommonButtonProps(b, UI_FONT.deriveFont(Font.BOLD, 14f), new Insets(10, 22, 10, 22), 8);
        b.setForeground(Color.WHITE);
        return b;
    }

    public static JButton createDangerButton(String text, Icon icon) {
        // Dangerous actions (red)
        RoundedButton b = new RoundedButton(text, scaleIcon(icon, 18), 6, DANGER, Color.WHITE, true, true);
        applyCommonButtonProps(b, UI_FONT.deriveFont(Font.BOLD, 14f), new Insets(6, 10, 6, 10), 8);
        return b;
    }

    public static JButton createSuccessButton(String text, Icon icon) {
        // Success / confirm
        RoundedButton b = new RoundedButton(text, scaleIcon(icon, 18), 6, SUCCESS, Color.WHITE, true, true);
        applyCommonButtonProps(b, UI_FONT.deriveFont(Font.BOLD, 14f), new Insets(6, 12, 6, 12), 8);
        return b;
    }

    /**
     * Create a module-styled large button (used on dashboard).
     * Uses a light background, colored icon and text, optional border.
     */
    public static JButton createModuleButton(String text, Icon icon, Color bg, Color textColor, Color iconColor, Color borderColor) {
        RoundedButton b = new RoundedButton(text, icon, 10, bg, textColor, false, true);
        b.setFont(UI_FONT.deriveFont(Font.BOLD, 14f));
        b.setHorizontalAlignment(SwingConstants.CENTER);
        b.setHorizontalTextPosition(SwingConstants.CENTER);
        b.setVerticalTextPosition(SwingConstants.BOTTOM);
        b.setIconTextGap(10);
        b.setPreferredSize(new Dimension(260, 100));
        return b;
    }

    // Helper: apply common visual properties to buttons to avoid duplication
    private static void applyCommonButtonProps(RoundedButton b, Font font, Insets margin, int iconGap) {
        b.setFont(font);
        b.setHorizontalAlignment(SwingConstants.CENTER);
        b.setHorizontalTextPosition(SwingConstants.RIGHT);
        b.setIconTextGap(iconGap);
        b.setMargin(margin);
        b.setPreferredSize(BUTTON_SIZE);
    }

    // No visible outline borders: we intentionally avoid drawing lines around buttons.

    // Small rounded button implementation that paints rounded background and optional shadow.
    private static class RoundedButton extends JButton {
        private final int radius;
        private final Color baseColor;
        private final Color fgColor;
        private final boolean drawShadow;
        private final boolean filled;
        private final Color hoverColor;
        private final Color pressedColor;
        private boolean hover = false;
        private boolean pressed = false;

        // fields to remember original visuals; border can be assigned after construction
        private Border originalBorder = null;
        private Color originalForeground = null;

        // Backwards-compatible constructors
        RoundedButton(String text, Icon icon, int radius, Color base, Color fg, boolean shadow, boolean filled) {
            this(text, icon, radius, base, fg, shadow, filled, null, null);
        }

        RoundedButton(String text, Icon icon, int radius, Color base, Color fg, boolean shadow, boolean filled, Color hoverColor, Color pressedColor) {
            super(text, icon);
            this.radius = radius;
            this.baseColor = base;
            this.fgColor = fg;
            this.drawShadow = shadow;
            this.filled = filled;
            // default hover/pressed if not provided
            this.hoverColor = hoverColor != null ? hoverColor : adjustBrightness(base, 0.95f);
            this.pressedColor = pressedColor != null ? pressedColor : adjustBrightness(base, 0.9f);
            setContentAreaFilled(false);
            // allow border painting (factories assign borders after construction)
            setBorderPainted(true);
            setFocusPainted(false);
            setOpaque(false);
            setForeground(fgColor);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            // capture original foreground now; border may be assigned later so capture on first hover
            this.originalForeground = getForeground();

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) {
                    hover = true;
                    // capture original border if not captured yet (allows callers to set border after ctor)
                    if (originalBorder == null) originalBorder = getBorder();
                    if (originalForeground == null) originalForeground = getForeground();

                    // apply subtle elevation via empty bottom inset (no line)
                    setBorder(BorderFactory.createEmptyBorder(0,0,2,0));
                    // change text color slightly for emphasis
                    setForeground(adjustBrightness(fgColor, 0.85f));
                    repaint();
                }
                @Override public void mouseExited(MouseEvent e)  {
                    hover = false; pressed = false;
                    // restore original border/foreground
                    setBorder(originalBorder);
                    setForeground(originalForeground != null ? originalForeground : fgColor);
                    repaint();
                }
                @Override public void mousePressed(MouseEvent e) { pressed = true; repaint(); }
                @Override public void mouseReleased(MouseEvent e) { pressed = false; repaint(); }
            });
        }

        @Override
        public void setBorder(Border border) {
            super.setBorder(border);
            // remember the original border when it's assigned so hover can restore it
            if (this.originalBorder == null && border != null) {
                this.originalBorder = border;
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            Color bg = baseColor;
            if (pressed) bg = pressedColor;
            else if (hover) bg = hoverColor;

            if (drawShadow && filled) {
                // use a tinted semi-transparent shadow based on the base color
                Color sc = new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 77);
                g2.setColor(sc);
                g2.fillRoundRect(2, 3, w-4, h-3, radius, radius);
            }

            if (filled) {
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, w-2, h-4, radius, radius);
            } else if (hover) {
                // subtle hover fill for outlined buttons
                Color hoverFill = new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), 18);
                g2.setColor(hoverFill);
                g2.fillRoundRect(0, 0, w-2, h-4, radius, radius);
            }

            g2.dispose();

            super.paintComponent(g);
        }

        @Override
        public boolean isContentAreaFilled() { return false; }
    }
}