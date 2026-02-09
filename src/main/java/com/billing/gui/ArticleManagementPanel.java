package com.billing.gui;

import com.billing.entity.Article;
import com.billing.entity.ArticleCategory;
import com.billing.entity.ArticleFamily;
import com.billing.entity.Supplier;
import com.billing.service.ArticleService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ArticleManagementPanel extends JPanel {
    
    private static final Logger logger = Logger.getLogger(ArticleManagementPanel.class.getName());
    private final ArticleService articleService;
    
    // Table and model
    private JTable articleTable;
    private DefaultTableModel tableModel;
    
    // UI Components
    private JTextField searchField;
    private JComboBox<String> filterComboBox;
    private JComboBox<ArticleFamily> familyFilterComboBox;
    private JComboBox<ArticleCategory> categoryFilterComboBox;
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
    
    // Table columns
    private final String[] columnNames = {
        "ID", "Code", "Name", "Family", "Category", "Supplier", 
        "Cost Price", "Sale Price", "VAT", "Stock", "Min Stock", "Active"
    };
    
    public ArticleManagementPanel(ArticleService articleService) {
        this.articleService = articleService;
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
        
        articleTable = new JTable(tableModel);
        articleTable.setFont(UIConstants.DEFAULT_FONT);
        articleTable.getTableHeader().setFont(UIConstants.DEFAULT_FONT.deriveFont(Font.BOLD));
        articleTable.getTableHeader().setOpaque(true);
        articleTable.setRowHeight(28);
        articleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        articleTable.setAutoCreateRowSorter(false);
        articleTable.getTableHeader().setReorderingAllowed(false);
        articleTable.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        articleTable.getTableHeader().setCursor(Cursor.getDefaultCursor());

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
                label.setBackground(UIConstants.MODULE_PRODUCTS_TEXT);
                label.setForeground(Color.WHITE);
                label.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
                return label;
            }
        };
        articleTable.getTableHeader().setDefaultRenderer(headerRenderer);

        // Alternating row colors and selection styling (match clients table)
        DefaultTableCellRenderer unifiedRenderer = new DefaultTableCellRenderer() {
            private final Color EVEN = Color.WHITE;
            private final Color ODD = UIConstants.MODULE_PRODUCTS_BG;

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                          boolean hasFocus, int row, int column) {
                JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (isSelected) {
                    c.setBackground(UIConstants.MODULE_PRODUCTS_ICON);
                    c.setForeground(Color.WHITE);
                } else {
                    c.setBackground(row % 2 == 0 ? EVEN : ODD);
                    c.setForeground(new Color(0x212121));
                }
                return c;
            }
        };

        articleTable.setDefaultRenderer(Object.class, unifiedRenderer);
        articleTable.setDefaultRenderer(Number.class, unifiedRenderer);
        articleTable.setDefaultRenderer(Integer.class, unifiedRenderer);

        setColumnWidths();
        
        // Disable table sorter to avoid type comparison issues
        // tableSorter = new TableRowSorter<>(tableModel);
        // articleTable.setRowSorter(tableSorter);
        
        // Labels
        recordCountLabel = new JLabel("Articles loaded: 0");
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
        setBackground(UIConstants.MODULE_PRODUCTS_BG);
        
        // Top panel with search and controls (includes title)
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);
        
        // Center panel with table
        JScrollPane tableScrollPane = new JScrollPane(articleTable);
        tableScrollPane.setPreferredSize(new Dimension(0, 400));
        add(tableScrollPane, BorderLayout.CENTER);
        
        // Bottom panel with action buttons
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void setColumnWidths() {
        int[] columnWidths = {50, 90, 180, 120, 120, 140, 90, 90, 70, 70, 90, 70};

        for (int i = 0; i < columnWidths.length && i < articleTable.getColumnCount(); i++) {
            articleTable.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }
    }
    
    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(UIConstants.MODULE_PRODUCTS_BG);
        
        // Header bar title
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(0, 80));
        headerPanel.setBackground(UIConstants.MODULE_PRODUCTS_TEXT);

        JLabel titleLabel = new JLabel("Article Management", SwingConstants.CENTER);
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
        
        // Right side: Add Article and Refresh
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
        bottomPanel.setBackground(UIConstants.MODULE_PRODUCTS_BG);

        bottomPanel.add(viewButton);
        bottomPanel.add(editButton);
        bottomPanel.add(deleteButton);
        
        return bottomPanel;
    }
    
    private void setupEventHandlers() {
        // Table selection listener
        articleTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean hasSelection = articleTable.getSelectedRow() != -1;
                editButton.setEnabled(hasSelection);
                deleteButton.setEnabled(hasSelection);
                viewButton.setEnabled(hasSelection);
            }
        });
        
        // Table double-click listener
        articleTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && articleTable.getSelectedRow() != -1) {
                    viewArticleDetails();
                }
            }
        });
        
        // Button listeners
        addButton.addActionListener(e -> addArticle());
        editButton.addActionListener(e -> editArticle());
        deleteButton.addActionListener(e -> deleteArticle());
        viewButton.addActionListener(e -> viewArticleDetails());
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
            
            // Then apply filters (which will load and display articles)
            applyFilters();
            
            updateCountLabels();
            
        } catch (Exception e) {
            logger.severe("Error loading article data: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading article data: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadFilterData() {
        try {
            familyFilterComboBox.removeAllItems();
            familyFilterComboBox.addItem(null); // "All" option
            List<ArticleFamily> families = articleService.getAllFamilies();
            for (ArticleFamily family : families) {
                familyFilterComboBox.addItem(family);
            }
            
            categoryFilterComboBox.removeAllItems();
            categoryFilterComboBox.addItem(null); // "All" option
            List<ArticleCategory> categories = articleService.getAllCategories();
            for (ArticleCategory category : categories) {
                categoryFilterComboBox.addItem(category);
            }
            
            supplierFilterComboBox.removeAllItems();
            supplierFilterComboBox.addItem(null); // "All" option
            List<Supplier> suppliers = articleService.getAllSuppliers();
            for (Supplier supplier : suppliers) {
                supplierFilterComboBox.addItem(supplier);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void updateTable(List<Article> articles) {
        tableModel.setRowCount(0);
        
        for (Article article : articles) {
            Object[] rowData = {
                article.getId(),
                article.getCode() != null ? article.getCode() : "",
                article.getName(),
                article.getFamily() != null ? article.getFamily().getName() : "",
                article.getCategory() != null ? article.getCategory().getName() : "",
                article.getSupplier() != null ? article.getSupplier().getName() : "",
                String.format("%.2f€", article.getCostPrice()),
                String.format("%.2f€", article.getSalePrice()),
                String.format("%d%%", article.getIVAPercent()),
                article.getCurrentStock(),
                article.getMinimumStock(),
                article.isActive() ? "Yes" : "No"
            };
            tableModel.addRow(rowData);
        }
        
        updateCountLabels();
    }
    
    private void applyFilters() {
        try {
            String searchText = searchField.getText().trim();
            List<Article> allArticles = articleService.getAllArticles();
            
            // Apply filters
            List<Article> filteredArticles = new ArrayList<>(allArticles);
            
            // Search filter
            if (!searchText.isEmpty()) {
                filteredArticles.removeIf(article -> !isArticleMatchingSearch(article, searchText));
            }
            
            // Family filter
            ArticleFamily selectedFamily = (ArticleFamily) familyFilterComboBox.getSelectedItem();
            if (selectedFamily != null) {
                filteredArticles.removeIf(article -> 
                    article.getFamily() == null || !article.getFamily().getCode().equals(selectedFamily.getCode()));
            }
            
            // Category filter
            ArticleCategory selectedCategory = (ArticleCategory) categoryFilterComboBox.getSelectedItem();
            if (selectedCategory != null) {
                filteredArticles.removeIf(article -> 
                    article.getCategory() == null || !article.getCategory().getCode().equals(selectedCategory.getCode()));
            }
            
            // Supplier filter
            Supplier selectedSupplier = (Supplier) supplierFilterComboBox.getSelectedItem();
            if (selectedSupplier != null) {
                filteredArticles.removeIf(article -> 
                    article.getSupplier() == null || !article.getSupplier().getName().equals(selectedSupplier.getName()));
            }
            
            // Active only filter
            if (activeOnlyCheckBox.isSelected()) {
                filteredArticles.removeIf(article -> !article.isActive());
            }
            
            // Low stock filter
            if (lowStockCheckBox.isSelected()) {
                filteredArticles.removeIf(article -> article.getCurrentStock() >= article.getMinimumStock());
            }
                
            updateTable(filteredArticles);
        } catch (Exception e) {
            e.printStackTrace(); // Print full stack trace to console
            logger.severe("Error applying filters: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Error applying filters: " + e.getMessage() + "\nType: " + e.getClass().getName(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean isArticleMatchingSearch(Article article, String searchText) {
        String filter = (String) filterComboBox.getSelectedItem();
        switch (filter) {
            case "Name":
                return article.getName().toLowerCase().contains(searchText.toLowerCase());
            case "Description":
                return article.getDescription().toLowerCase().contains(searchText.toLowerCase());
            case "Barcode":
                return article.getBarcode() != null && article.getBarcode().toLowerCase().contains(searchText.toLowerCase());
            default: // "All"
                return article.getName().toLowerCase().contains(searchText.toLowerCase()) ||
                       article.getDescription().toLowerCase().contains(searchText.toLowerCase()) ||
                       (article.getBarcode() != null && article.getBarcode().toLowerCase().contains(searchText.toLowerCase()));
        }
    }
    
    private void updateCountLabels() {
        try {
            List<Article> allArticles = articleService.getAllArticles();
            int totalArticles = allArticles.size();
            int lowStockCount = (int) allArticles.stream()
                .filter(a -> a.getCurrentStock() < a.getMinimumStock())
                .count();
            int inactiveCount = (int) allArticles.stream()
                .filter(a -> !a.isActive())
                .count();
            
            recordCountLabel.setText("Articles loaded: " + totalArticles);
            lowStockCountLabel.setText("Low stock: " + lowStockCount);
            inactiveCountLabel.setText("Inactive: " + inactiveCount);
        } catch (Exception e) {
            logger.warning("Error updating count labels: " + e.getMessage());
        }
    }
    
    private void addArticle() {
        Frame parentFrame = getParentFrame();
        ArticleFormDialog dialog = new ArticleFormDialog(parentFrame, articleService);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            refreshData();
        }
    }
    
    private void editArticle() {
        int selectedRow = articleTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select an article to edit.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int modelRow = articleTable.convertRowIndexToModel(selectedRow);
            Integer articleId = (Integer) tableModel.getValueAt(modelRow, 0);
            
            Article article = articleService.getArticleById(articleId);
            if (article != null) {
                Frame parentFrame = getParentFrame();
                ArticleFormDialog dialog = new ArticleFormDialog(parentFrame, articleService, article);
                dialog.setVisible(true);
                
                if (dialog.isConfirmed()) {
                    refreshData();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Article not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error editing article: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteArticle() {
        int selectedRow = articleTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select an article to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int modelRow = articleTable.convertRowIndexToModel(selectedRow);
            Integer articleId = (Integer) tableModel.getValueAt(modelRow, 0);
            String articleName = (String) tableModel.getValueAt(modelRow, 2);
            
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete the article '" + articleName + "'?\n" +
                "This action cannot be undone.",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
                
            if (confirm == JOptionPane.YES_OPTION) {
                articleService.deleteArticle(articleId);
                JOptionPane.showMessageDialog(this, "Article deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error deleting the article: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void viewArticleDetails() {
        int selectedRow = articleTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select an article to view details.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int modelRow = articleTable.convertRowIndexToModel(selectedRow);
            Integer articleId = (Integer) tableModel.getValueAt(modelRow, 0);
            
            Article article = articleService.getArticleById(articleId);
            if (article != null) {
                Frame parentFrame = getParentFrame();
                ArticleDetailsDialog dialog = new ArticleDetailsDialog(parentFrame, article, articleService);
                dialog.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Article not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error showing article details: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void refreshData() {
        loadData();
    }
    

    
    public void selectArticleById(Integer articleId) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Integer id = (Integer) tableModel.getValueAt(i, 0);
            if (id.equals(articleId)) {
                int viewRow = articleTable.convertRowIndexToView(i);
                articleTable.setRowSelectionInterval(viewRow, viewRow);
                articleTable.scrollRectToVisible(articleTable.getCellRect(viewRow, 0, true));
                break;
            }
        }
    }
    
    public Article getSelectedArticle() {
        int selectedRow = articleTable.getSelectedRow();
        if (selectedRow == -1) return null;
        
        try {
            int modelRow = articleTable.convertRowIndexToModel(selectedRow);
            Integer articleId = (Integer) tableModel.getValueAt(modelRow, 0);
            
            return articleService.getArticleById(articleId);
            
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