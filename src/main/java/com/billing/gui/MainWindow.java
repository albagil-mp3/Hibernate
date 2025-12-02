package com.billing.gui;

import com.billing.service.ClientService;
import com.billing.util.HibernateUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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
        
        // File menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setFont(new Font("Calibri", Font.PLAIN, 12));
        
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.setFont(new Font("Calibri", Font.PLAIN, 12));
        exitMenuItem.addActionListener(e -> exitApplication());
        fileMenu.add(exitMenuItem);
        
        // Clients menu
        JMenu clientsMenu = new JMenu("Clients");
        clientsMenu.setFont(new Font("Calibri", Font.PLAIN, 12));
        
        JMenuItem manageClientsMenuItem = new JMenuItem("Manage Clients");
        manageClientsMenuItem.setFont(new Font("Calibri", Font.PLAIN, 12));
        manageClientsMenuItem.addActionListener(e -> showClientManagement());
        clientsMenu.add(manageClientsMenuItem);
        
        // Help menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setFont(new Font("Calibri", Font.PLAIN, 12));
        
        JMenuItem aboutMenuItem = new JMenuItem("About");
        aboutMenuItem.setFont(new Font("Calibri", Font.PLAIN, 12));
        aboutMenuItem.addActionListener(e -> showAbout());
        helpMenu.add(aboutMenuItem);
        
        menuBar.add(fileMenu);
        menuBar.add(clientsMenu);
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Add welcome panel initially
        add(createWelcomePanel(), BorderLayout.CENTER);
    }
    
    private JPanel createWelcomePanel() {
        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        
        // Title
        JLabel titleLabel = new JLabel("Billing System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Calibri", Font.BOLD, 24));
        welcomePanel.add(titleLabel, BorderLayout.NORTH);
        
        // Content
        JPanel contentPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
        
        JLabel welcomeLabel = new JLabel("Welcome to the Client Management System", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Calibri", Font.PLAIN, 16));
        
        JLabel descriptionLabel = new JLabel("Use the menu to access client management features", SwingConstants.CENTER);
        descriptionLabel.setFont(new Font("Calibri", Font.PLAIN, 14));
        
        JButton startButton = new JButton("Start Managing Clients");
        startButton.setFont(new Font("Calibri", Font.BOLD, 14));
        startButton.setPreferredSize(new Dimension(250, 40));
        startButton.addActionListener(e -> showClientManagement());
        
        contentPanel.add(welcomeLabel);
        contentPanel.add(descriptionLabel);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(startButton);
        contentPanel.add(buttonPanel);
        
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
        setSize(1000, 700);
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null); // Center on screen
        
        // Set icon (placeholder - can be replaced with actual icon)
        try {
            // ImageIcon icon = new ImageIcon(getClass().getResource("/icon.png"));
            // setIconImage(icon.getImage());
        } catch (Exception e) {
            // Icon not found, continue without icon
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
            "Version 1.0\n\n" +
            "Java Application using Hibernate ORM and MySQL\n" +
            "Developed for client management in billing systems\n\n" +
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