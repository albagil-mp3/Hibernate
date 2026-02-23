package com.billing.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.billing.service.DocumentQueryService;
import com.billing.service.ExportService;


/**
 * Panel for tracking invoices
 */
public class InvoiceTrackingPanel extends JPanel {
    
    private final DocumentQueryService documentQueryService;
    private final ExportService exportService;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> sortComboBox;
    private List<DocumentQueryService.DocumentTrackingRow> currentRows = java.util.Collections.emptyList();
    
    // Button references
    private JButton searchButton;
    private JButton clearButton;
    private JButton viewButton;
    private JButton exportButton;
    private JButton refreshButton;
    
    // Column names for the table
    private final String[] columnNames = {
        "Code", "Type", "Date", "Party", "Status", "Subtotal", "Taxes", "Total"
    };
    
    public InvoiceTrackingPanel(DocumentQueryService documentQueryService, ExportService exportService) {
        this.documentQueryService = documentQueryService;
        this.exportService = exportService;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadInvoices();
    }
    
    private void initializeComponents() {
        // Search components
        searchField = new JTextField(20);
        searchField.setFont(UIConstants.UI_FONT);
        
        String[] sortOptions = {"Sort by Code", "Sort by Date", "Sort by Party"};
        sortComboBox = new JComboBox<>(sortOptions);
        sortComboBox.setFont(UIConstants.UI_FONT);
        sortComboBox.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

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
                    case 0 -> String.class;        // Code
                    case 1 -> String.class;        // Type
                    case 2 -> String.class;        // Date
                    case 3 -> String.class;        // Party
                    case 4 -> String.class;        // Status
                    case 5, 6, 7 -> BigDecimal.class; // Amounts
                    default -> String.class;        // All other columns are String
                }; 
            }
        };
        
        table = new JTable(tableModel);
        table.setFont(UIConstants.UI_FONT);
        table.getTableHeader().setFont(UIConstants.UI_FONT.deriveFont(Font.BOLD));
        table.getTableHeader().setOpaque(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(28);
        table.getTableHeader().setReorderingAllowed(false);
        table.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        table.getTableHeader().setCursor(Cursor.getDefaultCursor()); // Regular cursor for header
        
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
                label.setBackground(UIConstants.MODULE_INVOICES_TEXT);
                label.setForeground(Color.WHITE);
                label.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
                return label;
            }
        };
        table.getTableHeader().setDefaultRenderer(headerRenderer);
        
        // Disable table sorting completely
        table.setAutoCreateRowSorter(false);
        
        // Set column widths
        setColumnWidths();
        
        // Alternating row colors and selection styling
        DefaultTableCellRenderer unifiedRenderer = new DefaultTableCellRenderer() {
            private final Color EVEN = Color.WHITE;
            private final Color ODD = UIConstants.MODULE_INVOICES_BG;
            
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                          boolean hasFocus, int row, int column) {
                JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (isSelected) {
                    c.setBackground(UIConstants.MODULE_INVOICES_ICON);
                    c.setForeground(Color.WHITE);
                } else {
                    c.setBackground(row % 2 == 0 ? EVEN : ODD);
                    c.setForeground(new Color(0x212121));
                }
                return c;
            }
        };
        
        // Apply same renderer for all column types
        table.setDefaultRenderer(Object.class, unifiedRenderer);
        table.setDefaultRenderer(Number.class, unifiedRenderer);
        table.setDefaultRenderer(Integer.class, unifiedRenderer);
        table.setDefaultRenderer(BigDecimal.class, unifiedRenderer);
        
        // Buttons (top search buttons)
        searchButton = UIConstants.createSecondaryButton("SEARCH",
            UIConstants.loadIcon("/icons/search.png", 16));

        clearButton = UIConstants.createSecondaryButton("CLEAR",
            UIConstants.loadIcon("/icons/clear.png", 16));
    }
    
    private void setColumnWidths() {
        int[] columnWidths = {110, 110, 110, 220, 130, 110, 110, 110};
        
        for (int i = 0; i < columnWidths.length && i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(UIConstants.MODULE_INVOICES_BG);
        
        // Top panel with search and controls (includes title)
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);
        
        // Center panel with table
        JScrollPane tableScrollPane = new JScrollPane(table);
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
        topPanel.setBackground(UIConstants.MODULE_INVOICES_BG);
        
        // Header bar title
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(0, 80));
        headerPanel.setBackground(UIConstants.MODULE_INVOICES_TEXT);

        JLabel titleLabel = new JLabel("Invoice Management", SwingConstants.CENTER);
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
        
        // Right side: Refresh
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        
        rightPanel.add(refreshButton);
        
        controlsPanel.add(leftPanel, BorderLayout.WEST);
        controlsPanel.add(rightPanel, BorderLayout.EAST);
        
        topPanel.add(controlsPanel, BorderLayout.CENTER);
        
        return topPanel;
    }
    
    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        bottomPanel.setBackground(UIConstants.MODULE_INVOICES_BG);
        
        viewButton = UIConstants.createSecondaryButton("DETAILS",
            UIConstants.loadIcon("/icons/details.png", 16));
        viewButton.setForeground(Color.WHITE);

        exportButton = UIConstants.createSecondaryButton("EXPORT",
            UIConstants.loadIcon("/icons/export.png", 16));
        exportButton.setForeground(Color.WHITE);

        bottomPanel.add(viewButton);
        bottomPanel.add(exportButton);
        
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
        refreshButton.addActionListener(e -> loadInvoices());
        
        // Bottom panel buttons
        viewButton.addActionListener(e -> viewInvoiceDetails());
        exportButton.addActionListener(e -> exportInvoices());
        
        // Double-click to edit / view
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    viewInvoiceDetails();
                }
            }
        });
    }
    
    public void loadInvoices() {
        try {
            List<DocumentQueryService.DocumentTrackingRow> rows = documentQueryService.listInvoiceTrackingRows();
            updateTable(rows);
        } catch (IllegalArgumentException iae) {
            JOptionPane.showMessageDialog(this,
                iae.getMessage(),
                "Warning",
                JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException re) {
            JOptionPane.showMessageDialog(this,
                "Error loading invoices: " + re.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateTable(List<DocumentQueryService.DocumentTrackingRow> rows) {
        tableModel.setRowCount(0); // Clear existing rows
        currentRows = rows != null ? rows : java.util.Collections.emptyList();
        
        for (DocumentQueryService.DocumentTrackingRow row : currentRows) {
            Object[] rowData = {
                row.number != null ? row.number : "",
                row.type,
                row.date != null ? row.date : "",
                row.party != null ? row.party : "",
                row.status != null ? row.status : "",
                row.subtotal != null ? row.subtotal : BigDecimal.ZERO,
                row.taxes != null ? row.taxes : BigDecimal.ZERO,
                row.total != null ? row.total : BigDecimal.ZERO
            };
            tableModel.addRow(rowData);
        }
    }
    
    private void performSearch() {
        String searchTerm = searchField.getText().trim();
        try {
            List<DocumentQueryService.DocumentTrackingRow> rows = documentQueryService.searchInvoiceTrackingRows(searchTerm);
            updateTable(rows);
        } catch (IllegalArgumentException iae) {
            JOptionPane.showMessageDialog(this,
                iae.getMessage(),
                "Warning",
                JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException re) {
            JOptionPane.showMessageDialog(this,
                "Error searching invoices: " + re.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearSearch() {
        searchField.setText("");
        loadInvoices();
    }
    
    /**
     * Apply sorting based on the selected option in the combo box
     */
    private void applySorting() {
        try {
            String selectedSort = (String) sortComboBox.getSelectedItem();
            List<DocumentQueryService.DocumentTrackingRow> rows = documentQueryService.listInvoiceTrackingRowsSorted(selectedSort);
            updateTable(rows);
            
        } catch (IllegalArgumentException iae) {
            JOptionPane.showMessageDialog(this,
                iae.getMessage(),
                "Warning",
                JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException re) {
            JOptionPane.showMessageDialog(this,
                "Error sorting invoices: " + re.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewInvoiceDetails() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select an invoice to view.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int modelRow = table.convertRowIndexToModel(selectedRow);
            if (modelRow < 0 || modelRow >= currentRows.size()) {
                JOptionPane.showMessageDialog(this,
                    "Invalid selection.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            DocumentQueryService.DocumentTrackingRow row = currentRows.get(modelRow);
            if (row.id == null) {
                JOptionPane.showMessageDialog(this,
                    "Invoice ID not found.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            var document = documentQueryService.findById(row.id);
            if (document == null) {
                JOptionPane.showMessageDialog(this,
                    "Invoice not found.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!(document instanceof com.billing.model.document.Invoice)) {
                JOptionPane.showMessageDialog(this,
                    "Selected document is not an invoice.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            com.billing.model.document.Invoice invoice = (com.billing.model.document.Invoice) document;
            Frame parent = JOptionPane.getFrameForComponent(this);
            InvoiceDetailsDialog dialog = new InvoiceDetailsDialog(parent, invoice);
            dialog.setVisible(true);
        } catch (RuntimeException re) {
            JOptionPane.showMessageDialog(this,
                "Error viewing invoice details: " + re.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportInvoices() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select an invoice to export.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            int modelRow = table.convertRowIndexToModel(selectedRow);
            Long docId = currentRows.size() > modelRow ? currentRows.get(modelRow).id : null;
            String code = (String) tableModel.getValueAt(modelRow, 0);
            if (docId == null) throw new IllegalArgumentException("No invoice selected");
            Object[] options = {"Export JSON", "Export XML"};
            int choice = JOptionPane.showOptionDialog(this,
                    "Export invoices to:",
                    "Export",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);
            if (choice == 0) {
                javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
                chooser.setSelectedFile(new java.io.File((code == null || code.isBlank()) ? "invoice.json" : code + ".json"));
                if (chooser.showSaveDialog(this) != javax.swing.JFileChooser.APPROVE_OPTION) return;
                exportService.exportInvoiceJson(docId, chooser.getSelectedFile().toPath());
            } else if (choice == 1) {
                javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
                chooser.setSelectedFile(new java.io.File((code == null || code.isBlank()) ? "invoice.xml" : code + ".xml"));
                if (chooser.showSaveDialog(this) != javax.swing.JFileChooser.APPROVE_OPTION) return;
                exportService.exportInvoiceXml(docId, chooser.getSelectedFile().toPath());
            } else {
                return;
            }
            JOptionPane.showMessageDialog(this,
                "Export completed.",
                "Export",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Export failed: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
