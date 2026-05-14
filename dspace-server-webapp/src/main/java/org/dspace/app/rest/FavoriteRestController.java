package org.dspace.app.rest;

import org.dspace.app.rest.model.FavoriteItemResponseRest;
import org.dspace.app.rest.model.FavoriteRest;
import org.dspace.content.Item;
import org.dspace.content.MetadataValue;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.content.service.ItemService;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.dao.FavoriteDAO;
import org.dspace.eperson.factory.EPersonServiceFactory;
import org.dspace.eperson.service.EPersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteRestController {

    @Autowired
    private FavoriteDAO favoriteDAO;

    private final EPersonService ePersonService = EPersonServiceFactory.getInstance().getEPersonService();
    private final ItemService itemService = ContentServiceFactory.getInstance().getItemService();

    @GetMapping
    @PreAuthorize("hasAuthority('AUTHENTICATED')")
    public ResponseEntity<Boolean> isFavorite(
            @RequestParam UUID userID,
            @RequestParam UUID itemID) throws SQLException {

        try (Context context = new Context()) {
            boolean isFavorite = favoriteDAO.isFavorite(context, userID, itemID);
            return ResponseEntity.ok(isFavorite);
        }
    }

    @PutMapping
    @PreAuthorize("hasAuthority('AUTHENTICATED')")
    public ResponseEntity<Void> addFavorite(
            @RequestBody FavoriteRest favoriteRest) throws SQLException {

        try (Context context = new Context()) {
            EPerson currentUser = ePersonService.find(context, favoriteRest.getUserId());
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            context.setCurrentUser(currentUser);

            favoriteDAO.addFavorite(context, favoriteRest.getUserId(), favoriteRest.getItemId());
            context.complete();
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority('AUTHENTICATED')")
    public ResponseEntity<Void> removeFavorite(
            @RequestParam UUID userID,
            @RequestParam UUID itemID) throws SQLException {

        try (Context context = new Context()) {
            EPerson currentUser = ePersonService.find(context, userID);
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            context.setCurrentUser(currentUser);

            favoriteDAO.removeFavorite(context, userID, itemID);
            context.complete();
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }

    @GetMapping("/items")
    @PreAuthorize("hasAuthority('AUTHENTICATED')")
    public ResponseEntity<List<FavoriteItemResponseRest>> getUserFavorites(
            @RequestParam UUID userID) throws SQLException {

        try (Context context = new Context()) {
            context.turnOffAuthorisationSystem();

            EPerson user = ePersonService.find(context, userID);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            List<UUID> favoriteItemIds = favoriteDAO.getUserFavorites(context, userID);

            List<FavoriteItemResponseRest> response = new ArrayList<>();
            for (UUID itemId : favoriteItemIds) {
                Item item = itemService.find(context, itemId);
                if (item != null) {
                    String title = getMetadataValue(item, "dc.title");
                    String uri = getMetadataValue(item, "dc.identifier.uri");
                    response.add(new FavoriteItemResponseRest(itemId, title, uri));
                }
            }

            return ResponseEntity.ok(response);
        }
    }

    private String getMetadataValue(Item item, String metadataField) {
        List<MetadataValue> values = itemService.getMetadataByMetadataString(item, metadataField);
        if (values != null && !values.isEmpty()) {
            return values.get(0).getValue();
        }
        return "";
    }
}
