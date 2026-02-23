package com.billing.gui;

import com.billing.model.item.Item;
import com.billing.model.item.ItemCategory;
import com.billing.model.item.ItemFamily;
import com.billing.model.party.Supplier;
import com.billing.service.ItemService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.logging.Logger;

public class ItemManagementPanel extends JPanel {
    
    private static final Logger logger = Logger.getLogger(ItemManagementPanel.class.getName());
    private final ItemService itemService;
    
    // Table and model
    private JTable ItemTable;
    private DefaultTableModel tableModel;
    
    // UI Components
    private JTextField searchField;
    private JComboBox<String> filterComboBox;
    private JComboBox<ItemFamily> familyFilterComboBox;
    private JComboBox<ItemCategory> categoryFilterComboBox;
    private JComboBox<Supplier> supplierFilterComboBox;
    private JCheckBox activeOnlyCheckBox;
    private JCheckBox lowStockCheckBox;
    
    // Buttons
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton viewButton;
    private JButton refreshButton;
    private JButton searchButton;
    private JButton clearButton;
    
    // Labels for count
    private JLabel recordCountLabel;
    private JLabel lowStockCountLabel;
    private JLabel inactiveCountLabel;
    
    // Table columns (ID kept as hidden first column for internal use)
    private final String[] columnNames = {
        "ID", "Code", "Name", "Family", "Category", "Supplier", 
        "Cost Price", "Sale Price", "VAT", "Stock", "Min Stock", "Active"
    };
    
    public ItemManagementPanel(ItemService itemService) {
        this.itemService = itemService;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadData();
    }
    
