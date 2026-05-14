package org.dspace.app.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.dspace.app.rest.RestResourceController;
import org.springframework.hateoas.Link;

import java.util.Date;
import java.util.UUID;

/**
 * REST model for Favorite entity.
 */
public class FavoriteRest extends BaseObjectRest<Integer> {

    public static final String NAME = "favorite";
    public static final String CATEGORY = "core";
    public static final String PLURAL_NAME = "favorites";

    @JsonProperty("user_id")
    private UUID userId;

    @JsonProperty("item_id")
    private UUID itemId;

    @JsonProperty("created_at")
    private Date createdAt;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getItemId() {
        return itemId;
    }

    public void setItemId(UUID itemId) {
        this.itemId = itemId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String getType() {
        return NAME;
    }

    @Override
    public String getCategory() {
        return CATEGORY;
    }

    @Override
    public String getTypePlural() {
        return PLURAL_NAME;
    }

    @Override
    public Class getController() {
        return RestResourceController.class;
    }
}