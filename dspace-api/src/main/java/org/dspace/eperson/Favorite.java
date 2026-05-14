package org.dspace.eperson;

import java.util.Date;
import java.util.UUID;
import jakarta.persistence.*;

/**
 * Entity class representing a user favorite (bookmark) on an item.
 * Maps to the "favorites" table.
 * This is a join table between EPerson and Item, not a DSpaceObject.
 */
@Entity
@Table(name = "favorites")
@IdClass(FavoriteId.class)
public class Favorite {

    @Id
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Id
    @Column(name = "item_id", nullable = false)
    private UUID itemId;

    @Column(name = "created_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    // Конструкторы
    public Favorite() {}

    public Favorite(UUID userId, UUID itemId) {
        this.userId = userId;
        this.itemId = itemId;
        this.createdAt = new Date();
    }

    // Геттеры и сеттеры
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}