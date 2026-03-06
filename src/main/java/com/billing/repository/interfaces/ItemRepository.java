package com.billing.repository.interfaces;

import java.math.BigDecimal;
import java.util.List;

import com.billing.model.item.Item;
import com.billing.model.item.ItemCategory;
import com.billing.model.item.ItemFamily;
import com.billing.model.item.Unit;
import com.billing.repository.Repository;

public interface ItemRepository extends Repository<Item, Long> {
    Item findById(Long id);
    Item findByBarcode(String barcode);
    List<Item> findByName(String name);
    List<Item> findAllActive();
    List<Item> findAllInactive();
    List<Item> findLowStockItems();
    List<Item> findBySupplier(Long supplierId);
    List<Item> findByFamilyCode(String familyCode);
    List<Item> findByCategoryCode(String categoryCode);
    List<Item> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);
    void updateCode(Long id, String code);

    ItemFamily findFamilyByCode(String code);
    ItemCategory findCategoryByCode(String code);
    Unit findUnitBySymbol(String symbol);

    List<ItemFamily> findAllFamilies();
    List<ItemCategory> findAllCategories();
    List<Unit> findAllUnits();
}
