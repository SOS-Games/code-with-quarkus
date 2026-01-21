package com.framework.service;

import com.framework.data.model.Item;
import com.framework.data.model.LootTable;
import com.framework.data.model.Skill;
import com.framework.service.core.StaticDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for StaticDataService.
 */
@QuarkusTest
public class StaticDataServiceTest {
    
    @Inject
    StaticDataService staticDataService;
    
    @Test
    public void testGetItem() {
        // This test will fail until items are added to StaticItemData
        // Uncomment and modify when items are defined:
        /*
        Item item = staticDataService.getItem("copper_ore");
        assertNotNull(item);
        assertEquals("copper_ore", item.getId());
        */
        
        // Test that non-existent item throws exception
        assertThrows(IllegalArgumentException.class, () -> {
            staticDataService.getItem("non_existent_item");
        });
    }
    
    @Test
    public void testGetSkill() {
        // This test will fail until skills are added to StaticSkillData
        // Uncomment and modify when skills are defined:
        /*
        Skill skill = staticDataService.getSkill("woodcutting");
        assertNotNull(skill);
        assertEquals("woodcutting", skill.getId());
        */
        
        // Test that non-existent skill throws exception
        assertThrows(IllegalArgumentException.class, () -> {
            staticDataService.getSkill("non_existent_skill");
        });
    }
    
    @Test
    public void testGetLootTable() {
        // This test will fail until loot tables are added to StaticLootTableData
        // Uncomment and modify when loot tables are defined:
        /*
        LootTable lootTable = staticDataService.getLootTable("common_mining_drops");
        assertNotNull(lootTable);
        assertEquals("common_mining_drops", lootTable.getId());
        */
        
        // Test that non-existent loot table throws exception
        assertThrows(IllegalArgumentException.class, () -> {
            staticDataService.getLootTable("non_existent_loot_table");
        });
    }
    
    @Test
    public void testGetAllItems() {
        // Test that getAllItems returns a collection
        assertNotNull(staticDataService.getAllItems());
        // Initially empty until items are added to StaticItemData
    }
}
