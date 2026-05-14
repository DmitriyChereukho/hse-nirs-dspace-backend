package org.dspace.app.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class FavoriteItemResponseRest {

    @JsonProperty("item_id")
    private UUID itemId;

    @JsonProperty("title")
    private String title;

    @JsonProperty("uri")
    private String uri;

    // Конструкторы
    public FavoriteItemResponseRest() {
    }

    public FavoriteItemResponseRest(UUID itemId, String title, String uri) {
        this.itemId = itemId;
        this.title = title;
        this.uri = uri;
    }

    // Геттеры и сеттеры
    public UUID getItemId() {
        return itemId;
    }

    public void setItemId(UUID itemId) {
        this.itemId = itemId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}