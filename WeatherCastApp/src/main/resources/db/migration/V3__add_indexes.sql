CREATE INDEX IF NOT EXISTS idx_city_weather_date ON public.city_weather (date);
CREATE INDEX IF NOT EXISTS idx_city_weather_city_date ON public.city_weather (city_id, date);
CREATE INDEX IF NOT EXISTS idx_city_weather_city_date_time ON public.city_weather (city_id, date, time);

CREATE INDEX IF NOT EXISTS idx_subscriptions_user ON public.subscriptions (user_id);
CREATE INDEX IF NOT EXISTS idx_subscriptions_time ON public.subscriptions (notification_time);
CREATE INDEX IF NOT EXISTS idx_subscriptions_active ON public.subscriptions (is_active) WHERE is_active = TRUE;