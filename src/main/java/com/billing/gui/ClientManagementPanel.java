package com.billing.gui;

import com.billing.entity.Client;
import com.billing.service.ClientService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
    private TableRowSorter<DefaultTableModel> tableSorter;
    
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
        searchField.setFont(new Font("Calibri", Font.PLAIN, 12));
        
        String[] sortOptions = {"Sort by ID", "Sort by DNI", "Sort by Name"};
        sortComboBox = new JComboBox<>(sortOptions);
        sortComboBox.setFont(new Font("Calibri", Font.PLAIN, 12));
        
        // Table
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        
        clientTable = new JTable(tableModel);
        clientTable.setFont(new Font("Calibri", Font.PLAIN, 12));
        clientTable.getTableHeader().setFont(new Font("Calibri", Font.BOLD, 12));
        clientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        clientTable.setRowHeight(25);
        
        // Set column widths
        setColumnWidths();
        
        tableSorter = new TableRowSorter<>(tableModel);
        clientTable.setRowSorter(tableSorter);
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
        
        // Top panel with search and controls (includes title)
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);
        
        // Center panel with table
        JScrollPane tableScrollPane = new JScrollPane(clientTable);
        tableScrollPane.setPreferredSize(new Dimension(0, 400));
        add(tableScrollPane, BorderLayout.CENTER);
        
        // Bottom panel with buttons
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        
        // Title
        JLabel titleLabel = new JLabel("Client Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Calibri", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        topPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));
        
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Calibri", Font.PLAIN, 12));
        
        searchButton = new JButton("Search");
        searchButton.setFont(new Font("Calibri", Font.PLAIN, 12));
        
        clearButton = new JButton("Clear");
        clearButton.setFont(new Font("Calibri", Font.PLAIN, 12));
        
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
        
        addButton = new JButton("Add Client");
        editButton = new JButton("Edit Client");
        deleteButton = new JButton("Delete Client");
        viewButton = new JButton("View Details");
        refreshButton = new JButton("Refresh");
        
        // Set font for all buttons
        Font buttonFont = new Font("Calibri", Font.PLAIN, 12);
        addButton.setFont(buttonFont);
        editButton.setFont(buttonFont);
        deleteButton.setFont(buttonFont);
        viewButton.setFont(buttonFont);
        refreshButton.setFont(buttonFont);
        
        bottomPanel.add(addButton);
        bottomPanel.add(editButton);
        bottomPanel.add(deleteButton);
        bottomPanel.add(viewButton);
        bottomPanel.add(refreshButton);
        
        return bottomPanel;
    }
    
    private void setupEventHandlers() {
        // Search functionality
        searchField.addActionListener(e -> performSearch());
        
        // Search buttons
        searchButton.addActionListener(e -> performSearch());
        clearButton.addActionListener(e -> clearSearch());
        
        // Sort combo box
        sortComboBox.addActionListener(e -> applySorting());
        
        // Bottom panel buttons
        addButton.addActionListener(e -> addClient());
        editButton.addActionListener(e -> editClient());
        deleteButton.addActionListener(e -> deleteClient());
        viewButton.addActionListener(e -> viewClientDetails());
        refreshButton.addActionListener(e -> loadClients());
        
        // Double-click to edit
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
                client.getCreditLimit() != null ? client.getCreditLimit().toString() : "0.00",
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
            // Convert view row to model row
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