package com.billing.service;

import com.billing.dto.ItemDetailsView;
import com.billing.dto.ItemImportDTO;
import com.billing.model.party.Supplier;
import com.billing.model.item.Item;
import com.billing.model.item.ItemCategory;
import com.billing.model.item.ItemFamily;
import com.billing.model.item.Unit;
import com.billing.repository.RepositoryFactory;
import com.billing.repository.interfaces.ItemRepository;
import com.billing.repository.interfaces.PartyRepository;
import com.billing.tx.HibernateTransactionManager;
import com.billing.tx.TransactionManager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

/**
 * Service class for Item business logic operations
 */
public class ItemService {

    private static final Logger logger = Logger.getLogger(ItemService.class.getName());
    private static final List<Integer> VALID_IVA_PERCENTAGES = Arrays.asList(0, 4, 10, 21);

    private final ItemRepository itemRepository;
    private final PartyRepository partyRepository;
    private final TransactionManager tx;

    public ItemService() {
        this.itemRepository = RepositoryFactory.createItemRepository();
        this.partyRepository = RepositoryFactory.createPartyRepository();
        this.tx = new HibernateTransactionManager();
    }

    public ItemService(ItemRepository itemRepository,
                       PartyRepository partyRepository,
                       TransactionManager tx) {
        this.itemRepository = itemRepository;
        this.partyRepository = partyRepository;
        this.tx = tx;
    }

    public Item createItem(Item item) {
        logger.info("Creating new item: " + item.getName());
        validateItem(item);
        validateUniqueConstraints(item);

        return tx.runInTransaction(() -> {
            item.setCode(nextItemCode());
            return itemRepository.save(item);
        });
    }

    /**
     * Regenerate codes for all items (useful after bulk import).
     * Returns the number of updated rows.
     */
    public int regenerateAllItemCodes() {
        return tx.runInTransaction(() -> {
            List<Item> items = itemRepository.findAll();
            items.sort(Comparator.comparing(i -> i.getName() == null ? "" : i.getName(), String.CASE_INSENSITIVE_ORDER));
            int n = 1;
            for (Item it : items) {
                if (it.getId() != null) {
                    itemRepository.updateCode(it.getId(), formatItemCode(n++));
                }
            }
            logger.info("Regenerated codes for items: " + items.size());
            return items.size();
        });
    }

    public Item updateItem(Item item) {
        logger.info("Updating item: " + item.getName());
        validateItem(item);
        validateUniqueConstraintsForUpdate(item);
        return tx.runInTransaction(() -> itemRepository.save(item));
    }

    public List<Item> getAllItems() {
        try {
            return tx.runInTransaction(() -> itemRepository.findAll());
        } catch (Exception e) {
            logger.severe("Error retrieving all items: " + e.getMessage());
            throw new RuntimeException("Error retrieving all items", e);
        }
    }

    public List<Item> getAllActiveItems() {
        try { return tx.runInTransaction(() -> itemRepository.findAllActive()); } catch (Exception e) { throw new RuntimeException("Error retrieving active items", e); }
    }

    public List<Item> getAllInactiveItems() {
        try { return tx.runInTransaction(() -> itemRepository.findAllInactive()); } catch (Exception e) { throw new RuntimeException("Error retrieving inactive items", e); }
    }

    public List<Item> getLowStockItems() {
        try { return tx.runInTransaction(() -> itemRepository.findLowStockItems()); } catch (Exception e) { throw new RuntimeException("Error retrieving low stock items", e); }
    }

    public Item getItemById(Long id) {
        if (id == null) throw new IllegalArgumentException("Item ID cannot be null");
        return tx.runInTransaction(() -> itemRepository.findById(id));
    }

    /**
     * Returns a prepared view object with computed business fields for presentation.
     * Delegates calculations to ItemReportService so the GUI can remain a pure view.
     */
    public ItemDetailsView getItemDetailsView(Long id) {
        Item item = getItemById(id);
        if (item == null) return null;
        ItemReportService reportService = new ItemReportService();
        return reportService.buildView(item);
    }

