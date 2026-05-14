package org.dspace.eperson.dao.impl;

import org.dspace.core.Context;
import org.dspace.eperson.Favorite;
import org.dspace.eperson.dao.FavoriteDAO;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import jakarta.persistence.Query;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

/**
 * Hibernate implementation of FavoriteDAO.
 * Uses the Hibernate Session obtained from Context.
 */
@Repository
public class FavoriteDAOImpl implements FavoriteDAO {

    @SuppressWarnings("unchecked")
    private Session getHibernateSession(Context context) throws SQLException {
        // DBConnection.getSession() returns the Hibernate Session
        return (Session) context.getDBConnection().getSession();
    }

    @Override
    public boolean isFavorite(Context context, UUID userId, UUID itemId) throws SQLException {
        String hql = "SELECT COUNT(f) FROM Favorite f " +
                "WHERE f.userId = :userId AND f.itemId = :itemId";
        Query query = getHibernateSession(context).createQuery(hql);
        query.setParameter("userId", userId);
        query.setParameter("itemId", itemId);

        Long count = (Long) query.getSingleResult();
        return count > 0;
    }

    @Override
    public void addFavorite(Context context, UUID userId, UUID itemId) throws SQLException {
        // Проверяем, не существует ли уже
        if (isFavorite(context, userId, itemId)) {
            return;
        }

        Favorite favorite = new Favorite(userId, itemId);
        getHibernateSession(context).persist(favorite);
    }

    @Override
    public void removeFavorite(Context context, UUID userId, UUID itemId) throws SQLException {
        String hql = "DELETE FROM Favorite f " +
                "WHERE f.userId = :userId AND f.itemId = :itemId";
        Query query = getHibernateSession(context).createQuery(hql);
        query.setParameter("userId", userId);
        query.setParameter("itemId", itemId);
        query.executeUpdate();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<UUID> getUserFavorites(Context context, UUID userId) throws SQLException {
        if (userId == null) {
            return List.of();
        }
        String hql = "SELECT f.itemId FROM Favorite f " +
                "WHERE f.userId = :userId " +
                "ORDER BY f.createdAt DESC";
        Query query = getHibernateSession(context).createQuery(hql);
        query.setParameter("userId", userId);

        return query.getResultList();
    }
}