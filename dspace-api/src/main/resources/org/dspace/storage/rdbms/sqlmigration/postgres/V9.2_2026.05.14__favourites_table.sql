-- Создаем таблицу favorites
CREATE TABLE favorites (
                           user_id UUID NOT NULL,
                           item_id UUID NOT NULL,
                           created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                           PRIMARY KEY (user_id, item_id)
);

-- Добавляем внешние ключи для целостности данных
ALTER TABLE favorites
    ADD CONSTRAINT fk_favorites_user FOREIGN KEY (user_id)
        REFERENCES eperson(uuid) ON DELETE CASCADE;

ALTER TABLE favorites
    ADD CONSTRAINT fk_favorites_item FOREIGN KEY (item_id)
        REFERENCES item(uuid) ON DELETE CASCADE;

-- Создаем индексы для быстрого поиска
CREATE INDEX idx_favorites_user ON favorites(user_id);
CREATE INDEX idx_favorites_item ON favorites(item_id);