    public Item getItemByBarcode(String barcode) {
        if (barcode == null || barcode.trim().isEmpty()) return null;
        return tx.runInTransaction(() -> itemRepository.findByBarcode(barcode.trim()));
    }

    public List<Item> searchItemsByName(String name) {
        if (name == null || name.trim().isEmpty()) return getAllItems();
        return tx.runInTransaction(() -> itemRepository.findByName(name.trim()));
    }

    public void deleteItem(Long id) {
        Item item = getItemById(id);
        if (item != null) { logger.info("Deleting item: " + item.getName()); tx.runInTransaction(() -> itemRepository.delete(item)); }
        else throw new IllegalArgumentException("Item not found with ID: " + id);
    }

    public List<Supplier> getAllSuppliers() { try { return tx.runInTransaction(() -> partyRepository.findAllSuppliers()); } catch (Exception e) { throw new RuntimeException("Error retrieving suppliers", e); } }

    public List<Supplier> getAllActiveSuppliers() { try { return tx.runInTransaction(() -> partyRepository.findAllActiveSuppliers()); } catch (Exception e) { throw new RuntimeException("Error retrieving active suppliers", e); } }

    public List<ItemFamily> getAllFamilies() { try { return tx.runInTransaction(() -> itemRepository.findAllFamilies()); } catch (Exception e) { throw new RuntimeException("Error retrieving families", e); } }

    public List<ItemCategory> getAllCategories() { try { return tx.runInTransaction(() -> itemRepository.findAllCategories()); } catch (Exception e) { throw new RuntimeException("Error retrieving categories", e); } }

    public List<Unit> getAllUnits() { try { return tx.runInTransaction(() -> itemRepository.findAllUnits()); } catch (Exception e) { throw new RuntimeException("Error retrieving units", e); } }

    public List<Item> searchItems(String filterType,
                                  String searchText,
                                  ItemFamily family,
                                  ItemCategory category,
                                  Supplier supplier,
                                  boolean activeOnly,
                                  boolean lowStock) {
        return tx.runInTransaction(() -> {
            List<Item> items = itemRepository.findAll();
            if (searchText != null && !searchText.isBlank()) {
                String term = searchText.toLowerCase();
                items.removeIf(item -> !matchesFilter(item, filterType, term));
            }
            if (family != null) {
                items.removeIf(item -> item.getFamily() == null || !item.getFamily().getCode().equals(family.getCode()));
            }
            if (category != null) {
                items.removeIf(item -> item.getCategory() == null || !item.getCategory().getCode().equals(category.getCode()));
            }
            if (supplier != null) {
                items.removeIf(item -> item.getSupplier() == null || !item.getSupplier().getId().equals(supplier.getId()));
            }
            if (activeOnly) {
                items.removeIf(item -> !item.isActive());
            }
            if (lowStock) {
                items.removeIf(item -> item.getCurrentStock() >= item.getMinimumStock());
            }
            return items;
        });
    }

    public ItemCounts getItemCounts() {
        return tx.runInTransaction(() -> {
            List<Item> items = itemRepository.findAll();
            int total = items.size();
            int lowStock = 0;
            int inactive = 0;
            for (Item item : items) {
                if (item.getCurrentStock() < item.getMinimumStock()) lowStock++;
                if (!item.isActive()) inactive++;
            }
            return new ItemCounts(total, lowStock, inactive);
        });
    }

    public ImportResult importItems(List<ItemImportDTO> input) {
        if (input == null) return new ImportResult(0, 0, 0);
        return tx.runInTransaction(() -> {
            int imported = 0;
            int skipped = 0;
            int failed = 0;
            for (ItemImportDTO dto : input) {
                try {
                    Item item = mapFromDto(dto);
                    createItem(item);
                    imported++;
                } catch (Exception e) {
                    failed++;
                }
            }
            return new ImportResult(imported, skipped, failed);
        });
    }

