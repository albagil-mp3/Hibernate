package com.billing.gui;

import com.billing.entity.Client;
import com.billing.service.ClientService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.List;


/**
 * Panel for managing client operations (CRUD)
 * Provides table view with search functionality and form dialogs
 */
public class ClientManagementPanel extends JPanel {
    
    private ClientService clientService;
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
        "ID", "Name", "DNI", "Address", "City", "Province", 
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
        
        // Table model
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                // Ensure proper class types for each column
                switch (columnIndex) {
                    case 0: return Integer.class;             // ID
                    case 12: return BigDecimal.class;         // Credit Limit
                    case 13: return String.class;             // Active (Yes/No)
                    default: return String.class;            // All other columns are String
                }
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
                label.setBackground(UIConstants.PURPLE_MAIN);
                label.setForeground(UIConstants.BONE_WHITE);
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
            private final Color EVEN = UIConstants.BONE_WHITE;
            private final Color ODD = UIConstants.LAVENDER_LIGHT;
            
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                          boolean hasFocus, int row, int column) {
                JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (isSelected) {
                    c.setBackground(UIConstants.TURQUOISE_ACCENT);
                    c.setForeground(UIConstants.BONE_WHITE);
                } else {
                    c.setBackground(row % 2 == 0 ? EVEN : ODD);
                    c.setForeground(UIConstants.ANTHRACITE);
                }
                return c;
            }
        };
        
        // Apply same renderer for all column types
        clientTable.setDefaultRenderer(Object.class, unifiedRenderer);
        clientTable.setDefaultRenderer(Number.class, unifiedRenderer);
        clientTable.setDefaultRenderer(Integer.class, unifiedRenderer);
        clientTable.setDefaultRenderer(BigDecimal.class, unifiedRenderer);
        
        // Buttons (top search buttons) - ensure background is applied by forcing content area fill
        searchButton = new JButton("Search");
        UIConstants.styleButton(searchButton, UIConstants.PURPLE_MAIN, UIConstants.BONE_WHITE, new Dimension(120, 30));

        clearButton = new JButton("Clear");
        UIConstants.styleButton(clearButton, UIConstants.PURPLE_DARK, UIConstants.BONE_WHITE, new Dimension(120, 30));
    }
    
    private void setColumnWidths() {
        int[] columnWidths = {50, 150, 80, 200, 120, 100, 80, 90, 90, 200, 150, 100, 100, 60};
        
        for (int i = 0; i < columnWidths.length && i < clientTable.getColumnCount(); i++) {
            clientTable.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(UIConstants.LAVENDER_LIGHT);
        
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
        topPanel.setBackground(UIConstants.LAVENDER_LIGHT);
        
        // Title
        JLabel titleLabel = new JLabel("Client Management", SwingConstants.CENTER);
        titleLabel.setFont(UIConstants.TITLE_FONT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        titleLabel.setForeground(UIConstants.ANTHRACITE);
        topPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));
        searchPanel.setOpaque(false);
        
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(UIConstants.UI_FONT);
        searchLabel.setForeground(UIConstants.ANTHRACITE);
        
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(clearButton);
        searchPanel.add(Box.createHorizontalStrut(20));
        searchPanel.add(new JLabel("Sort:"));
        searchPanel.add(sortComboBox);
        
        topPanel.add(searchPanel, BorderLayout.CENTER);
        
        return topPanel;
    }
    
    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        bottomPanel.setBackground(UIConstants.LAVENDER_LIGHT);
        
        addButton = new JButton("Add Client");
        editButton = new JButton("Edit Client");
        deleteButton = new JButton("Delete Client");
        viewButton = new JButton("View Details");
        refreshButton = new JButton("Refresh");
        
        // Set font and style for all buttons
        Font buttonFont = UIConstants.UI_FONT;
        JButton[] buttons = {addButton, editButton, deleteButton, viewButton, refreshButton};
        for (JButton btn : buttons) {
            btn.setFont(buttonFont);
            // primary action color for most, adjust as needed
            UIConstants.styleButton(btn, UIConstants.PURPLE_MAIN, UIConstants.BONE_WHITE, new Dimension(120, 30));
             bottomPanel.add(btn);
        }
        
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
        
        // Bottom panel buttons
        addButton.addActionListener(e -> addClient());
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
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading clients: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateTable(List<Client> clients) {
        tableModel.setRowCount(0); // Clear existing rows
        
        for (Client client : clients) {
            Object[] rowData = {
                client.getId(),
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
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error searching clients: " + e.getMessage(),
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
            
            switch (selectedSort) {
                case "Sort by ID":
                    clients = clientService.getAllClientsOrderedById();
                    break;
                case "Sort by DNI":
                    clients = clientService.getAllClientsOrderedByDni();
                    break;
                case "Sort by Name":
                    clients = clientService.getAllClientsOrderedByName();
                    break;
                default:
                    clients = clientService.getAllClients();
            }
            
            updateTable(clients);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error sorting clients: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void addClient() {
        ClientFormDialog dialog = new ClientFormDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            "Add New Client",
            null,
            clientService
        );
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            loadClients(); // Refresh table
        }
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
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error editing client: " + e.getMessage(),
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
            String clientName = (String) tableModel.getValueAt(modelRow, 1);
            
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
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error deleting client: " + e.getMessage(),
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
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error viewing client details: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}