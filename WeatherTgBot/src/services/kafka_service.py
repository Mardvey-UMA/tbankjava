import asyncio
import logging
from typing import Optional, Callable, Dict, Any
from confluent_kafka import Consumer, DeserializingConsumer
from confluent_kafka.schema_registry import SchemaRegistryClient
from confluent_kafka.schema_registry.avro import AvroDeserializer
from confluent_kafka.serialization import StringDeserializer

from src.config.config import Config

logger = logging.getLogger(__name__)

class KafkaService:
    """Сервис для работы с Kafka"""
    
    def __init__(self, config: Config):
        self.config = config
        self.consumer: Optional[DeserializingConsumer] = None
        self.schema_registry_client: Optional[SchemaRegistryClient] = None
        self.avro_deserializer: Optional[AvroDeserializer] = None
        self._running = False
    
    def _setup_schema_registry(self):
        """Настройка Schema Registry"""
        self.schema_registry_client = SchemaRegistryClient({
            'url': self.config.kafka.schema_registry_url
        })
        
        # Получаем схему для топика ответов
        schema = self.schema_registry_client.get_latest_version(
            f"{self.config.kafka.response_topic}-value"
        )
        
        self.avro_deserializer = AvroDeserializer(
            schema_registry_client=self.schema_registry_client,
            schema_str=schema.schema.schema_str
        )
    
    def _setup_consumer(self):
        """Настройка консьюмера"""
        self.consumer = DeserializingConsumer({
            'bootstrap.servers': self.config.kafka.bootstrap_servers,
            'group.id': self.config.kafka.group_id,
            'auto.offset.reset': 'earliest',
            'key.deserializer': StringDeserializer('utf-8'),
            'value.deserializer': self.avro_deserializer
        })
        
        self.consumer.subscribe([self.config.kafka.response_topic])
    
    async def start(self, message_handler: Callable[[Dict[str, Any]], None]):
        """Запуск обработки сообщений"""
        if not self.schema_registry_client:
            self._setup_schema_registry()
        
        if not self.consumer:
            self._setup_consumer()
        
        self._running = True
        
        while self._running:
            try:
                msg = self.consumer.poll(1.0)
                if msg is None:
                    continue
                if msg.error():
                    logger.error(f"Ошибка при получении сообщения: {msg.error()}")
                    continue
                
                # Обрабатываем сообщение
                await message_handler(msg.value())
                
            except Exception as e:
                logger.error(f"Ошибка при обработке сообщения: {e}")
                await asyncio.sleep(1)
    
    def stop(self):
        """Остановка обработки сообщений"""
        self._running = False
        if self.consumer:
            self.consumer.close() 