    private void validateItem(Item item) {
        List<String> errors = new ArrayList<>();
        if (item == null) throw new IllegalArgumentException("Item cannot be null");
        if (item.getName() == null || item.getName().trim().isEmpty()) errors.add("Item name cannot be null or empty");
        if (item.getName() != null && item.getName().length() > 80) errors.add("Item name must not exceed 80 characters");
        if (item.getDescription() != null && item.getDescription().length() > 500) errors.add("Description must not exceed 500 characters");
        if (item.getFamily() == null) errors.add("Family cannot be null");
        if (item.getCategory() == null) errors.add("Category cannot be null");
        if (item.getUnit() == null) errors.add("Unit cannot be null");
        if (item.getSupplier() == null) errors.add("Supplier cannot be null");
        if (item.getCostPrice() == null) errors.add("Cost price cannot be null"); else if (item.getCostPrice().compareTo(BigDecimal.ZERO) < 0) errors.add("Cost price must be >= 0.00");
        if (item.getSalePrice() == null) errors.add("Sale price cannot be null"); else if (item.getSalePrice().compareTo(BigDecimal.ZERO) < 0) errors.add("Sale price must be >= 0.00");
        if (item.getCostPrice() != null && item.getSalePrice() != null && item.getSalePrice().compareTo(item.getCostPrice()) < 0) errors.add("Sale price must be >= cost price");
        if (item.getIVAPercent() == null) errors.add("IVA percentage cannot be null"); else if (!VALID_IVA_PERCENTAGES.contains(item.getIVAPercent())) errors.add("IVA percentage must be 0, 4, 10, or 21");
        if (item.getCurrentStock() == null) errors.add("Current stock cannot be null"); else if (item.getCurrentStock() < 0) errors.add("Current stock must be >= 0");
        if (item.getMinimumStock() == null) errors.add("Minimum stock cannot be null"); else if (item.getMinimumStock() < 0) errors.add("Minimum stock must be >= 0");
        if (item.getBarcode() != null && !item.getBarcode().trim().isEmpty()) { String barcode = item.getBarcode().trim(); if (!barcode.matches("^[0-9]{13}$")) errors.add("Barcode must be exactly 13 digits"); }
        if (item.getObservations() != null && item.getObservations().length() > 500) errors.add("Notes must not exceed 500 characters");
        if (!errors.isEmpty()) throw new IllegalArgumentException("Validation errors: " + String.join(", ", errors));
    }

    private void validateUniqueConstraints(Item item) {
        if (item.getBarcode() != null && !item.getBarcode().trim().isEmpty()) {
            Item existing = tx.runInTransaction(() -> itemRepository.findByBarcode(item.getBarcode()));
            if (existing != null) throw new IllegalArgumentException("Barcode already exists: " + item.getBarcode());
        }
    }

    private void validateUniqueConstraintsForUpdate(Item item) {
        if (item.getBarcode() != null && !item.getBarcode().trim().isEmpty()) {
            Item existing = tx.runInTransaction(() -> itemRepository.findByBarcode(item.getBarcode()));
            if (existing != null && !existing.getId().equals(item.getId())) throw new IllegalArgumentException("Barcode already exists: " + item.getBarcode());
        }
    }

    public List<Integer> getValidIvaPercentages() { return new ArrayList<>(VALID_IVA_PERCENTAGES); }

    public BigDecimal calculatePriceWithIVA(BigDecimal basePrice, Integer ivaPercent) {
        if (basePrice == null || ivaPercent == null) return BigDecimal.ZERO;
        BigDecimal ivaMultiplier = BigDecimal.ONE.add(BigDecimal.valueOf(ivaPercent).divide(BigDecimal.valueOf(100)));
        return basePrice.multiply(ivaMultiplier);
    }

