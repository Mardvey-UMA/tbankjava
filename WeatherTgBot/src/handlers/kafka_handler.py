import logging
from typing import Optional
from aiogram import Bot
from datetime import datetime

from src.models.weather import KafkaWeatherResponseDTO

logger = logging.getLogger(__name__)

class KafkaMessageHandler:
    """Обработчик сообщений из Kafka"""
    
    def __init__(self, bot: Bot):
        self.bot = bot
    
    async def handle_message(self, telegram_id: str, weather_data: KafkaWeatherResponseDTO):
        """Обработка сообщения из Kafka"""
        try:
            # Формируем сообщение с прогнозом погоды
            message = "🌤 Прогноз погоды:\n\n"
            
            for forecast in weather_data.forecasts:
                # Преобразуем дату в читаемый формат
                date = forecast.date.strftime("%Y-%m-%d")
                time = forecast.time.strftime("%H:%M")  # time.strftime работает и для объекта time
                
                message += (
                    f"📅 {date} {time}\n"
                    f"🌡 Температура: {forecast.temp}°C\n"
                    f"🌡 Ощущается как: {forecast.feels_like}°C\n"
                    f"💨 Ветер: {forecast.wind_speed} м/с, {forecast.wind_dir}°\n"
                    f"💧 Влажность: {forecast.humidity}%\n"
                    f"🌪 Давление: {forecast.pressure} Па\n"
                    f"☀️ УФ-индекс: {forecast.uv_index}\n\n"
                )
            
            # Отправляем сообщение пользователю
            await self.bot.send_message(
                chat_id=telegram_id,
                text=message
            )
            
        except Exception as e:
            logger.error(f"Ошибка при обработке сообщения из Kafka: {e}")
            raise 