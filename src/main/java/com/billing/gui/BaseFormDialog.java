package com.billing.gui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

/**
 * Abstract base class for form dialogs (create/edit operations)
 * Provides common structure, validation framework, and button handling
 */
public abstract class BaseFormDialog<T> extends JDialog {
    
    protected T entity;
    protected boolean confirmed = false;
    protected JButton okButton;
    protected JButton cancelButton;
    
    public BaseFormDialog(Frame parent, String title, boolean modal) {
        super(getNullSafeParent(parent), title, modal);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(true);
        setLayout(new BorderLayout());
        
        // Create buttons with proper styling
        cancelButton = UIConstants.createDangerButton("CANCEL", UIConstants.loadIcon("/icons/cancel.png", 16));
        okButton = UIConstants.createSuccessButton("SAVE", UIConstants.loadIcon("/icons/save.png", 16));
        setupButtonHandlers();
        
        // Setup the UI - this will be called by subclasses
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
        // to configure the dialog layout
    
    
    /**
     * Setup basic dialog layout with tabbed pane for the form
     * Subclasses should call this method after initializing their components
     */
    protected void setupFormLayout() {
        // Create the main form panel - subclasses override createFormPanel()
        java.awt.Component formComponent = createFormPanel();
        if (formComponent != null) {
            // If the form panel is already a tabbed pane from the subclass, use it directly
            if (formComponent instanceof JTabbedPane) {
                add((JTabbedPane) formComponent, BorderLayout.CENTER);
            } else {
                // Otherwise wrap it in a tab
                JTabbedPane tabbedPane = new JTabbedPane();
                tabbedPane.setFont(UIConstants.DEFAULT_FONT);
                tabbedPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                tabbedPane.addTab("General", (JPanel) formComponent);
                add(tabbedPane, BorderLayout.CENTER);
            }
        }
        
        // Button panel - centered with cancel on left, save on right
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 20, 0));
        
        // Style buttons
        cancelButton.setForeground(java.awt.Color.WHITE);
        cancelButton.setFont(UIConstants.BUTTON_FONT);
        
        okButton.setForeground(java.awt.Color.WHITE);
        okButton.setFont(UIConstants.BUTTON_FONT);
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(okButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Setup button event handlers
     */
    private void setupButtonHandlers() {
        okButton.addActionListener(e -> handleOkAction());
        cancelButton.addActionListener(e -> {
            confirmed = false;
            dispose();
        });
    }
    
    /**
     * Handle OK button click - validate and save
     */
    private void handleOkAction() {
        try {
            if (validateForm()) {
                if (saveEntity()) {
                    confirmed = true;
                    dispose();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error saving: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Populate form fields with entity data (for edit mode)
     * Subclasses should override to populate their specific fields
     */
    protected abstract void populateFields();
    
    /**
     * Create the form panel with tabs
     * Subclasses can override to create custom tabbed layouts
     * Return a JTabbedPane for tabbed layouts or JPanel for single tab
     */
    protected abstract java.awt.Component createFormPanel();
    
    /**
     * Validate form input
     * Subclasses should override to validate their specific fields
     */
    protected abstract boolean validateForm();
    
    /**
     * Save the entity to database
     * Subclasses should implement the actual save logic
     */
    protected abstract boolean saveEntity();
    
    /**
     * Initialize the form with entity data (edit mode)
     */
    public void initializeWithEntity(T entity) {
        this.entity = entity;
        populateFields();
    }
    
    /**
     * Check if the dialog was confirmed by the user
     */
    public boolean isConfirmed() {
        return confirmed;
    }
    
    /**
     * Get the entity being edited/created
     */
    public T getEntity() {
        return entity;
    }
}
