package com.framework.service.repos;

import com.framework.data.entity.AgentStateEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Repository for AgentStateEntity.
 */
@ApplicationScoped
public class AgentStateRepository implements PanacheRepositoryBase<AgentStateEntity, String> {
    // Panache provides methods like persist(), findById(), findAll(), etc., automatically.
}
