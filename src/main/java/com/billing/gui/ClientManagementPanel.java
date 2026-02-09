package com.billing.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.billing.entity.Client;
import com.billing.service.ClientService;


/**
 * Panel for managing client operations (CRUD)
 * Provides table view with search functionality and form dialogs
 */
public class ClientManagementPanel extends JPanel {
    
    private final ClientService clientService;
    private JTable clientTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> sortComboBox;
    
    // Button references
    private JButton searchButton;
    private JButton clearButton;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton viewButton;
    private JButton refreshButton;
    
    // Column names for the table
    private final String[] columnNames = {
        "ID", "Code", "Name", "DNI", "Address", "City", "Province", 
        "Postal Code", "Fixed Phone", "Mobile Phone", "Email", 
        "Website", "Payment Method", "Credit Limit", "Active"
    };
    
    public ClientManagementPanel(ClientService clientService) {
        this.clientService = clientService;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadClients();
    }
    
    private void initializeComponents() {
        // Search components
        searchField = new JTextField(20);
        searchField.setFont(UIConstants.UI_FONT);
        
        String[] sortOptions = {"Sort by ID", "Sort by DNI", "Sort by Name"};
        sortComboBox = new JComboBox<>(sortOptions);
        sortComboBox.setFont(UIConstants.UI_FONT);
        sortComboBox.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // Initialize Add and Refresh buttons using module palette
        addButton = UIConstants.createSuccessButton("ADD",
            UIConstants.loadIcon("/icons/add.png", 16));
        addButton.setForeground(Color.WHITE);

        refreshButton = UIConstants.createSecondaryButton("REFRESH",
            UIConstants.loadIcon("/icons/refresh.png", 16));
        refreshButton.setBorder(null);
        refreshButton.setForeground(Color.WHITE);
        
        // Table model
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                // Ensure proper class types for each column
                return switch (columnIndex) {
                    case 0 -> Integer.class;        // ID
                    case 13 -> BigDecimal.class;    // Credit Limit
                    case 14 -> String.class;        // Active (Yes/No)
                    default -> String.class;        // All other columns are String
                }; 
            }
        };
        
        clientTable = new JTable(tableModel);
        clientTable.setFont(UIConstants.UI_FONT);
        clientTable.getTableHeader().setFont(UIConstants.UI_FONT.deriveFont(Font.BOLD));
        clientTable.getTableHeader().setOpaque(true);
        clientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        clientTable.setRowHeight(28);
        clientTable.getTableHeader().setReorderingAllowed(false);
        clientTable.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        clientTable.getTableHeader().setCursor(Cursor.getDefaultCursor()); // Regular cursor for header
        
        // Custom header renderer without sort arrows
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setFont(UIConstants.UI_FONT.deriveFont(Font.BOLD));
                label.setOpaque(true);
                label.setBackground(UIConstants.MODULE_CLIENTS_TEXT);
                label.setForeground(Color.WHITE);
                label.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
                return label;
            }
        };
        clientTable.getTableHeader().setDefaultRenderer(headerRenderer);
        
        // Disable table sorting completely
        clientTable.setAutoCreateRowSorter(false);
        
        // Set column widths
        setColumnWidths();
        
        // Alternating row colors and selection styling
        DefaultTableCellRenderer unifiedRenderer = new DefaultTableCellRenderer() {
            private final Color EVEN = Color.WHITE;
            private final Color ODD = UIConstants.MODULE_CLIENTS_BG;
            
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                          boolean hasFocus, int row, int column) {
                JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (isSelected) {
                    c.setBackground(UIConstants.MODULE_CLIENTS_ICON);
                    c.setForeground(Color.WHITE);
                } else {
                    c.setBackground(row % 2 == 0 ? EVEN : ODD);
                    c.setForeground(new Color(0x212121));
                }
                return c;
            }
        };
        
        // Apply same renderer for all column types
        clientTable.setDefaultRenderer(Object.class, unifiedRenderer);
        clientTable.setDefaultRenderer(Number.class, unifiedRenderer);
        clientTable.setDefaultRenderer(Integer.class, unifiedRenderer);
        clientTable.setDefaultRenderer(BigDecimal.class, unifiedRenderer);
        
        // Buttons (top search buttons)
        searchButton = UIConstants.createSecondaryButton("SEARCH",
            UIConstants.loadIcon("/icons/search.png", 16));

        clearButton = UIConstants.createSecondaryButton("CLEAR",
            UIConstants.loadIcon("/icons/clear.png", 16));
    }
    
    private void setColumnWidths() {
        int[] columnWidths = {50, 90, 150, 80, 200, 120, 100, 80, 90, 90, 200, 150, 100, 100, 60};
        
        for (int i = 0; i < columnWidths.length && i < clientTable.getColumnCount(); i++) {
            clientTable.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(UIConstants.MODULE_CLIENTS_BG);
        
        // Top panel with search and controls (includes title)
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);
        
        // Center panel with table
        JScrollPane tableScrollPane = new JScrollPane(clientTable);
        tableScrollPane.setPreferredSize(new Dimension(0, 400));
        // enhanced, more dynamic mouse-wheel scrolling via UIConstants
        UIConstants.enhanceScroll(tableScrollPane);
        add(tableScrollPane, BorderLayout.CENTER);
        
        // Bottom panel with buttons
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(UIConstants.MODULE_CLIENTS_BG);
        
        // Header bar title
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(0, 80));
        headerPanel.setBackground(UIConstants.MODULE_CLIENTS_TEXT);

        JLabel titleLabel = new JLabel("Client Management", SwingConstants.CENTER);
        titleLabel.setFont(UIConstants.TITLE_FONT.deriveFont(24f));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));

        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        topPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Search and controls panel - search on left, buttons on right
        JPanel controlsPanel = new JPanel(new BorderLayout());
        controlsPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));
        controlsPanel.setOpaque(false);
        
        // Left side: Search and Sort
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setOpaque(false);
        
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(UIConstants.UI_FONT);
        searchLabel.setForeground(new Color(0x212121));
        
        leftPanel.add(searchLabel);
        leftPanel.add(searchField);
        leftPanel.add(searchButton);
        leftPanel.add(clearButton);
        leftPanel.add(Box.createHorizontalStrut(20));
        leftPanel.add(new JLabel("Sort:"));
        leftPanel.add(sortComboBox);
        
        // Right side: Add Client and Refresh
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        
        rightPanel.add(addButton);
        rightPanel.add(refreshButton);
        
        controlsPanel.add(leftPanel, BorderLayout.WEST);
        controlsPanel.add(rightPanel, BorderLayout.EAST);
        
        topPanel.add(controlsPanel, BorderLayout.CENTER);
        
        return topPanel;
    }
    
    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        bottomPanel.setBackground(UIConstants.MODULE_CLIENTS_BG);
        
        viewButton = UIConstants.createSecondaryButton("DETAILS",
            UIConstants.loadIcon("/icons/details.png", 16));
        viewButton.setForeground(Color.WHITE);
        
        editButton = UIConstants.createPrimaryButton("EDIT",
            UIConstants.loadIcon("/icons/edit.png", 16));
        editButton.setForeground(Color.WHITE);
        
        deleteButton = UIConstants.createDangerButton("DELETE",
            UIConstants.loadIcon("/icons/delete.png", 16));
        deleteButton.setForeground(Color.WHITE);

        bottomPanel.add(viewButton);
        bottomPanel.add(editButton);
        bottomPanel.add(deleteButton);
        
        return bottomPanel;
    }
    
    private void setupEventHandlers() {
        // Search functionality
        searchField.addActionListener(e -> performSearch());
        
        // Search buttons
        searchButton.addActionListener(e -> performSearch());
        clearButton.addActionListener(e -> clearSearch());
        
        // Sort combo box - apply sorting when selection changes
        sortComboBox.addActionListener(e -> applySorting());
        
        // Top panel buttons
        addButton.addActionListener(e -> addClient());
        refreshButton.addActionListener(e -> loadClients());
        
        // Bottom panel buttons
        editButton.addActionListener(e -> editClient());
        deleteButton.addActionListener(e -> deleteClient());
        viewButton.addActionListener(e -> viewClientDetails());
        refreshButton.addActionListener(e -> loadClients());
        
        // Double-click to edit / view
        clientTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    viewClientDetails();
                }
            }
        });
    }
    
    private void loadClients() {
        try {
            List<Client> clients = clientService.getAllClients();
            updateTable(clients);
        } catch (IllegalArgumentException iae) {
            JOptionPane.showMessageDialog(this,
                iae.getMessage(),
                "Warning",
                JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException re) {
            JOptionPane.showMessageDialog(this,
                "Error loading clients: " + re.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateTable(List<Client> clients) {
        tableModel.setRowCount(0); // Clear existing rows
        
        for (Client client : clients) {
            Object[] rowData = {
                client.getId(),
                client.getCode() != null ? client.getCode() : "",
                client.getName(),
                client.getDni(),
                client.getAddress(),
                client.getCity(),
                client.getProvince() != null ? client.getProvince().getDisplayName() : "",
                client.getPostalCode() != null ? client.getPostalCode() : "",
                client.getFixedPhone() != null ? client.getFixedPhone() : "",
                client.getMobilePhone() != null ? client.getMobilePhone() : "",
                client.getEmail() != null ? client.getEmail() : "",
                client.getWebsite() != null ? client.getWebsite() : "",
                client.getPaymentMethod() != null ? client.getPaymentMethod().toString() : "",
                client.getCreditLimit() != null ? client.getCreditLimit() : BigDecimal.ZERO,
                client.getActive() != null ? (client.getActive() ? "Yes" : "No") : "No"
            };
            tableModel.addRow(rowData);
        }
    }
    
    private void performSearch() {
        String searchTerm = searchField.getText().trim();
        try {
            List<Client> clients = clientService.searchClients(searchTerm);
            updateTable(clients);
        } catch (IllegalArgumentException iae) {
            JOptionPane.showMessageDialog(this,
                iae.getMessage(),
                "Warning",
                JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException re) {
            JOptionPane.showMessageDialog(this,
                "Error searching clients: " + re.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearSearch() {
        searchField.setText("");
        loadClients();
    }
    
    /**
     * Apply sorting based on the selected option in the combo box
     */
    private void applySorting() {
        try {
            String selectedSort = (String) sortComboBox.getSelectedItem();
            List<Client> clients;
            
            clients = switch (selectedSort) {
                case "Sort by ID" -> clientService.getAllClientsOrderedById();
                case "Sort by DNI" -> clientService.getAllClientsOrderedByDni();
                case "Sort by Name" -> clientService.getAllClientsOrderedByName();
                default -> clientService.getAllClients();
            };
            
            updateTable(clients);
            
        } catch (IllegalArgumentException iae) {
            JOptionPane.showMessageDialog(this,
                iae.getMessage(),
                "Warning",
                JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException re) {
            JOptionPane.showMessageDialog(this,
                "Error sorting clients: " + re.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void addClient() {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        ClientFormDialog dialog = new ClientFormDialog(
            parent,
            "Add New Client",
            null,
            clientService
        );
        // ensure dialog is application-modal and centered over parent
        dialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            loadClients(); // Refresh table
        }
    }

    /**
     * Public wrapper so other UI components can open the Add Client dialog.
     */
    public void showAddClient() {
        addClient();
    }
    
    private void editClient() {
        int selectedRow = clientTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select a client to edit.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int modelRow = clientTable.convertRowIndexToModel(selectedRow);
            Integer clientId = (Integer) tableModel.getValueAt(modelRow, 0);
            Client client = clientService.findClientById(clientId);
            
            if (client != null) {
                ClientFormDialog dialog = new ClientFormDialog(
                    (Frame) SwingUtilities.getWindowAncestor(this),
                    "Edit Client",
                    client,
                    clientService
                );
                dialog.setVisible(true);
                
                if (dialog.isConfirmed()) {
                    loadClients(); // Refresh table
                }
            }
        } catch (IllegalArgumentException iae) {
            JOptionPane.showMessageDialog(this,
                iae.getMessage(),
                "Warning",
                JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException re) {
            JOptionPane.showMessageDialog(this,
                "Error editing client: " + re.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteClient() {
        int selectedRow = clientTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select a client to delete.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int modelRow = clientTable.convertRowIndexToModel(selectedRow);
            Integer clientId = (Integer) tableModel.getValueAt(modelRow, 0);
            String clientName = (String) tableModel.getValueAt(modelRow, 2);
            
            int option = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete client '" + clientName + "'?\n" +
                "This action cannot be undone.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (option == JOptionPane.YES_OPTION) {
                clientService.deleteClient(clientId);
                loadClients(); // Refresh table
                JOptionPane.showMessageDialog(this,
                    "Client deleted successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IllegalArgumentException iae) {
            JOptionPane.showMessageDialog(this,
                iae.getMessage(),
                "Warning",
                JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException re) {
            JOptionPane.showMessageDialog(this,
                "Error deleting client: " + re.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void viewClientDetails() {
        int selectedRow = clientTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select a client to view.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int modelRow = clientTable.convertRowIndexToModel(selectedRow);
            Integer clientId = (Integer) tableModel.getValueAt(modelRow, 0);
            Client client = clientService.findClientById(clientId);
            
            if (client != null) {
                ClientDetailsDialog dialog = new ClientDetailsDialog(
                    (Frame) SwingUtilities.getWindowAncestor(this),
                    client
                );
                dialog.setVisible(true);
            }
        } catch (IllegalArgumentException iae) {
            JOptionPane.showMessageDialog(this,
                iae.getMessage(),
                "Warning",
                JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException re) {
            JOptionPane.showMessageDialog(this,
                "Error viewing client details: " + re.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}