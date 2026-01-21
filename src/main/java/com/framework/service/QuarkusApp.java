package com.framework.service;

import com.framework.data.staticdata.StaticActionData;
import com.framework.data.staticdata.StaticItemData;
import com.framework.data.staticdata.StaticLootTableData;
import com.framework.data.staticdata.StaticSkillData;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import io.quarkus.runtime.StartupEvent;

/**
 * Main application class for Quarkus.
 * Handles application startup initialization.
 */
@ApplicationScoped
public class QuarkusApp {
    
    /**
     * Initializes static data on application startup.
     * @param evt The startup event
     */
    void onStart(@Observes StartupEvent evt) {
        // Initialize all static data factories
        StaticItemData.initialize();
        StaticSkillData.initialize();
        StaticLootTableData.initialize();
        StaticActionData.initialize();
        
        System.out.println("Game framework initialized successfully!");
    }
}
