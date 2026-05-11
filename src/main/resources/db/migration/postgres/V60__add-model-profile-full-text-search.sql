ALTER TABLE model_profiles
    ADD COLUMN IF NOT EXISTS search_vector tsvector;

CREATE OR REPLACE FUNCTION update_model_profile_search_vector()
    RETURNS trigger AS $$
BEGIN
    NEW.search_vector :=
            setweight(to_tsvector('simple', coalesce(NEW.model_name, '')), 'A') ||
            setweight(to_tsvector('simple', coalesce(NEW.bio, '')), 'B') ||
            setweight(to_tsvector('simple', coalesce(NEW.city, '')), 'B') ||
            setweight(to_tsvector('simple', coalesce(NEW.hair_color, '')), 'C') ||
            setweight(to_tsvector('simple', coalesce(NEW.body_type, '')), 'C') ||
            setweight(to_tsvector('simple', coalesce(NEW.skin_tone, '')), 'C') ||
            setweight(to_tsvector('simple', coalesce(NEW.gender, '')), 'C');

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_model_profile_search_vector ON model_profiles;

CREATE TRIGGER trg_model_profile_search_vector
    BEFORE INSERT OR UPDATE ON model_profiles
    FOR EACH ROW
EXECUTE FUNCTION update_model_profile_search_vector();

CREATE INDEX IF NOT EXISTS idx_model_profile_search_vector
    ON model_profiles
        USING GIN (search_vector);

UPDATE model_profiles
SET search_vector =
        setweight(to_tsvector('simple', coalesce(model_name, '')), 'A') ||
        setweight(to_tsvector('simple', coalesce(bio, '')), 'B') ||
        setweight(to_tsvector('simple', coalesce(city, '')), 'B') ||
        setweight(to_tsvector('simple', coalesce(hair_color, '')), 'C') ||
        setweight(to_tsvector('simple', coalesce(body_type, '')), 'C') ||
        setweight(to_tsvector('simple', coalesce(skin_tone, '')), 'C') ||
        setweight(to_tsvector('simple', coalesce(gender, '')), 'C');
