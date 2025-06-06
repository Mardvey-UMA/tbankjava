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


CREATE TABLE IF NOT EXISTS public.users (
                                            id BIGSERIAL PRIMARY KEY,
                                            telegram_id BIGINT UNIQUE NOT NULL,
                                            created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
    );


CREATE TABLE IF NOT EXISTS public.subscription (
                                                   id BIGSERIAL PRIMARY KEY,
                                                   user_id BIGINT NOT NULL,
                                                   city_id BIGINT NOT NULL,
                                                   notification_time TIME NOT NULL,
                                                   time_zone VARCHAR(50) NOT NULL DEFAULT 'UTC',
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE,
    CONSTRAINT fk_city FOREIGN KEY (city_id) REFERENCES public.city(id) ON DELETE CASCADE,
    CONSTRAINT unique_user_city UNIQUE (user_id, city_id)
    );

