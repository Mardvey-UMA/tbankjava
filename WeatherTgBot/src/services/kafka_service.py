import asyncio
import logging
from typing import Optional, Callable, Dict, Any
from confluent_kafka import Consumer, DeserializingConsumer
from confluent_kafka.schema_registry import SchemaRegistryClient
from confluent_kafka.schema_registry.avro import AvroDeserializer
from confluent_kafka.serialization import StringDeserializer
from confluent_kafka.error import KafkaError

from src.config.config import Config
from src.models.weather import KafkaWeatherResponseDTO

logger = logging.getLogger(__name__)

class KafkaService:
    """Сервис для работы с Kafka"""
    
    def __init__(self, config: Config):
        self.config = config
        self.consumer: Optional[DeserializingConsumer] = None
        self.schema_registry_client: Optional[SchemaRegistryClient] = None
        self.avro_deserializer: Optional[AvroDeserializer] = None
        self._running = False
        self._initialized = False
        logger.info("KafkaService инициализирован")
    
    async def _setup_schema_registry(self):
        """Настройка Schema Registry"""
        logger.info("Начинаем настройку Schema Registry")
        try:
            self.schema_registry_client = SchemaRegistryClient({
                'url': self.config.kafka.schema_registry_url,
                'timeout': 5  # 5 секунд таймаут
            })
            logger.info(f"Schema Registry клиент создан для {self.config.kafka.schema_registry_url}")
            
            # Получаем схему для топика ответов
            schema = self.schema_registry_client.get_latest_version(
                f"{self.config.kafka.response_topic}-value"
            )
            logger.info(f"Получена схема для топика {self.config.kafka.response_topic}")
            
            self.avro_deserializer = AvroDeserializer(
                schema_registry_client=self.schema_registry_client,
                schema_str=schema.schema.schema_str
            )
            logger.info("Avro десериализатор создан")
            
        except Exception as e:
            logger.error(f"Ошибка при настройке Schema Registry: {e}")
            raise  # Пробрасываем исключение вместо создания десериализатора с пустой схемой
    
    async def _setup_consumer(self):
        """Настройка консьюмера"""
        logger.info("Начинаем настройку Kafka консьюмера")
        try:
            self.consumer = DeserializingConsumer({
                'bootstrap.servers': self.config.kafka.bootstrap_servers,
                'group.id': self.config.kafka.group_id,
                'auto.offset.reset': 'earliest',
                'key.deserializer': StringDeserializer('utf-8'),
                'value.deserializer': self.avro_deserializer,
                'session.timeout.ms': 6000,  # 6 секунд таймаут сессии
                'heartbeat.interval.ms': 2000,  # 2 секунды интервал heartbeat
                'max.poll.interval.ms': 300000  # 5 минут максимальный интервал между poll
            })
            logger.info(f"Kafka консьюмер создан для {self.config.kafka.bootstrap_servers}")
            
            self.consumer.subscribe([self.config.kafka.response_topic])
            logger.info(f"Подписка на топик {self.config.kafka.response_topic} создана")
            
        except Exception as e:
            logger.error(f"Ошибка при настройке консьюмера: {e}")
            raise
    
    async def initialize(self):
        """Асинхронная инициализация сервиса"""
        if not self._initialized:
            logger.info("Начинаем инициализацию Kafka сервиса")
            try:
                await self._setup_schema_registry()
                await self._setup_consumer()
                self._initialized = True
                logger.info("Kafka сервис успешно инициализирован")
            except Exception as e:
                logger.error(f"Ошибка при инициализации Kafka сервиса: {e}")
                self._initialized = False
                raise
    
    async def start(self, message_handler: Callable[[str, KafkaWeatherResponseDTO], None]):
        """Запуск обработки сообщений"""
        if not self._initialized:
            logger.info("Kafka сервис не инициализирован, пропускаем запуск")
            return
            
        logger.info("Запуск обработки сообщений Kafka")
        try:
            self._running = True
            
            while self._running:
                try:
                    # Используем asyncio.sleep для предотвращения блокировки
                    await asyncio.sleep(0.1)
                    
                    msg = self.consumer.poll(0.1)  # Уменьшаем время ожидания
                    if msg is None:
                        continue
                    if msg.error():
                        if msg.error().code() == KafkaError._PARTITION_EOF:
                            logger.debug("Достигнут конец партиции")
                            continue
                        logger.error(f"Ошибка при получении сообщения: {msg.error()}")
                        continue
                    
                    # Получаем telegram_id из ключа сообщения
                    telegram_id = msg.key()
                    logger.debug(f"Получено сообщение для пользователя {telegram_id}")
                    
                    # Десериализуем сообщение в нашу модель
                    weather_data = KafkaWeatherResponseDTO.parse_obj(msg.value())
                    
                    # Обрабатываем сообщение
                    await message_handler(telegram_id, weather_data)
                    logger.debug(f"Сообщение для пользователя {telegram_id} обработано")
                    
                except Exception as e:
                    logger.error(f"Ошибка при обработке сообщения: {e}")
                    await asyncio.sleep(1)
        except Exception as e:
            logger.error(f"Критическая ошибка в Kafka сервисе: {e}")
            self._running = False
            raise
    
    def stop(self):
        """Остановка обработки сообщений"""
        logger.info("Остановка Kafka сервиса")
        self._running = False
        if self.consumer:
            try:
                self.consumer.close()
                logger.info("Kafka консьюмер закрыт")
            except Exception as e:
                logger.error(f"Ошибка при закрытии консьюмера: {e}") 