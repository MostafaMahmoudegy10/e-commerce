ALTER TABLE product
    ADD COLUMN IF NOT EXISTS search_vector tsvector;

CREATE OR REPLACE FUNCTION update_product_search_vector()
    RETURNS trigger AS $$
BEGIN
    NEW.search_vector :=
            setweight(to_tsvector('simple', coalesce(NEW.product_name_en, '')), 'A') ||
            setweight(to_tsvector('simple', coalesce(NEW.product_name_ar, '')), 'A') ||
            setweight(to_tsvector('simple', coalesce(NEW.product_description_en, '')), 'B') ||
            setweight(to_tsvector('simple', coalesce(NEW.product_description_ar, '')), 'B');

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_product_search_vector ON product;

CREATE TRIGGER trg_product_search_vector
    BEFORE INSERT OR UPDATE ON product
    FOR EACH ROW
EXECUTE FUNCTION update_product_search_vector();

CREATE INDEX IF NOT EXISTS idx_product_search_vector
    ON product
        USING GIN (search_vector);

UPDATE product
SET search_vector =
        setweight(to_tsvector('simple', coalesce(product_name_en, '')), 'A') ||
        setweight(to_tsvector('simple', coalesce(product_name_ar, '')), 'A') ||
        setweight(to_tsvector('simple', coalesce(product_description_en, '')), 'B') ||
        setweight(to_tsvector('simple', coalesce(product_description_ar, '')), 'B');