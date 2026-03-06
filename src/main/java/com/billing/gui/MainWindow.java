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
import javax.swing.JMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.billing.service.ClientService;
import com.billing.service.DocumentQueryService;
import com.billing.service.ItemService;
import com.billing.service.SalesService;
import com.billing.service.DeliveryService;
import com.billing.service.SupplierService;
import com.billing.util.HibernateUtil;
import com.billing.config.AppConfig;
import com.billing.config.ConfigLoader;
import com.billing.io.ClientImporter;
import com.billing.io.SupplierImporter;
import com.billing.io.ItemImporter;
import com.billing.service.ExportService;
import com.billing.tx.HibernateTransactionManager;
import com.billing.tx.TransactionManager;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.JFileChooser;

/**
 * Main application window for the Billing System
 * Provides navigation to different modules of the application
 */
public class MainWindow extends JFrame {
    
    private ClientService clientService;
    private ItemService itemService;
    private SupplierService supplierService;
    private SalesService salesService;
    private DeliveryService deliveryService;
    private ClientManagementPanel clientManagementPanel;
    private ItemManagementPanel itemManagementPanel;
    private SupplierManagementPanel supplierManagementPanel;
    private DeliveryManagementPanel deliveryManagementPanel;
    private OrderManagementPanel orderManagementPanel;
    private InvoiceTrackingPanel invoiceTrackingPanel;
    private final AppConfig appConfig;
    private final TransactionManager tx = new HibernateTransactionManager();
    private final ExportService exportService = new ExportService();

    public MainWindow() {
        this.appConfig = ConfigLoader.load();
        initializeServices();
        initializeComponents();
        setupEventHandlers();
        configureWindow();
    }