    private void initializeComponents() {
        // Search components
        searchField = new JTextField(20);
        searchField.setFont(UIConstants.DEFAULT_FONT);
        
        String[] filterOptions = {"All", "Name", "Description", "Barcode"};
        filterComboBox = new JComboBox<>(filterOptions);
        filterComboBox.setFont(UIConstants.DEFAULT_FONT);
        
        // Filter comboboxes
        familyFilterComboBox = new JComboBox<>();
        familyFilterComboBox.setFont(UIConstants.DEFAULT_FONT);
        
        categoryFilterComboBox = new JComboBox<>();
        categoryFilterComboBox.setFont(UIConstants.DEFAULT_FONT);
        
        supplierFilterComboBox = new JComboBox<>();
        supplierFilterComboBox.setFont(UIConstants.DEFAULT_FONT);
        
        // Checkboxes
        activeOnlyCheckBox = new JCheckBox("Active only", false);
        activeOnlyCheckBox.setFont(UIConstants.DEFAULT_FONT);
        
        lowStockCheckBox = new JCheckBox("Low stock");
        lowStockCheckBox.setFont(UIConstants.DEFAULT_FONT);
        
        // Buttons using UIConstants styling
        addButton = UIConstants.createSuccessButton("ADD",
            UIConstants.loadIcon("/icons/add.png", 16));
        addButton.setForeground(Color.WHITE);
        
        editButton = UIConstants.createPrimaryButton("EDIT",
            UIConstants.loadIcon("/icons/edit.png", 16));
        editButton.setForeground(Color.WHITE);
        
        deleteButton = UIConstants.createDangerButton("DELETE",
            UIConstants.loadIcon("/icons/delete.png", 16));
        deleteButton.setForeground(Color.WHITE);
        
        viewButton = UIConstants.createSecondaryButton("DETAILS",
            UIConstants.loadIcon("/icons/details.png", 16));
        viewButton.setForeground(Color.WHITE);
        
        refreshButton = UIConstants.createSecondaryButton("REFRESH",
            UIConstants.loadIcon("/icons/refresh.png", 16));
        refreshButton.setBorder(null);
        refreshButton.setForeground(Color.WHITE);
        
        searchButton = UIConstants.createSecondaryButton("SEARCH",
            UIConstants.loadIcon("/icons/search.png", 16));

        clearButton = UIConstants.createSecondaryButton("CLEAR",
            UIConstants.loadIcon("/icons/clear.png", 16));
        
        // Table
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        
        ItemTable = new JTable(tableModel);
        ItemTable.setFont(UIConstants.DEFAULT_FONT);
        ItemTable.getTableHeader().setFont(UIConstants.DEFAULT_FONT.deriveFont(Font.BOLD));
        ItemTable.getTableHeader().setOpaque(true);
        ItemTable.setRowHeight(28);
        ItemTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ItemTable.setAutoCreateRowSorter(false);
        ItemTable.getTableHeader().setReorderingAllowed(false);
        ItemTable.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        ItemTable.getTableHeader().setCursor(Cursor.getDefaultCursor());

        // Custom header renderer without sort arrows
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setFont(UIConstants.DEFAULT_FONT.deriveFont(Font.BOLD));
                label.setOpaque(true);
                label.setBackground(UIConstants.MODULE_ITEMS_TEXT);
                label.setForeground(Color.WHITE);
                label.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
                return label;
            }
        };
        ItemTable.getTableHeader().setDefaultRenderer(headerRenderer);

        // Alternating row colors and selection styling (match clients table)
        DefaultTableCellRenderer unifiedRenderer = new DefaultTableCellRenderer() {
            private final Color EVEN = Color.WHITE;
            private final Color ODD = UIConstants.MODULE_ITEMS_BG;

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                          boolean hasFocus, int row, int column) {
                JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (isSelected) {
                    c.setBackground(UIConstants.MODULE_ITEMS_ICON);
                    c.setForeground(Color.WHITE);
                } else {
                    c.setBackground(row % 2 == 0 ? EVEN : ODD);
                    c.setForeground(new Color(0x212121));
                }
                return c;
            }
        };

        ItemTable.setDefaultRenderer(Object.class, unifiedRenderer);
        ItemTable.setDefaultRenderer(Number.class, unifiedRenderer);
        ItemTable.setDefaultRenderer(Integer.class, unifiedRenderer);

        setColumnWidths();
        
        // Disable table sorter to avoid type comparison issues
        // tableSorter = new TableRowSorter<>(tableModel);
        // ItemTable.setRowSorter(tableSorter);
        
        // Labels
        recordCountLabel = new JLabel("Items loaded: 0");
        recordCountLabel.setFont(UIConstants.DEFAULT_FONT);
        
        lowStockCountLabel = new JLabel("Low stock: 0");
        lowStockCountLabel.setFont(UIConstants.DEFAULT_FONT);
        lowStockCountLabel.setForeground(Color.RED);
        
        inactiveCountLabel = new JLabel("Inactive: 0");
        inactiveCountLabel.setFont(UIConstants.DEFAULT_FONT);
        inactiveCountLabel.setForeground(Color.GRAY);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(UIConstants.MODULE_ITEMS_BG);
        
        // Top panel with search and controls (includes title)
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);
        
        // Center panel with table
        JScrollPane tableScrollPane = new JScrollPane(ItemTable);
        tableScrollPane.setPreferredSize(new Dimension(0, 400));
        add(tableScrollPane, BorderLayout.CENTER);
        
        // Bottom panel with action buttons
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void setColumnWidths() {
        int[] columnWidths = {0, 50, 90, 180, 120, 120, 140, 90, 90, 70, 70, 90};

        for (int i = 0; i < columnWidths.length && i < ItemTable.getColumnCount(); i++) {
            ItemTable.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }

        // Hide ID column (internal use)
        if (ItemTable.getColumnCount() > 0) {
            var idCol = ItemTable.getColumnModel().getColumn(0);
            idCol.setMinWidth(0);
            idCol.setMaxWidth(0);
            idCol.setPreferredWidth(0);
            idCol.setResizable(false);
        }
    }
    
    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(UIConstants.MODULE_ITEMS_BG);
        
        // Header bar title
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(0, 80));
        headerPanel.setBackground(UIConstants.MODULE_ITEMS_TEXT);

        JLabel titleLabel = new JLabel("Item Management", SwingConstants.CENTER);
        titleLabel.setFont(UIConstants.TITLE_FONT.deriveFont(24f));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));

        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        topPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Search and controls panel - search on left, buttons on right
        JPanel controlsPanel = new JPanel(new BorderLayout());
        controlsPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));
        controlsPanel.setOpaque(false);
        
        // Left side: Search and Filter
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
        leftPanel.add(new JLabel("Filter:"));
        leftPanel.add(filterComboBox);
        
        // Right side: Add Item and Refresh
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
        bottomPanel.setBackground(UIConstants.MODULE_ITEMS_BG);

        bottomPanel.add(viewButton);
        bottomPanel.add(editButton);
        bottomPanel.add(deleteButton);
        
        return bottomPanel;
    }
    
    private void setupEventHandlers() {
        // Table selection listener
        ItemTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean hasSelection = ItemTable.getSelectedRow() != -1;
                editButton.setEnabled(hasSelection);
                deleteButton.setEnabled(hasSelection);
                viewButton.setEnabled(hasSelection);
            }
        });
        
        // Table double-click listener
        ItemTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && ItemTable.getSelectedRow() != -1) {
                    viewItemDetails();
                }
            }
        });
        
        // Button listeners
        addButton.addActionListener(e -> addItem());
        editButton.addActionListener(e -> editItem());
        deleteButton.addActionListener(e -> deleteItem());
        viewButton.addActionListener(e -> viewItemDetails());
        refreshButton.addActionListener(e -> refreshData());
        searchButton.addActionListener(e -> applyFilters());
        clearButton.addActionListener(e -> {
            searchField.setText("");
            applyFilters();
        });
        
        // Search and filter listeners
        filterComboBox.addActionListener(e -> applyFilters());
        familyFilterComboBox.addActionListener(e -> applyFilters());
        categoryFilterComboBox.addActionListener(e -> applyFilters());
        supplierFilterComboBox.addActionListener(e -> applyFilters());
        activeOnlyCheckBox.addActionListener(e -> applyFilters());
        lowStockCheckBox.addActionListener(e -> applyFilters());
        
        // Search field document listener for real-time search
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { applyFilters(); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { applyFilters(); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applyFilters(); }
        });
    }
    
    private void loadData() {
        try {
            // Load filter data first
            loadFilterData();
            
            // Then apply filters (which will load and display Items)
            applyFilters();
            
            updateCountLabels();
            
        } catch (Exception e) {
            logger.severe("Error loading item data: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading item data: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public void reloadItems() {
        loadData();
    }
    
    private void loadFilterData() {
        try {
            familyFilterComboBox.removeAllItems();
            familyFilterComboBox.addItem(null); // "All" option
                List<ItemFamily> families = itemService.getAllFamilies();
            for (ItemFamily family : families) {
                familyFilterComboBox.addItem(family);
            }
            updateCountLabels();
            categoryFilterComboBox.removeAllItems();
            categoryFilterComboBox.addItem(null); // "All" option
            List<ItemCategory> categories = itemService.getAllCategories();
            for (ItemCategory category : categories) {
                categoryFilterComboBox.addItem(category);
            }
            
            supplierFilterComboBox.removeAllItems();
            supplierFilterComboBox.addItem(null); // "All" option
            List<Supplier> suppliers = itemService.getAllSuppliers();
            for (Supplier supplier : suppliers) {
                supplierFilterComboBox.addItem(supplier);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void updateTable(List<Item> items) {
        tableModel.setRowCount(0);
        
        for (Item item : items) {
            Object[] rowData = {
                item.getId(),
                item.getCode() != null ? item.getCode() : "",
                item.getName(),
                item.getFamily() != null ? item.getFamily().getName() : "",
                item.getCategory() != null ? item.getCategory().getName() : "",
                item.getSupplier() != null ? item.getSupplier().getName() : "",
                String.format("%.2f€", item.getCostPrice()),
                String.format("%.2f€", item.getSalePrice()),
                String.format("%d%%", item.getIVAPercent()),
                item.getCurrentStock(),
                item.getMinimumStock(),
                item.isActive() ? "Yes" : "No"
            };
            tableModel.addRow(rowData);
        }
        
        updateCountLabels();
    }
    
    private void applyFilters() {
        try {
            String searchText = searchField.getText().trim();
            String filterType = (String) filterComboBox.getSelectedItem();
            ItemFamily selectedFamily = (ItemFamily) familyFilterComboBox.getSelectedItem();
            ItemCategory selectedCategory = (ItemCategory) categoryFilterComboBox.getSelectedItem();
            Supplier selectedSupplier = (Supplier) supplierFilterComboBox.getSelectedItem();
            boolean activeOnly = activeOnlyCheckBox.isSelected();
            boolean lowStock = lowStockCheckBox.isSelected();

            List<Item> filteredItems = itemService.searchItems(
                filterType,
                searchText,
                selectedFamily,
                selectedCategory,
                selectedSupplier,
                activeOnly,
                lowStock
            );

            updateTable(filteredItems);
        } catch (Exception e) {
            e.printStackTrace(); // Print full stack trace to console
            logger.severe("Error applying filters: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Error applying filters: " + e.getMessage() + "\nType: " + e.getClass().getName(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateCountLabels() {
        try {
            ItemService.ItemCounts counts = itemService.getItemCounts();
            recordCountLabel.setText("Items loaded: " + counts.total);
            lowStockCountLabel.setText("Low stock: " + counts.lowStock);
            inactiveCountLabel.setText("Inactive: " + counts.inactive);
        } catch (Exception e) {
            logger.warning("Error updating count labels: " + e.getMessage());
        }
    }
    
    private void addItem() {
        Frame parentFrame = getParentFrame();
        ItemFormDialog dialog = new ItemFormDialog(parentFrame, itemService);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            refreshData();
        }
    }
    
    private void editItem() {
        int selectedRow = ItemTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select an item to edit.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int modelRow = ItemTable.convertRowIndexToModel(selectedRow);
            Integer ItemId = (Integer) tableModel.getValueAt(modelRow, 0);

            Item item = itemService.getItemById(ItemId.longValue());
            if (item != null) {
                Frame parentFrame = getParentFrame();
                ItemFormDialog dialog = new ItemFormDialog(parentFrame, itemService, item);
                dialog.setVisible(true);

                if (dialog.isConfirmed()) {
                    refreshData();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Item not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error editing item: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteItem() {
        int selectedRow = ItemTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select an item to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int modelRow = ItemTable.convertRowIndexToModel(selectedRow);
            Integer ItemId = (Integer) tableModel.getValueAt(modelRow, 0);
            String ItemName = (String) tableModel.getValueAt(modelRow, 2);
            
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete the item '" + ItemName + "'?\n" +
                "This action cannot be undone.",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
                
            if (confirm == JOptionPane.YES_OPTION) {
                itemService.deleteItem(ItemId.longValue());
                JOptionPane.showMessageDialog(this, "Item deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            }
            
            } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error deleting the item: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void viewItemDetails() {
        int selectedRow = ItemTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select an item to view details.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int modelRow = ItemTable.convertRowIndexToModel(selectedRow);
            Integer ItemId = (Integer) tableModel.getValueAt(modelRow, 0);
            
            // Use service to obtain prepared view and report service to handle printing/export
            com.billing.dto.ItemDetailsView view = itemService.getItemDetailsView(ItemId.longValue());
            if (view != null) {
                Frame parentFrame = getParentFrame();
                com.billing.service.ItemReportService reportService = new com.billing.service.ItemReportService();
                ItemDetailsDialog dialog = new ItemDetailsDialog(parentFrame, view, reportService);
                dialog.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Item not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error showing item details: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void refreshData() {
        loadData();
    }
    

    
    public void selectItemById(Long ItemId) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Long id = (Long) tableModel.getValueAt(i, 0);
            if (id.equals(ItemId)) {
                int viewRow = ItemTable.convertRowIndexToView(i);
                ItemTable.setRowSelectionInterval(viewRow, viewRow);
                ItemTable.scrollRectToVisible(ItemTable.getCellRect(viewRow, 0, true));
                break;
            }
        }
    }
    
    public Item getSelectedItem() {
        int selectedRow = ItemTable.getSelectedRow();
        if (selectedRow == -1) return null;
        
        try {
            int modelRow = ItemTable.convertRowIndexToModel(selectedRow);
            Long ItemId = (Long) tableModel.getValueAt(modelRow, 0);

            return itemService.getItemById(ItemId);
            
        } catch (Exception e) {
            return null;
        }
    }
    
    private Frame getParentFrame() {
        java.awt.Window window = SwingUtilities.getWindowAncestor(this);
        if (window instanceof Frame) {
            return (Frame) window;
        }
        
        // Fallback: walk up component hierarchy
        java.awt.Container parent = this.getParent();
        while (parent != null && !(parent instanceof Frame)) {
            parent = parent.getParent();
        }
        
        // If we found a Frame, return it; otherwise return null
        if (parent instanceof Frame) {
            return (Frame) parent;
        }
        
        // Last resort: try to find the main window from all frames
        for (java.awt.Frame frame : java.awt.Frame.getFrames()) {
            if (frame.isVisible()) {
                return frame;
            }
        }
        
        return null;
    }
}
