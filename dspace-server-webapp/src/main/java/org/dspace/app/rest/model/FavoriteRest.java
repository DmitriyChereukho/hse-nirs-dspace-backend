package org.dspace.app.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class FavoriteRest {

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
}
