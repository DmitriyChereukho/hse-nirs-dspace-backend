/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.dspace.app.rest.model.ResearcherIdentifierRest;
import org.dspace.content.Item;
import org.dspace.content.MetadataValue;
import org.dspace.content.service.ItemService;
import org.dspace.core.Context;
import org.dspace.content.factory.ContentServiceFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST endpoint that returns external researcher identifier fields for a given
 * researcher profile item, together with resolved profile URLs for each system.
 *
 * <p>Supported identifiers:
 * <ul>
 *   <li>ORCID (person.identifier.orcid) — https://orcid.org/
 *   <li>Scopus Author ID (person.identifier.scopus-author-id) — https://www.scopus.com/
 *   <li>Web of Science ResearcherID (person.identifier.rid) — https://www.webofscience.com/
 *   <li>Google Scholar (person.identifier.gsid) — https://scholar.google.com/
 *   <li>SPIN / RSCI eLIBRARY.RU (person.identifier.spin) — https://elibrary.ru/
 * </ul>
 *
 * <p>GET /api/researcher-identifiers?itemId={uuid}
 */
@RestController
@RequestMapping("/api/researcher-identifiers")
public class ResearcherIdentifiersRestController {

    private final ItemService itemService = ContentServiceFactory.getInstance().getItemService();

    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<Map<String, ResearcherIdentifierRest>> getIdentifiers(
            @RequestParam UUID itemId) throws SQLException {

        try (Context context = new Context()) {
            context.turnOffAuthorisationSystem();

            Item item = itemService.find(context, itemId);
            if (item == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            Map<String, ResearcherIdentifierRest> identifiers = new HashMap<>();

            addIdentifier(item, "person.identifier.orcid", "orcid",
                    value -> "https://orcid.org/" + value, identifiers);

            addIdentifier(item, "person.identifier.scopus-author-id", "scopus",
                    value -> "https://www.scopus.com/authid/detail.uri?authorId=" + value, identifiers);

            addIdentifier(item, "person.identifier.rid", "researcherId",
                    value -> "https://www.webofscience.com/wos/author/record/" + value, identifiers);

            addIdentifier(item, "person.identifier.gsid", "googleScholar",
                    value -> "https://scholar.google.com/citations?user=" + value, identifiers);

            addIdentifier(item, "person.identifier.spin", "spin",
                    value -> "https://elibrary.ru/author_profile.asp?id=" + value, identifiers);

            return ResponseEntity.ok(identifiers);
        }
    }

    private void addIdentifier(Item item, String metadataField, String key,
                                java.util.function.Function<String, String> urlBuilder,
                                Map<String, ResearcherIdentifierRest> result) {
        List<MetadataValue> values = itemService.getMetadataByMetadataString(item, metadataField);
        if (values != null && !values.isEmpty()) {
            String value = values.get(0).getValue();
            if (value != null && !value.isBlank()) {
                result.put(key, new ResearcherIdentifierRest(value, urlBuilder.apply(value)));
            }
        }
    }
}
