package com.billing.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

/**
 * Abstract base class for details dialogs (read-only view)
 * Provides common structure for displaying entity information
 */
public abstract class BaseDetailsDialog<T> extends JDialog {
    
    protected T entity;
    protected JButton printButton;
    
    public BaseDetailsDialog(Frame parent, T entity, String title) {
        super(getNullSafeParent(parent), title, true);
        this.entity = entity;
        
        onBeforeLayout();
        initializeDialog();
        setupLayout();
        configureDialog();
    }

    /**
     * Hook for subclasses to initialize UI components before layout is built.
     */
    protected void onBeforeLayout() {
        // Default no-op.
    }
    
    private static Frame getNullSafeParent(Frame parent) {
        if (parent != null) {
            return parent;
        }
        
        // Try to find a visible frame from the system
        for (java.awt.Frame frame : java.awt.Frame.getFrames()) {
            if (frame.isVisible()) {
                return frame;
            }
        }
        
        // If no visible frame found, create a new one
        return new javax.swing.JFrame();
    }
    
    /**
     * Initialize dialog properties
     */
    protected void initializeDialog() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(true);
        setMinimumSize(new Dimension(550, 400));
    }
    
    /**
     * Setup the main layout with scrollable content and buttons
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Main content panel (to be populated by subclasses)
        JPanel contentPanel = createContentPanel();
        
        // Scroll pane for vertical scrolling
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        // Increase scroll speed for better responsiveness
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.getVerticalScrollBar().setBlockIncrement(120);
        
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = createButtonPanel();
        if (buttonPanel != null) {
            add(buttonPanel, BorderLayout.SOUTH);
        }
    }

    /**
     * Create the button panel for the dialog. Subclasses can override.
     */
    protected JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        buttonPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 0));
        printButton = UIConstants.createSecondaryButton("PRINT", null);
        javax.swing.Icon printIcon = UIConstants.loadIcon("/icons/print.png", 16);
        if (printIcon != null) {
            printButton.setIcon(printIcon);
        }
        printButton.addActionListener(e -> handlePrint());
        buttonPanel.add(printButton);
        return buttonPanel;
    }
    
    /**
     * Create the main content panel with entity details
     * Subclasses should override to create their specific content
     */
    protected abstract JPanel createContentPanel();
    
    /**
     * Handle print action
     * Subclasses can override to implement custom printing logic
     */
    protected void handlePrint() {
        // Default implementation - subclasses can override
    }
    
    /**
     * Configure the dialog size and position
     */
    protected void configureDialog() {
        setSize(600, 500);
        setLocationRelativeTo(getParent());
    }
    
    /**
     * Get the entity being displayed
     */
    public T getEntity() {
        return entity;
    }
}
