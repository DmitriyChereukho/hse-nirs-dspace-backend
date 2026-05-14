package org.dspace.eperson.dao;

import org.dspace.core.Context;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

/**
 * DAO interface for Favorite operations.
 */
public interface FavoriteDAO {

    boolean isFavorite(Context context, UUID userId, UUID itemId) throws SQLException;

    void addFavorite(Context context, UUID userId, UUID itemId) throws SQLException;

    void removeFavorite(Context context, UUID userId, UUID itemId) throws SQLException;

    List<UUID> getUserFavorites(Context context, UUID userId) throws SQLException;
}