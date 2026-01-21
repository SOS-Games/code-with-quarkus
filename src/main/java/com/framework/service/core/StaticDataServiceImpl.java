package com.framework.service.core;

import com.framework.data.model.Item;
import com.framework.data.model.LootTable;
import com.framework.data.model.Skill;
import com.framework.data.staticdata.StaticItemData;
import com.framework.data.staticdata.StaticLootTableData;
import com.framework.data.staticdata.StaticSkillData;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Collection;

/**
 * Implementation of StaticDataService.
 * Delegates to the static data factories.
 */
@ApplicationScoped
public class StaticDataServiceImpl implements StaticDataService {

    @Override
    public Item getItem(String id) {
        return StaticItemData.getItem(id);
    }

    @Override
    public Skill getSkill(String id) {
        return StaticSkillData.getSkill(id);
    }

    @Override
    public LootTable getLootTable(String id) {
        return StaticLootTableData.getLootTable(id);
    }

    @Override
    public Collection<Item> getAllItems() {
        return StaticItemData.getAllItems().values();
    }
}
