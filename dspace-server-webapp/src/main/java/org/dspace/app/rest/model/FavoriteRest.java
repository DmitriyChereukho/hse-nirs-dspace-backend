package org.dspace.app.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.dspace.app.rest.RestResourceController;
import org.springframework.hateoas.Link;

import java.util.UUID;

/**
 * REST model for Favorite entity.
 */
public class FavoriteRest extends BaseObjectRest<Integer> {

    public static final String NAME = "favorite";
    public static final String CATEGORY = "core";
    public static final String PLURAL_NAME = "favorites";

    @JsonProperty("userID")
    private UUID userId;

    @JsonProperty("itemID")
    private UUID itemId;

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