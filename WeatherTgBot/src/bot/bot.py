import asyncio
import logging
from aiogram import Bot, Dispatcher
from aiogram.fsm.storage.memory import MemoryStorage
from typing import Any, Awaitable, Callable, Dict

from src.config.config import Config
from src.services.weather_service import WeatherService
from src.services.kafka_service import KafkaService
from src.handlers import start, weather, subscription
from src.handlers.kafka_handler import KafkaMessageHandler
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
        self.kafka_handler = KafkaMessageHandler(self.bot)
        
        # Регистрируем обработчики
        self._setup_handlers()
        
        # Регистрируем middleware
        self._setup_middleware()
        
        logger.info("WeatherBot инициализирован")
    
    def _setup_handlers(self):
        """Регистрация обработчиков"""
        self.dp.include_router(start.router)
        self.dp.include_router(weather.router)
        self.dp.include_router(subscription.router)
        logger.info("Обработчики зарегистрированы")
    
    def _setup_middleware(self):
        """Регистрация middleware"""
        # Middleware для обработки ошибок
        self.dp.message.middleware(ErrorHandlerMiddleware())
        self.dp.callback_query.middleware(ErrorHandlerMiddleware())
        
        # Middleware для передачи сервисов
        service_middleware = ServiceMiddleware(self.weather_service)
        self.dp.message.middleware(service_middleware)
        self.dp.callback_query.middleware(service_middleware)
        logger.info("Middleware зарегистрированы")
    
    async def _initialize_kafka(self):
        """Асинхронная инициализация Kafka"""
        logger.info("Начинаем инициализацию Kafka")
        try:
            await self.kafka_service.initialize()
            kafka_task = asyncio.create_task(
                self.kafka_service.start(self.kafka_handler.handle_message)
            )
            logger.info("Kafka успешно инициализирована")
            return kafka_task
        except Exception as e:
            logger.error(f"Ошибка при инициализации Kafka: {e}")
            return None
    
    async def start(self):
        """Запуск бота"""
        kafka_task = None
        try:
            # Запускаем бота
            logger.info("Запуск бота")
            polling_task = asyncio.create_task(
                self.dp.start_polling(self.bot)
            )
            
            # Инициализируем Kafka в фоновом режиме
            kafka_task = await self._initialize_kafka()
            
            # Ждем завершения работы бота
            await polling_task
            
        except Exception as e:
            logger.error(f"Ошибка при запуске бота: {e}")
            raise
        finally:
            # Останавливаем обработку сообщений из Kafka
            if kafka_task:
                logger.info("Останавливаем Kafka")
                self.kafka_service.stop()
                try:
                    await asyncio.wait_for(kafka_task, timeout=5.0)
                except asyncio.TimeoutError:
                    logger.error("Таймаут при остановке Kafka сервиса")
                except Exception as e:
                    logger.error(f"Ошибка при остановке Kafka сервиса: {e}")
    
    async def stop(self):
        """Остановка бота"""
        logger.info("Остановка бота")
        await self.bot.session.close()
        await self.weather_service.close()
        logger.info("Бот остановлен")

def run_bot(config: Config):
    bot = WeatherBot(config)
    asyncio.run(bot.start()) 