    private void initializeServices() {
        clientService = new com.billing.service.ClientService();
        itemService = new ItemService();
        supplierService = new SupplierService();
        salesService = new SalesService();
        deliveryService = new DeliveryService(salesService);

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
        
        // Initialize item management panel
        itemManagementPanel = new ItemManagementPanel(itemService);

        // Initialize supplier management panel
        supplierManagementPanel = new SupplierManagementPanel(supplierService);
        
        // Initialize delivery management panel
        deliveryManagementPanel = new DeliveryManagementPanel(deliveryService);

        // Initialize order management panel
        orderManagementPanel = new OrderManagementPanel(deliveryService);
        
        // Initialize invoice management panel
        invoiceTrackingPanel = new InvoiceTrackingPanel(new DocumentQueryService(), exportService);

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
        homeMenu.setForeground(Color.BLACK);
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
        
        JMenu ordersMenu = new JMenu("Orders");
        styleMenu(ordersMenu);
        ordersMenu.setIcon(UIConstants.loadIcon("/icons/orders-dark.png", 12));
        ordersMenu.setForeground(UIConstants.MODULE_ORDERS_TEXT);
        ordersMenu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showOrderManagement();
            }
        });

        JMenu invoiceMenu = new JMenu("Invoices");
        styleMenu(invoiceMenu);
        invoiceMenu.setIcon(UIConstants.loadIcon("/icons/invoice-dark.png", 12));
        invoiceMenu.setForeground(UIConstants.MODULE_INVOICES_TEXT);
        invoiceMenu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showInvoiceTracking();
            }
        });

        JMenu deliveryMenu = new JMenu("Delivery Notes");
        styleMenu(deliveryMenu);
        deliveryMenu.setIcon(UIConstants.loadIcon("/icons/delivery-dark.png", 12));
        deliveryMenu.setForeground(UIConstants.MODULE_DELIVERY_TEXT);
        deliveryMenu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showDeliveryManagement();
            }
        });

        JMenu ItemsMenu = new JMenu("Items");
        styleMenu(ItemsMenu);
        ItemsMenu.setIcon(UIConstants.loadIcon("/icons/items-dark.png", 12));
        ItemsMenu.setForeground(UIConstants.MODULE_ITEMS_TEXT);
        ItemsMenu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showItemManagement();
            }
        });

        JMenu suppliersMenu = new JMenu("Suppliers");
        styleMenu(suppliersMenu);
        suppliersMenu.setIcon(UIConstants.loadIcon("/icons/suppliers-dark.png", 12));
        suppliersMenu.setForeground(UIConstants.MODULE_SUPPLIERS_TEXT);
        suppliersMenu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showSupplierManagement();
            }
        });

        menuBar.add(homeMenu);
        menuBar.add(clientsMenu);
        menuBar.add(suppliersMenu);
        menuBar.add(ItemsMenu);
        menuBar.add(ordersMenu);
        menuBar.add(deliveryMenu);
        menuBar.add(invoiceMenu);
     

        // Tools menu for maintenance actions
        JMenu toolsMenu = new JMenu("Tools");
        styleMenu(toolsMenu);
        toolsMenu.setIcon(UIConstants.loadIcon("/icons/tools-dark.png", 12));
        toolsMenu.setForeground(Color.BLACK);
        JMenuItem regenCodesItem = new JMenuItem("Regenerate Codes");
        regenCodesItem.addActionListener(e -> {
            int opt = JOptionPane.showConfirmDialog(this,
                    "Regenerate codes for all items, suppliers and clients? This will update the database.",
                    "Confirm Regenerate Codes",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (opt == JOptionPane.YES_OPTION) {
                // Run regeneration and show results
                int itemsUpdated = 0;
                int clientsUpdated = 0;
                int suppliersUpdated = 0;
                try {
                    itemsUpdated = itemService.regenerateAllItemCodes();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error regenerating item codes: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
                try {
                    clientsUpdated = clientService.regenerateAllClientCodes();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error regenerating client codes: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
                try {
                    suppliersUpdated = supplierService.regenerateAllSupplierCodes();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error regenerating supplier codes: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
                JOptionPane.showMessageDialog(this, "Regeneration completed.\nItems updated: " + itemsUpdated + "\nClients updated: " + clientsUpdated + "\nSuppliers updated: " + suppliersUpdated,
                        "Regeneration Completed", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        toolsMenu.add(regenCodesItem);

        JMenuItem importClientsJson = new JMenuItem("Import Clients (JSON)");
        importClientsJson.addActionListener(e -> importClients());
        toolsMenu.add(importClientsJson);

        JMenuItem importSuppliersJson = new JMenuItem("Import Suppliers (JSON)");
        importSuppliersJson.addActionListener(e -> importSuppliers());
        toolsMenu.add(importSuppliersJson);

        JMenuItem importItemsJson = new JMenuItem("Import Items (JSON)");
        importItemsJson.addActionListener(e -> importItems());
        toolsMenu.add(importItemsJson);
        menuBar.add(toolsMenu);

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
    
    private void showItemManagement() {
        getContentPane().removeAll();
        // Wrap the item management panel in a module-colored gradient
        GradientPanel wrapper = new GradientPanel(
            UIConstants.adjustBrightness(UIConstants.MODULE_ITEMS_BG, 0.85f),
            UIConstants.MODULE_ITEMS_BG);
        wrapper.setLayout(new BorderLayout());
        wrapper.add(itemManagementPanel, BorderLayout.CENTER);
        add(wrapper, BorderLayout.CENTER);
        revalidate();
        repaint();
        setTitle("Billing System - Item Management");
    }

    private void showSupplierManagement() {
        getContentPane().removeAll();
        GradientPanel wrapper = new GradientPanel(
            UIConstants.adjustBrightness(UIConstants.MODULE_SUPPLIERS_BG, 0.85f),
            UIConstants.MODULE_SUPPLIERS_BG);
        wrapper.setLayout(new BorderLayout());
        wrapper.add(supplierManagementPanel, BorderLayout.CENTER);
        add(wrapper, BorderLayout.CENTER);
        revalidate();
        repaint();
        setTitle("Billing System - Supplier Management");
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

        JPanel modulesGrid = new JPanel(new GridLayout(2, 3, 20, 20));
        modulesGrid.setOpaque(false);

        JButton clientsBtn = UIConstants.createModuleButton("CLIENTS",
            UIConstants.loadIcon("/icons/clients.png", 56),
            UIConstants.MODULE_CLIENTS_TEXT,
            Color.WHITE,
            UIConstants.MODULE_CLIENTS_ICON,
            UIConstants.MODULE_CLIENTS_BORDER);
        clientsBtn.addActionListener(e -> showClientManagement());

        JButton suppliersBtn = UIConstants.createModuleButton("SUPPLIERS",
            UIConstants.loadIcon("/icons/suppliers.png", 56),
            UIConstants.MODULE_SUPPLIERS_TEXT,
            Color.WHITE,
            UIConstants.MODULE_SUPPLIERS_ICON,
            UIConstants.MODULE_SUPPLIERS_BORDER);
        suppliersBtn.addActionListener(e -> showSupplierManagement());

        JButton itemsBtn = UIConstants.createModuleButton("ITEMS",
            UIConstants.loadIcon("/icons/items.png", 56),
            UIConstants.MODULE_ITEMS_TEXT,
            Color.WHITE,
            UIConstants.MODULE_ITEMS_ICON,
            UIConstants.MODULE_ITEMS_BORDER);
        itemsBtn.addActionListener(e -> showItemManagement());

        JButton ordersBtn = UIConstants.createModuleButton("ORDERS",
            UIConstants.loadIcon("/icons/orders.png", 56),
            UIConstants.MODULE_ORDERS_TEXT,
            Color.WHITE,
            UIConstants.MODULE_ORDERS_ICON,
            UIConstants.MODULE_ORDERS_BORDER);
        ordersBtn.addActionListener(e -> showOrderManagement());

        JButton deliveryBtn = UIConstants.createModuleButton("DELIVERY NOTES",
            UIConstants.loadIcon("/icons/delivery.png", 56),
            UIConstants.MODULE_DELIVERY_TEXT,
            Color.WHITE,
            UIConstants.MODULE_DELIVERY_ICON,
            UIConstants.MODULE_DELIVERY_BORDER);
        deliveryBtn.addActionListener(e -> showDeliveryManagement());

        JButton invoiceBtn = UIConstants.createModuleButton("INVOICES",
            UIConstants.loadIcon("/icons/invoice.png", 56),
            UIConstants.MODULE_INVOICES_TEXT,
            Color.WHITE,
            UIConstants.MODULE_INVOICES_ICON,
            UIConstants.MODULE_INVOICES_BORDER);
        invoiceBtn.addActionListener(e -> showInvoiceTracking());

        modulesGrid.add(clientsBtn);
        modulesGrid.add(suppliersBtn);
        modulesGrid.add(itemsBtn);
        modulesGrid.add(ordersBtn);
        modulesGrid.add(deliveryBtn);
        modulesGrid.add(invoiceBtn);

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
            case "products", "articles" -> UIConstants.MODULE_ITEMS_BG;
            case "suppliers" -> UIConstants.MODULE_SUPPLIERS_BG;
            case "orders", "invoices" -> UIConstants.MODULE_INVOICES_BG;
            case "delivery", "delivery notes", "delivery management", "delivery notes management" -> UIConstants.MODULE_DELIVERY_BG;
            default -> UIConstants.MODULE_CLIENTS_BG;
        };
    }

    private void showDeliveryManagement() {
        getContentPane().removeAll();
        GradientPanel wrapper = new GradientPanel(
            UIConstants.adjustBrightness(UIConstants.MODULE_DELIVERY_BG, 0.85f),
            UIConstants.MODULE_DELIVERY_BG);
        wrapper.setLayout(new BorderLayout());
        deliveryManagementPanel.reloadDeliveries();
        wrapper.add(deliveryManagementPanel, BorderLayout.CENTER);
        add(wrapper, BorderLayout.CENTER);
        revalidate();
        repaint();
        setTitle("Billing System - Delivery Notes Management");
    }

    private void showInvoiceTracking() {
        getContentPane().removeAll();
        GradientPanel wrapper = new GradientPanel(
            UIConstants.adjustBrightness(UIConstants.MODULE_INVOICES_BG, 0.85f),
            UIConstants.MODULE_INVOICES_BG);
        wrapper.setLayout(new BorderLayout());
        wrapper.add(invoiceTrackingPanel, BorderLayout.CENTER);
        add(wrapper, BorderLayout.CENTER);
        revalidate();
        repaint();
        setTitle("Billing System - Invoice Management");
    }

    private void showOrderManagement() {
        getContentPane().removeAll();
        GradientPanel wrapper = new GradientPanel(
            UIConstants.adjustBrightness(UIConstants.MODULE_INVOICES_BG, 0.85f),
            UIConstants.MODULE_INVOICES_BG);
        wrapper.setLayout(new BorderLayout());
        wrapper.add(orderManagementPanel, BorderLayout.CENTER);
        add(wrapper, BorderLayout.CENTER);
        revalidate();
        repaint();
        setTitle("Billing System - Order Management");
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

    private void importClients() {
        try {
            Path importDir = Paths.get(appConfig.getImportDir());
            JFileChooser chooser = new JFileChooser(importDir.toFile());
            int result = chooser.showOpenDialog(this);
            if (result != JFileChooser.APPROVE_OPTION) return;
            Path source = chooser.getSelectedFile().toPath();
            ClientImporter importer = new ClientImporter();
            var dtos = importer.importFromJson(source, Paths.get(appConfig.getBackupDir()));
            com.billing.service.ImportResult r = clientService.importClients(dtos);
            clientManagementPanel.loadClients();
            JOptionPane.showMessageDialog(this,
                    "Import completed.\nImported: " + r.imported + "\nSkipped: " + r.skipped + "\nFailed: " + r.failed,
                    "Import", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Import failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void importSuppliers() {
        try {
            Path importDir = Paths.get(appConfig.getImportDir());
            JFileChooser chooser = new JFileChooser(importDir.toFile());
            int result = chooser.showOpenDialog(this);
            if (result != JFileChooser.APPROVE_OPTION) return;
            Path source = chooser.getSelectedFile().toPath();
            SupplierImporter importer = new SupplierImporter();
            var dtos = importer.importFromJson(source, Paths.get(appConfig.getBackupDir()));
            com.billing.service.ImportResult r = supplierService.importSuppliers(dtos);
            JOptionPane.showMessageDialog(this,
                    "Import completed.\nImported: " + r.imported + "\nSkipped: " + r.skipped + "\nFailed: " + r.failed,
                    "Import", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Import failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void importItems() {
        try {
            Path importDir = Paths.get(appConfig.getImportDir());
            JFileChooser chooser = new JFileChooser(importDir.toFile());
            int result = chooser.showOpenDialog(this);
            if (result != JFileChooser.APPROVE_OPTION) return;
            Path source = chooser.getSelectedFile().toPath();
            ItemImporter importer = new ItemImporter();
            var dtos = importer.importFromJson(source, Paths.get(appConfig.getBackupDir()));
            com.billing.service.ImportResult r = itemService.importItems(dtos);
            itemManagementPanel.reloadItems();
            JOptionPane.showMessageDialog(this,
                    "Import completed.\nImported: " + r.imported + "\nSkipped: " + r.skipped + "\nFailed: " + r.failed,
                    "Import", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Import failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
