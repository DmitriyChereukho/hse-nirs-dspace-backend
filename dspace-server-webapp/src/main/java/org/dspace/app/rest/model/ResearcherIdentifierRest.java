/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Represents a single external researcher identifier with its value and a
 * direct URL to the researcher's profile on the corresponding platform.
 */
@JsonInclude(Include.NON_NULL)
public class ResearcherIdentifierRest {

    private String value;
    private String url;

    public ResearcherIdentifierRest() {
    }

    public ResearcherIdentifierRest(String value, String url) {
        this.value = value;
        this.url = url;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
