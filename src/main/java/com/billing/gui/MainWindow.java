package com.billing.gui;

import com.billing.service.ClientService;
import com.billing.util.HibernateUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * Main application window for the Billing System
 * Provides navigation to different modules of the application
 */
public class MainWindow extends JFrame {
    
    private ClientService clientService;
    private ClientManagementPanel clientManagementPanel;
    
    public MainWindow() {
        initializeServices();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        configureWindow();
    }
    
    private void initializeServices() {
        clientService = new ClientService();
        
        // Check database connectivity and show status
        if (!HibernateUtil.isInitialized()) {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this,
                    "Database is not available.\n" +
                    "The application will run in offline mode.\n" +
                    "Please check your MySQL connection and restart the application.",
                    "Database Warning",
                    JOptionPane.WARNING_MESSAGE);
            });
        }
    }
    
    private void initializeComponents() {
        setTitle("Billing System - Client Management");
        
        // Create menu bar
        createMenuBar();
        
        // Create main content panel
        clientManagementPanel = new ClientManagementPanel(clientService);
    }
    
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(UIConstants.PURPLE_DARK);
        menuBar.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));

        JMenu fileMenu = new JMenu("File");
        styleMenu(fileMenu);
        fileMenu.setHorizontalAlignment(SwingConstants.CENTER);

        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.setFont(UIConstants.UI_FONT);
        exitMenuItem.setForeground(Color.BLACK);
        exitMenuItem.setHorizontalAlignment(SwingConstants.CENTER);
        exitMenuItem.addActionListener(e -> exitApplication());
        fileMenu.add(exitMenuItem);

        JMenu clientsMenu = new JMenu("Clients");
        styleMenu(clientsMenu);
        clientsMenu.setHorizontalAlignment(SwingConstants.CENTER);

        JMenuItem manageClientsMenuItem = new JMenuItem("Manage Clients");
        manageClientsMenuItem.setFont(UIConstants.UI_FONT);
        manageClientsMenuItem.setForeground(Color.BLACK);
        manageClientsMenuItem.setHorizontalAlignment(SwingConstants.CENTER);
        manageClientsMenuItem.addActionListener(e -> showClientManagement());
        clientsMenu.add(manageClientsMenuItem);

        JMenu helpMenu = new JMenu("Help");
        styleMenu(helpMenu);
        helpMenu.setHorizontalAlignment(SwingConstants.CENTER);

        JMenuItem aboutMenuItem = new JMenuItem("About");
        aboutMenuItem.setFont(UIConstants.UI_FONT);
        aboutMenuItem.setForeground(Color.BLACK);
        aboutMenuItem.setHorizontalAlignment(SwingConstants.CENTER);
        aboutMenuItem.addActionListener(e -> showAbout());
        helpMenu.add(aboutMenuItem);

        menuBar.add(fileMenu);
        menuBar.add(clientsMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    // helper to style menus for consistent color/ font
    private void styleMenu(JMenu menu) {
        menu.setFont(UIConstants.UI_FONT);
        menu.setForeground(Color.BLACK);
        menu.setOpaque(false);
        menu.setHorizontalAlignment(SwingConstants.CENTER);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Add welcome panel initially
        add(createWelcomePanel(), BorderLayout.CENTER);
    }
    
    private JPanel createWelcomePanel() {
        GradientPanel welcomePanel = new GradientPanel(UIConstants.LAVENDER_LIGHT, UIConstants.BONE_WHITE);
        welcomePanel.setLayout(new BorderLayout());
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(48, 48, 48, 48));

        // Header bar
        GradientPanel header = new GradientPanel(UIConstants.PURPLE_DARK, UIConstants.PURPLE_MAIN);
        header.setPreferredSize(new Dimension(0, 80));
        header.setLayout(new BorderLayout());
        JLabel titleLabel = new JLabel("Billing System", SwingConstants.CENTER);
        titleLabel.setFont(UIConstants.TITLE_FONT.deriveFont(28f));
        titleLabel.setForeground(UIConstants.BONE_WHITE);
        header.add(titleLabel, BorderLayout.CENTER);
        welcomePanel.add(header, BorderLayout.NORTH);

        // Content
        JPanel contentPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));

        JLabel welcomeLabel = new JLabel("Welcome to the Client Management System", SwingConstants.CENTER);
        welcomeLabel.setFont(UIConstants.UI_FONT.deriveFont(16f));
        welcomeLabel.setForeground(UIConstants.ANTHRACITE);

        JLabel descriptionLabel = new JLabel("Use the menu to access client management features", SwingConstants.CENTER);
        descriptionLabel.setFont(UIConstants.UI_FONT.deriveFont(12f));
        descriptionLabel.setForeground(UIConstants.ANTHRACITE);

        JButton startButton = createStyledButton("Start Managing Clients",
            UIConstants.PURPLE_MAIN, UIConstants.BONE_WHITE, UIConstants.TURQUOISE_ACCENT);
        startButton.setPreferredSize(new Dimension(260, 44));
        startButton.addActionListener(e -> showClientManagement());

        contentPanel.add(welcomeLabel);
        contentPanel.add(descriptionLabel);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.add(startButton);
        contentPanel.add(buttonPanel);

        welcomePanel.add(contentPanel, BorderLayout.CENTER);

        return welcomePanel;
    }

    // Styled button with hover effect; accent color used for pressed/confirm state
    private JButton createStyledButton(String text, Color bg, Color fg, Color accent) {
        JButton b = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int arc = 14;
                Color fill = getModel().isPressed() ? accent : bg;
                g2.setColor(fill);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), arc, arc));
                // text
                g2.setColor(fg);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
                // do not call super.paintComponent to avoid default background draw
            }
        };

        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setFont(UIConstants.UI_FONT.deriveFont(Font.BOLD, 14f));
        b.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                b.setToolTipText(null);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                // no-op
            }
        });
        // keyboard accessibility: Space/Enter should show pressed appearance automatically via model
        return b;
    }
    
    private void setupEventHandlers() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitApplication();
            }
        });
    }
    
    private void configureWindow() {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(900, 640));
        setLocationRelativeTo(null);

        // apply soft lavender background
        getContentPane().setBackground(UIConstants.LAVENDER_LIGHT);

        // Set window icon (optional)
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/icon.png"));
            setIconImage(icon.getImage());
        } catch (Exception e) {
            // Icon not found, continue without icon
        }
    }

    // Simple gradient panel used for header and background
    private static class GradientPanel extends JPanel {
        private final Color c1;
        private final Color c2;
        public GradientPanel(Color c1, Color c2) {
            this.c1 = c1;
            this.c2 = c2;
            setOpaque(false);
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            GradientPaint gp = new GradientPaint(0, 0, c1, w, h, c2);
            g2.setPaint(gp);
            g2.fillRect(0, 0, w, h);
            g2.dispose();
            super.paintComponent(g);
        }
    }
    
    private void showClientManagement() {
        getContentPane().removeAll();
        add(clientManagementPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
        setTitle("Billing System - Client Management");
    }
    
    private void showAbout() {
        JOptionPane.showMessageDialog(this,
            "Billing System - Client Management\n" +
            "Version 1.0\n" +
            "\n" +
            "Java Application using Hibernate ORM and MySQL\n" +
            "Developed for client management in billing systems\n" +
            "\n" +
            "Features:\n" +
            "• Create, update, and delete clients\n" +
            "• Search and filter clients\n" +
            "• Spanish DNI and postal code validation\n" +
            "• Professional GUI with Calibri font",
            "About Billing System",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void exitApplication() {
        int option = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to exit the application?",
            "Exit Confirmation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (option == JOptionPane.YES_OPTION) {
            dispose();
            System.exit(0);
        }
    }
}