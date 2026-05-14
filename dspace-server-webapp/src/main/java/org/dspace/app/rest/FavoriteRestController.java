package org.dspace.app.rest;

import org.dspace.app.rest.model.FavoriteRest;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.factory.EPersonServiceFactory;
import org.dspace.eperson.service.EPersonService;
import org.dspace.eperson.dao.FavoriteDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/core/favorites")
public class FavoriteRestController {

    @Autowired
    private FavoriteDAO favoriteDAO;

    private final EPersonService ePersonService = EPersonServiceFactory.getInstance().getEPersonService();

    @GetMapping
    @PreAuthorize("hasAuthority('AUTHENTICATED')")
    public ResponseEntity<Boolean> isFavorite(
            @RequestParam UUID user_id,
            @RequestParam UUID item_id) throws SQLException {

        try (Context context = new Context()) {
            boolean isFavorite = favoriteDAO.isFavorite(context, user_id, item_id);
            return ResponseEntity.ok(isFavorite);
        }
    }

    @PutMapping
    @PreAuthorize("hasAuthority('AUTHENTICATED')")
    public ResponseEntity<Void> addFavorite(
            @RequestBody FavoriteRest favoriteRest) throws SQLException {

        try (Context context = new Context()) {
            // Используем EPersonService для поиска пользователя
            EPerson currentUser = ePersonService.find(context, favoriteRest.getUserId());
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
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
            @RequestParam UUID user_id,
            @RequestParam UUID item_id) throws SQLException {

        try (Context context = new Context()) {
            EPerson currentUser = ePersonService.find(context, user_id);
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            context.setCurrentUser(currentUser);

            favoriteDAO.removeFavorite(context, user_id, item_id);
            context.complete();
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }

    @GetMapping("/user/{user_id}")
    @PreAuthorize("hasAuthority('AUTHENTICATED')")
    public ResponseEntity<List<UUID>> getUserFavorites(
            @PathVariable("user_id") UUID userId) throws SQLException {

        try (Context context = new Context()) {
            // Опционально: проверяем, что пользователь существует
            EPerson user = ePersonService.find(context, userId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            List<UUID> favorites = favoriteDAO.getUserFavorites(context, userId);
            return ResponseEntity.ok(favorites);
        }
    }
}