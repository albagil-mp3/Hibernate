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
import javax.swing.Icon;
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
import com.billing.service.ArticleService;
import com.billing.util.HibernateUtil;

/**
 * Main application window for the Billing System
 * Provides navigation to different modules of the application
 */
public class MainWindow extends JFrame {
    
    private ClientService clientService;
    private ArticleService articleService;
    private ClientManagementPanel clientManagementPanel;
    private ArticleManagementPanel articleManagementPanel;

    public MainWindow() {
        initializeServices();
        initializeComponents();
        setupEventHandlers();
        configureWindow();
    }

    private void initializeServices() {
        clientService = new ClientService();
        articleService = new ArticleService();
        
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
        setLayout(new BorderLayout());

        // Create menu bar
        JMenuBar menuBar = createMenuBar();
        setJMenuBar(menuBar);

        // Initialize client management panel
        clientManagementPanel = new ClientManagementPanel(clientService);
        
        // Initialize article management panel
        articleManagementPanel = new ArticleManagementPanel(articleService);

        // Start with welcome panel
        add(createWelcomePanel(), BorderLayout.CENTER);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(Color.WHITE);

        // Home menu
        JMenu homeMenu = new JMenu("Home");
        styleMenu(homeMenu);
        homeMenu.setIcon(UIConstants.loadIcon("/icons/home-dark.png", 12));
        homeMenu.setForeground(UIConstants.MODULE_CLIENTS_TEXT);
        homeMenu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showHome();
            }
        });

        JMenu clientsMenu = new JMenu("Clients");
        styleMenu(clientsMenu);
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

        JMenu productsMenu = new JMenu("Articles");
        styleMenu(productsMenu);
        productsMenu.setIcon(UIConstants.loadIcon("/icons/products-dark.png", 12));
        productsMenu.setForeground(UIConstants.MODULE_PRODUCTS_TEXT);
        productsMenu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showArticleManagement();
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

        return menuBar;
    }

    private void styleMenu(JMenu menu) {
        menu.setOpaque(true);
        menu.setBackground(UIConstants.MODULE_CLIENTS_BG);
        menu.setForeground(UIConstants.MODULE_CLIENTS_TEXT);
        menu.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        menu.setFont(UIConstants.UI_FONT.deriveFont(Font.BOLD, 13f));
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
    
    private void showArticleManagement() {
        getContentPane().removeAll();
        // Wrap the article management panel in a module-colored gradient
        GradientPanel wrapper = new GradientPanel(
            UIConstants.adjustBrightness(UIConstants.MODULE_PRODUCTS_BG, 0.85f),
            UIConstants.MODULE_PRODUCTS_BG);
        wrapper.setLayout(new BorderLayout());
        wrapper.add(articleManagementPanel, BorderLayout.CENTER);
        add(wrapper, BorderLayout.CENTER);
        revalidate();
        repaint();
        setTitle("Billing System - Article Management");
    }

    private JPanel createWelcomePanel() {
        GradientPanel welcomePanel = new GradientPanel(
            UIConstants.adjustBrightness(UIConstants.MODULE_CLIENTS_BG, 0.5f),
            UIConstants.MODULE_CLIENTS_BG);
        welcomePanel.setLayout(new BorderLayout());

        // Header section
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 20, 40));

        JLabel welcomeLabel = new JLabel("Billing System Dashboard", SwingConstants.CENTER);
        welcomeLabel.setFont(UIConstants.TITLE_FONT.deriveFont(28f));
        welcomeLabel.setForeground(Color.WHITE);
        headerPanel.add(welcomeLabel, BorderLayout.CENTER);

        JLabel subtitleLabel = new JLabel("Integral Billing System for Businesses", SwingConstants.CENTER);
        subtitleLabel.setFont(UIConstants.UI_FONT.deriveFont(18f));
        subtitleLabel.setForeground(UIConstants.adjustBrightness(Color.WHITE, 0.8f));
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);

        welcomePanel.add(headerPanel, BorderLayout.NORTH);

        // Content section
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));

        JPanel modulesGrid = new JPanel(new GridLayout(2, 2, 20, 20));
        modulesGrid.setOpaque(false);

        JButton clientsBtn = UIConstants.createModuleButton("CLIENTS",
            UIConstants.loadIcon("/icons/clients.png", 56),
            UIConstants.MODULE_CLIENTS_TEXT,
            Color.WHITE,
            UIConstants.MODULE_CLIENTS_ICON,
            UIConstants.MODULE_CLIENTS_BORDER);
        clientsBtn.addActionListener(e -> showClientManagement());

        JButton invoicesBtn = UIConstants.createModuleButton("INVOICES",
            UIConstants.loadIcon("/icons/invoices.png", 56),
            UIConstants.MODULE_INVOICES_TEXT,
            Color.WHITE,
            UIConstants.MODULE_INVOICES_ICON,
            UIConstants.MODULE_INVOICES_BORDER);
        invoicesBtn.addActionListener(e -> showModuleStub("Invoices"));

        JButton productsBtn = UIConstants.createModuleButton("ARTICLES",
            UIConstants.loadIcon("/icons/products.png", 56),
            UIConstants.MODULE_PRODUCTS_TEXT,
            Color.WHITE,
            UIConstants.MODULE_PRODUCTS_ICON,
            UIConstants.MODULE_PRODUCTS_BORDER);
        productsBtn.addActionListener(e -> showArticleManagement());

        JButton suppliersBtn = UIConstants.createModuleButton("SUPPLIERS",
            UIConstants.loadIcon("/icons/suppliers.png", 56),
            UIConstants.MODULE_SUPPLIERS_TEXT,
            Color.WHITE,
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
        
        // Set minimum and preferred size for when window is restored
        setMinimumSize(new Dimension(1000, 700));
        setPreferredSize(new Dimension(1400, 900));
        setSize(1400, 900); // Default size when restored
        
        // Center the window on screen
        setLocationRelativeTo(null);
        
        // Start maximized
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setTitle("Billing System");
        
        // Set window icon
        Icon icon = UIConstants.loadIcon("/icon.png", 32);
        if (icon != null && icon instanceof ImageIcon) {
            setIconImage(((ImageIcon) icon).getImage());
        }
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
        
        JLabel subLabel = new JLabel("This module is not yet implemented", SwingConstants.CENTER);
        subLabel.setFont(UIConstants.UI_FONT.deriveFont(16f));
        subLabel.setForeground(UIConstants.adjustBrightness(Color.BLACK, 0.8f));

        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 0, 16));
        centerPanel.setOpaque(false);
        centerPanel.add(titleLabel);
        centerPanel.add(subLabel);

        stub.add(centerPanel, BorderLayout.CENTER);

        add(stub, BorderLayout.CENTER);
        revalidate();
        repaint();
        setTitle("Billing System - " + moduleName);
    }

    private Color getModuleColor(String moduleName) {
        if (moduleName == null) return UIConstants.MODULE_CLIENTS_BG;
        return switch (moduleName.toLowerCase()) {
            case "clients" -> UIConstants.MODULE_CLIENTS_BG;
            case "products", "articles" -> UIConstants.MODULE_PRODUCTS_BG;
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

    /**
     * Custom JPanel with gradient background
     */
    public static class GradientPanel extends JPanel {
        private final Color startColor;
        private final Color endColor;

        public GradientPanel(Color startColor, Color endColor) {
            this.startColor = startColor;
            this.endColor = endColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            int w = getWidth();
            int h = getHeight();
            GradientPaint gradient = new GradientPaint(0, 0, startColor, 0, h, endColor);
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, w, h);
        }
    }
}