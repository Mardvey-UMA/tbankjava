import asyncio
import logging
from aiogram import Bot, Dispatcher
from aiogram.fsm.storage.memory import MemoryStorage
from typing import Any, Awaitable, Callable, Dict

from src.config.config import Config
from src.services.weather_service import WeatherService
from src.services.kafka_service import KafkaService
from src.handlers import start, weather, subscription
from src.middleware.error_handler import ErrorHandlerMiddleware

logger = logging.getLogger(__name__)

class ServiceMiddleware:
    """Middleware для передачи сервисов в обработчики"""
    
    def __init__(self, weather_service: WeatherService):
        self.weather_service = weather_service
    
    async def __call__(
        self,
        handler: Callable[[Any, Dict[str, Any]], Awaitable[Any]],
        event: Any,
        data: Dict[str, Any]
    ) -> Any:
        data["weather_service"] = self.weather_service
        return await handler(event, data)

class WeatherBot:
    """Основной класс бота"""
    
    def __init__(self, config: Config):
        self.config = config
        self.bot = Bot(token=config.tg_bot.token)
        self.dp = Dispatcher(storage=MemoryStorage())
        
        # Инициализируем сервисы
        self.weather_service = WeatherService(config)
        self.kafka_service = KafkaService(config)
        
        # Регистрируем обработчики
        self._setup_handlers()
        
        # Регистрируем middleware
        self._setup_middleware()
    
    def _setup_handlers(self):
        """Регистрация обработчиков"""
        self.dp.include_router(start.router)
        self.dp.include_router(weather.router)
        self.dp.include_router(subscription.router)
    
    def _setup_middleware(self):
        """Регистрация middleware"""
        # Middleware для обработки ошибок
        self.dp.message.middleware(ErrorHandlerMiddleware())
        self.dp.callback_query.middleware(ErrorHandlerMiddleware())
        
        # Middleware для передачи сервисов
        service_middleware = ServiceMiddleware(self.weather_service)
        self.dp.message.middleware(service_middleware)
        self.dp.callback_query.middleware(service_middleware)
    
    async def _handle_kafka_message(self, message: dict):
        """Обработка сообщений из Kafka"""
        try:
            # Получаем данные из сообщения
            user_id = message.get('userId')
            forecast = message.get('forecast')
            
            if not user_id or not forecast:
                logger.error(f"Некорректное сообщение: {message}")
                return
            
            # Форматируем прогноз
            forecast_text = (
                f"🌤 Прогноз погоды для {forecast.get('cityName')}:\n\n"
                f"📅 Дата: {forecast.get('date')}\n"
                f"⏰ Время: {forecast.get('time')}\n"
                f"🌡 Температура: {forecast.get('temp')}°C\n"
                f"🌡 Ощущается как: {forecast.get('feelsLike')}°C\n"
                f"💨 Ветер: {forecast.get('windSpeed')} м/с\n"
                f"💧 Влажность: {forecast.get('humidity')}%\n"
                f"🌪 Давление: {forecast.get('pressure')} мм рт.ст.\n"
                f"☀️ УФ-индекс: {forecast.get('uvIndex')}"
            )
            
            # Отправляем прогноз пользователю
            await self.bot.send_message(user_id, forecast_text)
            
        except Exception as e:
            logger.error(f"Ошибка при обработке сообщения из Kafka: {e}")
    
    async def start(self):
        """Запуск бота"""
        try:
            # Временно отключаем обработку сообщений из Kafka
            # kafka_task = asyncio.create_task(
            #     self.kafka_service.start(self._handle_kafka_message)
            # )
            
            # Запускаем бота
            await self.dp.start_polling(self.bot)
            
        except Exception as e:
            logger.error(f"Ошибка при запуске бота: {e}")
            raise
        finally:
            # Останавливаем обработку сообщений из Kafka
            # self.kafka_service.stop()
            # await kafka_task
            pass
    
    async def stop(self):
        """Остановка бота"""
        await self.bot.session.close()
        await self.weather_service.close()

def run_bot(config: Config):
    bot = WeatherBot(config)
    asyncio.run(bot.start()) 