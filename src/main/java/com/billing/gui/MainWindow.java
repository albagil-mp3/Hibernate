package com.billing.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.billing.service.ClientService;
import com.billing.util.HibernateUtil;

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
                JOptionPane.showMessageDialog(this, """
                                                    Database is not available.
                                                    The application will run in offline mode.
                                                    Please check your MySQL connection and restart the application.""",
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
        menuBar.setBackground(Color.WHITE);
        menuBar.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        menuBar.setPreferredSize(new Dimension(0, 48));

        // Top-level module menus 
        JMenu homeMenu = new JMenu("Home");
        styleMenu(homeMenu);
        homeMenu.setHorizontalAlignment(SwingConstants.CENTER);
        homeMenu.setIcon(UIConstants.loadIcon("/icons/home-dark.png", 14));
        homeMenu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showHome();
            }
        });

        JMenu clientsMenu = new JMenu("Clients");
        styleMenu(clientsMenu);
        clientsMenu.setHorizontalAlignment(SwingConstants.CENTER);
        clientsMenu.setIcon(UIConstants.loadIcon("/icons/clients-dark.png", 12));
        clientsMenu.setForeground(UIConstants.MODULE_CLIENTS_TEXT);
        clientsMenu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showClientManagement();
            }
        });
        
        JMenu invoicesMenu = new JMenu("Invoices");
        styleMenu(invoicesMenu);
        invoicesMenu.setIcon(UIConstants.loadIcon("/icons/invoices-dark.png", 12));
        invoicesMenu.setForeground(UIConstants.MODULE_INVOICES_TEXT);
        invoicesMenu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showModuleStub("Invoices");
            }
        });

        JMenu productsMenu = new JMenu("Products");
        styleMenu(productsMenu);
        productsMenu.setIcon(UIConstants.loadIcon("/icons/products-dark.png", 12));
        productsMenu.setForeground(UIConstants.MODULE_PRODUCTS_TEXT);
        productsMenu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showModuleStub("Products");
            }
        });

        JMenu suppliersMenu = new JMenu("Suppliers");
        styleMenu(suppliersMenu);
        suppliersMenu.setIcon(UIConstants.loadIcon("/icons/suppliers-dark.png", 12));
        suppliersMenu.setForeground(UIConstants.MODULE_SUPPLIERS_TEXT);
        suppliersMenu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showModuleStub("Suppliers");
            }
        });
        
        menuBar.add(homeMenu);
        menuBar.add(clientsMenu);
        menuBar.add(invoicesMenu);
        menuBar.add(productsMenu);
        menuBar.add(suppliersMenu);
        setJMenuBar(menuBar);
    }

    // helper to style menus for consistent color/ font
    private void styleMenu(JMenu menu) {
        menu.setFont(UIConstants.UI_FONT.deriveFont(Font.BOLD, 13f));
        menu.setForeground(new Color(0x212121));
        menu.setOpaque(false);
        menu.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        menu.setHorizontalAlignment(SwingConstants.CENTER);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Add welcome panel initially
        add(createWelcomePanel(), BorderLayout.CENTER);
    }
    
    private JPanel createWelcomePanel() {
        GradientPanel welcomePanel = new GradientPanel(UIConstants.INFO_BG, Color.WHITE);
        welcomePanel.setLayout(new BorderLayout());
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(48, 48, 48, 48));

        // Header bar
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIConstants.INFO_DARK);
        header.setOpaque(true);
        header.setPreferredSize(new Dimension(0, 80));
        JLabel titleLabel = new JLabel("Billing System", SwingConstants.CENTER);
        titleLabel.setFont(UIConstants.TITLE_FONT.deriveFont(24f));
        titleLabel.setForeground(Color.WHITE);
        header.add(titleLabel, BorderLayout.CENTER);
        welcomePanel.add(header, BorderLayout.NORTH);

        // Content: welcome text + module buttons grid
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));

        JLabel welcomeLabel = new JLabel("Welcome. Select a module to begin.", SwingConstants.CENTER);
        welcomeLabel.setFont(UIConstants.UI_FONT.deriveFont(18f));
        welcomeLabel.setForeground(UIConstants.INFO_DARK);
        contentPanel.add(welcomeLabel, BorderLayout.NORTH);

        JPanel modulesGrid = new JPanel(new GridLayout(2, 2, 18, 18));
        modulesGrid.setOpaque(false);
        modulesGrid.setBorder(BorderFactory.createEmptyBorder(24, 80, 24, 80));

        // Module buttons
        JButton clientsBtn = UIConstants.createModuleButton("CLIENTS",
            UIConstants.loadIcon("/icons/clients.png", 56),
            UIConstants.MODULE_CLIENTS_TEXT,
            UIConstants.MODULE_CLIENTS_BG,
            UIConstants.MODULE_CLIENTS_ICON,
            UIConstants.MODULE_CLIENTS_BORDER);
        clientsBtn.addActionListener(e -> showClientManagement());

        JButton invoicesBtn = UIConstants.createModuleButton("INVOICES",
            UIConstants.loadIcon("/icons/invoices.png", 56),
            UIConstants.MODULE_INVOICES_TEXT,
            UIConstants.MODULE_INVOICES_BG,
            UIConstants.MODULE_INVOICES_ICON,
            UIConstants.MODULE_INVOICES_BORDER);
        invoicesBtn.addActionListener(e -> showModuleStub("Invoices"));

        JButton productsBtn = UIConstants.createModuleButton("PRODUCTS",
            UIConstants.loadIcon("/icons/products.png", 56),
            UIConstants.MODULE_PRODUCTS_TEXT,
            UIConstants.MODULE_PRODUCTS_BG,
            UIConstants.MODULE_PRODUCTS_ICON,
            UIConstants.MODULE_PRODUCTS_BORDER);
        productsBtn.addActionListener(e -> showModuleStub("Products"));

        JButton suppliersBtn = UIConstants.createModuleButton("SUPPLIERS",
            UIConstants.loadIcon("/icons/suppliers.png", 56),
            UIConstants.MODULE_SUPPLIERS_TEXT,
            UIConstants.MODULE_SUPPLIERS_BG,
            UIConstants.MODULE_SUPPLIERS_ICON,
            UIConstants.MODULE_SUPPLIERS_BORDER);
        suppliersBtn.addActionListener(e -> showModuleStub("Suppliers"));

        modulesGrid.add(clientsBtn);
        modulesGrid.add(invoicesBtn);
        modulesGrid.add(productsBtn);
        modulesGrid.add(suppliersBtn);

        contentPanel.add(modulesGrid, BorderLayout.CENTER);

        welcomePanel.add(contentPanel, BorderLayout.CENTER);

        return welcomePanel;
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

        // apply soft info background
        getContentPane().setBackground(UIConstants.INFO_BG);

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
        // Wrap the client management panel in a module-colored gradient
        GradientPanel wrapper = new GradientPanel(
            UIConstants.adjustBrightness(UIConstants.MODULE_CLIENTS_BG, 0.85f),
            UIConstants.MODULE_CLIENTS_BG);
        wrapper.setLayout(new BorderLayout());
        wrapper.add(clientManagementPanel, BorderLayout.CENTER);
        add(wrapper, BorderLayout.CENTER);
        revalidate();
        repaint();
        setTitle("Billing System - Client Management");
    }

    private Color getModuleColor(String moduleName) {
        if (moduleName == null) return UIConstants.MODULE_CLIENTS_BG;
        return switch (moduleName.toLowerCase()) {
            case "clients" -> UIConstants.MODULE_CLIENTS_BG;
            case "products" -> UIConstants.MODULE_PRODUCTS_BG;
            case "suppliers" -> UIConstants.MODULE_SUPPLIERS_BG;
            case "invoices" -> UIConstants.MODULE_INVOICES_BG;
            default -> UIConstants.MODULE_CLIENTS_BG;
        };
    }

    /**
     * Show the welcome/home panel.
     * Exposed so child panels can return to the main dashboard.
     */
    public void showHome() {
        getContentPane().removeAll();
        add(createWelcomePanel(), BorderLayout.CENTER);
        revalidate();
        repaint();
        setTitle("Billing System");
    }


    private void showModuleStub(String moduleName) {
        getContentPane().removeAll();

        // use module palette
        Color modColor = getModuleColor(moduleName);
        GradientPanel stub = new GradientPanel(UIConstants.adjustBrightness(modColor, 0.7f), modColor);
        stub.setLayout(new BorderLayout());
        stub.setBorder(BorderFactory.createEmptyBorder(48, 48, 48, 48));

        JLabel titleLabel = new JLabel(moduleName, SwingConstants.CENTER);
        titleLabel.setFont(UIConstants.TITLE_FONT.deriveFont(24f));
        titleLabel.setForeground(Color.WHITE);
        stub.add(titleLabel, BorderLayout.NORTH);

        JLabel message = new JLabel("Coming soon", SwingConstants.CENTER);
        message.setFont(UIConstants.UI_FONT.deriveFont(16f));
        message.setForeground(Color.WHITE);
        stub.add(message, BorderLayout.CENTER);

        add(stub, BorderLayout.CENTER);
        revalidate();
        repaint();
        setTitle("Billing System - " + moduleName);
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