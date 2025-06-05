CREATE INDEX IF NOT EXISTS idx_city_weather_date ON public.city_weather (date);
CREATE INDEX IF NOT EXISTS idx_city_weather_city_date ON public.city_weather (city_id, date);
CREATE INDEX IF NOT EXISTS idx_city_weather_city_date_time ON public.city_weather (city_id, date, time);

CREATE INDEX IF NOT EXISTS idx_subscription_user ON public.subscription (user_id);
CREATE INDEX IF NOT EXISTS idx_subscription_time ON public.subscription (notification_time);
CREATE INDEX IF NOT EXISTS idx_subscription_active ON public.subscription (is_active) WHERE is_active = TRUE;