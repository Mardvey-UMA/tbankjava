
CREATE TABLE IF NOT EXISTS public.weather_cast (
    weather_cast_id BIGINT PRIMARY KEY,
    temperature DECIMAL,
    humidity DECIMAL,
    wind_speed DECIMAL
);

CREATE TABLE IF NOT EXISTS public.city (
    city_id BIGINT PRIMARY KEY,
    longitude DECIMAL,
    latitude DECIMAL
);

CREATE TABLE IF NOT EXISTS public.cast_city (
    cast_city_id BIGINT PRIMARY KEY,
    "date" DATE,
    weather_cast_id_weather_cast BIGINT NOT NULL,
    city_id_city BIGINT NOT NULL,
        CONSTRAINT weather_cast_fk FOREIGN KEY (weather_cast_id_weather_cast)
        REFERENCES public.weather_cast (weather_cast_id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
        CONSTRAINT city_fk FOREIGN KEY (city_id_city)
        REFERENCES public.city (city_id)
        ON UPDATE CASCADE ON DELETE RESTRICT
    );