    private String nextItemCode() {
        List<Item> items = itemRepository.findAll();
        int max = 0;
        for (Item it : items) {
            int n = parseItemCode(it.getCode());
            if (n > max) max = n;
        }
        return formatItemCode(max + 1);
    }

    private int parseItemCode(String code) {
        if (code == null) return 0;
        if (!code.startsWith("ITM")) return 0;
        String num = code.substring(3);
        if (!num.matches("\\d+")) return 0;
        try {
            return Integer.parseInt(num);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String formatItemCode(int n) {
        return String.format("ITM%04d", n);
    }

    private boolean matchesFilter(Item item, String filterType, String term) {
        String name = item.getName() != null ? item.getName().toLowerCase() : "";
        String desc = item.getDescription() != null ? item.getDescription().toLowerCase() : "";
        String barcode = item.getBarcode() != null ? item.getBarcode().toLowerCase() : "";
        if (filterType == null) return name.contains(term) || desc.contains(term) || barcode.contains(term);
        return switch (filterType) {
            case "Name" -> name.contains(term);
            case "Description" -> desc.contains(term);
            case "Barcode" -> barcode.contains(term);
            default -> name.contains(term) || desc.contains(term) || barcode.contains(term);
        };
    }

    private Item mapFromDto(ItemImportDTO dto) {
        if (dto == null) throw new IllegalArgumentException("Item data cannot be null");
        if ((dto.name == null || dto.name.isBlank()) && (dto.description == null || dto.description.isBlank())) {
            throw new IllegalArgumentException("Item name/description required");
        }
        if (dto.familyCode == null || dto.familyCode.isBlank()) throw new IllegalArgumentException("familyCode required");
        if (dto.categoryCode == null || dto.categoryCode.isBlank()) throw new IllegalArgumentException("categoryCode required");
        if (dto.unitSymbol == null || dto.unitSymbol.isBlank()) throw new IllegalArgumentException("unitSymbol required");
        if (dto.supplierCode == null || dto.supplierCode.isBlank()) throw new IllegalArgumentException("supplierCode required");
        if (dto.costPrice == null || dto.costPrice.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("costPrice invalid");
        if (dto.salePrice == null || dto.salePrice.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("salePrice invalid");
        if (dto.ivaPercent == null) throw new IllegalArgumentException("ivaPercent required");
        if (dto.currentStock == null || dto.currentStock < 0) throw new IllegalArgumentException("currentStock invalid");
        if (dto.minimumStock == null || dto.minimumStock < 0) throw new IllegalArgumentException("minimumStock invalid");

        ItemFamily family = itemRepository.findFamilyByCode(dto.familyCode);
        ItemCategory category = itemRepository.findCategoryByCode(dto.categoryCode);
        Unit unit = itemRepository.findUnitBySymbol(dto.unitSymbol);
        Supplier supplier = partyRepository.findSupplierByCode(dto.supplierCode);
        if (family == null || category == null || unit == null || supplier == null) {
            throw new IllegalArgumentException("Invalid references");
        }

        Item item = new Item();
        String name = (dto.description == null || dto.description.isBlank()) ? dto.name : dto.description;
        item.setDescription(name);
        item.setFamily(family);
        item.setCategory(category);
        item.setUnit(unit);
        item.setSupplier(supplier);
        item.setCostPrice(dto.costPrice);
        item.setSalePrice(dto.salePrice);
        item.setIVAPercent(dto.ivaPercent);
        item.setCurrentStock(dto.currentStock);
        item.setMinimumStock(dto.minimumStock);
        item.setBarcode(dto.barcode);
        item.setActive(dto.active == null ? true : dto.active);
        item.setObservations(dto.observations);
        return item;
    }

    public static class ItemCounts {
        public final int total;
        public final int lowStock;
        public final int inactive;
        public ItemCounts(int total, int lowStock, int inactive) {
            this.total = total;
            this.lowStock = lowStock;
            this.inactive = inactive;
        }
    }
}
