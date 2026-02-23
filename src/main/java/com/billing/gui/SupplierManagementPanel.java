package com.billing.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

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

import com.billing.model.party.Supplier;
import com.billing.service.SupplierService;

/**
 * Panel for managing suppliers.
 */
public class SupplierManagementPanel extends JPanel {

    private final SupplierService supplierService;
    private JTable supplierTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> sortComboBox;
    private List<Supplier> currentSuppliers = java.util.Collections.emptyList();

    private JButton searchButton;
    private JButton clearButton;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton viewButton;
    private JButton refreshButton;

    private final String[] columnNames = {
        "Code", "Name", "Tax ID", "Address", "City", "Province",
        "Postal Code", "Fixed Phone", "Mobile Phone", "Email", "Website", "Active"
    };

    public SupplierManagementPanel(SupplierService supplierService) {
        this.supplierService = supplierService;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadSuppliers();
    }

    private void initializeComponents() {
        searchField = new JTextField(20);
        searchField.setFont(UIConstants.UI_FONT);

        String[] sortOptions = {"Sort by Code", "Sort by Tax ID", "Sort by Name"};
        sortComboBox = new JComboBox<>(sortOptions);
        sortComboBox.setFont(UIConstants.UI_FONT);
        sortComboBox.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addButton = UIConstants.createSuccessButton("ADD", UIConstants.loadIcon("/icons/add.png", 16));
        addButton.setForeground(Color.WHITE);

        refreshButton = UIConstants.createSecondaryButton("REFRESH", UIConstants.loadIcon("/icons/refresh.png", 16));
        refreshButton.setBorder(null);
        refreshButton.setForeground(Color.WHITE);

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class;
            }
        };

        supplierTable = new JTable(tableModel);
        supplierTable.setFont(UIConstants.UI_FONT);
        supplierTable.getTableHeader().setFont(UIConstants.UI_FONT.deriveFont(Font.BOLD));
        supplierTable.getTableHeader().setOpaque(true);
        supplierTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        supplierTable.setRowHeight(28);
        supplierTable.getTableHeader().setReorderingAllowed(false);
        supplierTable.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        supplierTable.getTableHeader().setCursor(Cursor.getDefaultCursor());

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
                label.setBackground(UIConstants.MODULE_SUPPLIERS_TEXT);
                label.setForeground(Color.WHITE);
                label.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
                return label;
            }
        };
        supplierTable.getTableHeader().setDefaultRenderer(headerRenderer);

        supplierTable.setAutoCreateRowSorter(false);
        setColumnWidths();

        DefaultTableCellRenderer unifiedRenderer = new DefaultTableCellRenderer() {
            private final Color EVEN = Color.WHITE;
            private final Color ODD = UIConstants.MODULE_SUPPLIERS_BG;

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                          boolean hasFocus, int row, int column) {
                JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (isSelected) {
                    c.setBackground(UIConstants.MODULE_SUPPLIERS_ICON);
                    c.setForeground(Color.WHITE);
                } else {
                    c.setBackground(row % 2 == 0 ? EVEN : ODD);
                    c.setForeground(new Color(0x212121));
                }
                return c;
            }
        };

        supplierTable.setDefaultRenderer(Object.class, unifiedRenderer);

        searchButton = UIConstants.createSecondaryButton("SEARCH", UIConstants.loadIcon("/icons/search.png", 16));
        clearButton = UIConstants.createSecondaryButton("CLEAR", UIConstants.loadIcon("/icons/clear.png", 16));
    }

    private void setColumnWidths() {
        int[] columnWidths = {90, 180, 130, 180, 120, 120, 100, 120, 120, 180, 180, 70};
        for (int i = 0; i < columnWidths.length && i < supplierTable.getColumnCount(); i++) {
            supplierTable.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(UIConstants.MODULE_SUPPLIERS_BG);

        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        JScrollPane tableScrollPane = new JScrollPane(supplierTable);
        tableScrollPane.setPreferredSize(new Dimension(0, 400));
        UIConstants.enhanceScroll(tableScrollPane);
        add(tableScrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(UIConstants.MODULE_SUPPLIERS_BG);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(0, 80));
        headerPanel.setBackground(UIConstants.MODULE_SUPPLIERS_TEXT);

        JLabel titleLabel = new JLabel("Supplier Management", SwingConstants.CENTER);
        titleLabel.setFont(UIConstants.TITLE_FONT.deriveFont(24f));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));

        headerPanel.add(titleLabel, BorderLayout.CENTER);
        topPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel controlsPanel = new JPanel(new BorderLayout());
        controlsPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));
        controlsPanel.setOpaque(false);

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
        bottomPanel.setBackground(UIConstants.MODULE_SUPPLIERS_BG);

        viewButton = UIConstants.createSecondaryButton("DETAILS", UIConstants.loadIcon("/icons/details.png", 16));
        viewButton.setForeground(Color.WHITE);

        editButton = UIConstants.createPrimaryButton("EDIT", UIConstants.loadIcon("/icons/edit.png", 16));
        editButton.setForeground(Color.WHITE);

        deleteButton = UIConstants.createDangerButton("DELETE", UIConstants.loadIcon("/icons/delete.png", 16));
        deleteButton.setForeground(Color.WHITE);

        bottomPanel.add(viewButton);
        bottomPanel.add(editButton);
        bottomPanel.add(deleteButton);
        return bottomPanel;
    }

    private void setupEventHandlers() {
        searchField.addActionListener(e -> performSearch());
        searchButton.addActionListener(e -> performSearch());
        clearButton.addActionListener(e -> clearSearch());
        sortComboBox.addActionListener(e -> applySorting());

        addButton.addActionListener(e -> addSupplier());
        refreshButton.addActionListener(e -> loadSuppliers());

        editButton.addActionListener(e -> editSupplier());
        deleteButton.addActionListener(e -> deleteSupplier());
        viewButton.addActionListener(e -> viewSupplierDetails());

        supplierTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    viewSupplierDetails();
                }
            }
        });
    }

    public void loadSuppliers() {
        try {
            currentSuppliers = supplierService.getAllSuppliers();
            updateTable(currentSuppliers);
        } catch (RuntimeException re) {
            JOptionPane.showMessageDialog(this,
                "Error loading suppliers: " + re.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTable(List<Supplier> suppliers) {
        tableModel.setRowCount(0);
        List<Supplier> rows = suppliers != null ? suppliers : java.util.Collections.emptyList();
        currentSuppliers = rows;

        for (Supplier supplier : rows) {
            Object[] rowData = {
                safe(supplier.getCode()),
                safe(supplier.getName()),
                safe(supplier.getTaxId()),
                safe(supplier.getAddress()),
                safe(supplier.getCity()),
                supplier.getProvince() != null ? supplier.getProvince().name() : "",
                safe(supplier.getPostalCode()),
                safe(supplier.getFixedPhone()),
                safe(supplier.getMobilePhone()),
                safe(supplier.getEmail()),
                safe(supplier.getWebsite()),
                Boolean.TRUE.equals(supplier.getActive()) ? "Yes" : "No"
            };
            tableModel.addRow(rowData);
        }
    }

    private void performSearch() {
        String searchTerm = searchField.getText().trim().toLowerCase(Locale.ROOT);
        if (searchTerm.isEmpty()) {
            updateTable(currentSuppliers);
            return;
        }

        List<Supplier> base = supplierService.getAllSuppliers();
        List<Supplier> filtered = base.stream()
            .filter(s -> contains(s.getCode(), searchTerm)
                || contains(s.getName(), searchTerm)
                || contains(s.getTaxId(), searchTerm)
                || contains(s.getCity(), searchTerm)
                || contains(s.getEmail(), searchTerm))
            .collect(Collectors.toList());
        updateTable(filtered);
    }

    private void clearSearch() {
        searchField.setText("");
        loadSuppliers();
    }

    private void applySorting() {
        List<Supplier> sorted = new ArrayList<>(currentSuppliers);
        String selectedSort = (String) sortComboBox.getSelectedItem();
        if (selectedSort == null) {
            return;
        }

        switch (selectedSort) {
            case "Sort by Tax ID" -> sorted.sort(Comparator.comparing(s -> safe(s.getTaxId())));
            case "Sort by Name" -> sorted.sort(Comparator.comparing(s -> safe(s.getName())));
            default -> sorted.sort(Comparator.comparing(s -> safe(s.getCode())));
        }
        updateTable(sorted);
    }

    private void viewSupplierDetails() {
        Supplier selectedSupplier = getSelectedSupplier();
        if (selectedSupplier == null) {
            JOptionPane.showMessageDialog(this,
                "Please select a supplier to view.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Supplier supplier = supplierService.findSupplierById(selectedSupplier.getId());
            if (supplier == null) {
                JOptionPane.showMessageDialog(this,
                    "Supplier not found.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            Frame parent = JOptionPane.getFrameForComponent(this);
            SupplierDetailsDialog dialog = new SupplierDetailsDialog(parent, supplier);
            dialog.setVisible(true);
        } catch (RuntimeException re) {
            JOptionPane.showMessageDialog(this,
                "Error viewing supplier details: " + re.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSupplier() {
        Supplier selectedSupplier = getSelectedSupplier();
        if (selectedSupplier == null) {
            JOptionPane.showMessageDialog(this,
                "Please select a supplier to delete.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete supplier '" + safe(selectedSupplier.getName()) + "'?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            supplierService.deleteSupplier(selectedSupplier.getId());
            loadSuppliers();
        } catch (RuntimeException re) {
            JOptionPane.showMessageDialog(this,
                "Error deleting supplier: " + re.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private Supplier getSelectedSupplier() {
        int selectedRow = supplierTable.getSelectedRow();
        if (selectedRow < 0) {
            return null;
        }

        int modelRow = supplierTable.convertRowIndexToModel(selectedRow);
        if (modelRow < 0 || modelRow >= currentSuppliers.size()) {
            return null;
        }
        return currentSuppliers.get(modelRow);
    }

    private void addSupplier() {
        Frame parent = JOptionPane.getFrameForComponent(this);
        SupplierFormDialog dialog = new SupplierFormDialog(
            parent,
            "Add New Supplier",
            null,
            supplierService
        );
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            loadSuppliers();
        }
    }

    private void editSupplier() {
        Supplier selectedSupplier = getSelectedSupplier();
        if (selectedSupplier == null) {
            JOptionPane.showMessageDialog(this,
                "Please select a supplier to edit.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Supplier supplier = supplierService.findSupplierById(selectedSupplier.getId());
            if (supplier == null) {
                throw new IllegalArgumentException("Supplier not found");
            }

            Frame parent = JOptionPane.getFrameForComponent(this);
            SupplierFormDialog dialog = new SupplierFormDialog(
                parent,
                "Edit Supplier",
                supplier,
                supplierService
            );
            dialog.setVisible(true);

            if (dialog.isConfirmed()) {
                loadSuppliers();
            }
        } catch (IllegalArgumentException iae) {
            JOptionPane.showMessageDialog(this,
                iae.getMessage(),
                "Warning",
                JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException re) {
            JOptionPane.showMessageDialog(this,
                "Error editing supplier: " + re.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private static boolean contains(String value, String term) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(term);
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}