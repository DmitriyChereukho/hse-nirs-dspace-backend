package org.dspace.app.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FavoriteItemResponseRest {

    @JsonProperty("title")
    private String title;

    @JsonProperty("uri")
    private String uri;

    public FavoriteItemResponseRest() {
    }

    public FavoriteItemResponseRest(String title, String uri) {
        this.title = title;
        this.uri = uri;
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
