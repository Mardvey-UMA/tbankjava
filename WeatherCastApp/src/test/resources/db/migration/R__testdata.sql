TRUNCATE public.city_weather RESTART IDENTITY CASCADE;
TRUNCATE public.weather       RESTART IDENTITY CASCADE;
TRUNCATE public.city          RESTART IDENTITY CASCADE;

INSERT INTO public.city (id, name, longitude, latitude) VALUES
                                                            (1, 'Обнинск',            55.55, 55.55),
                                                            (2, 'Петушки',  55.55, 55.55),
                                                            (3, 'Саратов',       55.55, 55.55);

INSERT INTO public.weather (
    id, temperature, humidity, wind_speed, uv_index,
    wind_direction, feels_like, pressure, created_at
) VALUES
      (1,  -5.0, 85, 3.5, 0.20, 180,  -9.0, 1015, '2025-06-06 06:00:00+00'),
      (2,  -3.0, 80, 4.0, 0.30, 190,  -6.0, 1010, '2025-06-06 06:00:00+00'),
      (3,  10.0, 60, 5.0, 3.20, 170,   8.0, 1005, '2025-06-06 06:00:00+00'),
      (4,  12.0, 55, 4.2, 4.10, 160,  11.0, 1003, '2025-06-06 06:00:00+00'),
      (5,  15.0, 50, 2.8, 5.50, 200,  14.0, 1008, '2025-06-06 06:00:00+00'),
      (6,  17.0, 45, 3.0, 6.10, 210,  16.0, 1009, '2025-06-06 06:00:00+00'),
      (7, -10.0, 90, 6.0, 0.10, 140, -15.0, 1020, '2025-06-06 06:00:00+00'),
      (8,  -8.0, 88, 5.5, 0.15, 130, -12.0, 1018, '2025-06-06 06:00:00+00'),
      (9,  20.0, 40, 2.0, 7.00, 220,  19.0, 1002, '2025-06-06 06:00:00+00'),
      (10, 22.0, 38, 1.8, 7.50, 225,  21.0, 1001, '2025-06-06 06:00:00+00');

INSERT INTO public.city_weather (
    id, date,       time,      weather_id, city_id
) VALUES
      (1, '2025-12-01', '06:00:00',  1, 1),
      (2, '2025-12-01', '09:00:00',  2, 1),
      (3, '2025-06-02', '14:00:00',  9, 1),
      (4, '2025-06-02', '15:00:00',  9, 1),

      (5, '2025-04-15', '12:00:00',  3, 2),
      (6, '2025-04-15', '15:00:00',  4, 2),
      (7, '2025-01-20', '12:00:00',  8, 2),

      (8, '2025-05-10', '12:00:00',  5, 3),
      (9, '2025-05-10', '18:00:00',  6, 3),
      (10, '2025-01-20', '06:00:00',  7, 3);
