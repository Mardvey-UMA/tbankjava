CREATE TABLE IF NOT EXISTS public.weather (
    id BIGSERIAL PRIMARY KEY,
    temperature DECIMAL,
    humidity DECIMAL,
    wind_speed DECIMAL,
    uv_index DECIMAL,
    wind_direction DECIMAL,
    feels_like DECIMAL,
    pressure DECIMAL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
    );

CREATE TABLE IF NOT EXISTS public.city (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL,
    longitude DECIMAL,
    latitude DECIMAL
    );

CREATE TABLE IF NOT EXISTS public.city_weather (
    id BIGSERIAL PRIMARY KEY,
    date DATE NOT NULL,
    time TIME NOT NULL,
    weather_id BIGINT NOT NULL,
    city_id BIGINT NOT NULL,

    CONSTRAINT fk_weather FOREIGN KEY (weather_id) REFERENCES public.weather(id),
    CONSTRAINT fk_city FOREIGN KEY (city_id) REFERENCES public.city(id),
    CONSTRAINT unique_city_date_time UNIQUE (city_id, date, time)
);

