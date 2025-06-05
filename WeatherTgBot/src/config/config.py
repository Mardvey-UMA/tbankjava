from dataclasses import dataclass
from environs import Env
import os
from pathlib import Path
from dotenv import load_dotenv

@dataclass
class DbConfig:
    host: str
    password: str
    user: str
    database: str

@dataclass
class TgBot:
    token: str
    admin_ids: list[int]

@dataclass
class KafkaConfig:
    bootstrap_servers: str
    schema_registry_url: str
    response_topic: str
    group_id: str

@dataclass
class ApiConfig:
    base_url: str

class Config:
    """Конфигурация приложения"""
    
    def __init__(self):
        # Загружаем .env файл из директории проекта
        env_path = Path(__file__).parent.parent.parent / '.env'
        load_dotenv(env_path)
        
        # Настройки бота
        bot_token = os.getenv('BOT_TOKEN')
        if not bot_token:
            raise ValueError("BOT_TOKEN не найден в .env файле")
        
        # Настройки API
        api_url = os.getenv('API_BASE_URL')
        if not api_url:
            raise ValueError("API_BASE_URL не найден в .env файле")
        
        # Настройки Kafka
        kafka_bootstrap_servers = os.getenv('KAFKA_BOOTSTRAP_SERVERS')
        if not kafka_bootstrap_servers:
            raise ValueError("KAFKA_BOOTSTRAP_SERVERS не найден в .env файле")
        
        schema_registry_url = os.getenv('SCHEMA_REGISTRY_URL')
        if not schema_registry_url:
            raise ValueError("SCHEMA_REGISTRY_URL не найден в .env файле")
        
        response_topic = os.getenv('RESPONSE_TOPIC')
        if not response_topic:
            raise ValueError("RESPONSE_TOPIC не найден в .env файле")
        
        group_id = os.getenv('GROUP_ID')
        if not group_id:
            raise ValueError("GROUP_ID не найден в .env файле")
        
        # Инициализируем конфигурации
        self.tg_bot = TgBot(
            token=bot_token,
            admin_ids=[]  # Пока оставим пустым, можно добавить позже
        )
        
        self.kafka = KafkaConfig(
            bootstrap_servers=kafka_bootstrap_servers,
            schema_registry_url=schema_registry_url,
            response_topic=response_topic,
            group_id=group_id
        )
        
        self.api = ApiConfig(
            base_url=api_url
        )

def load_config(path: str = None) -> Config:
    env = Env()
    env.read_env(path)

    return Config(
        tg_bot=TgBot(
            token=env.str("BOT_TOKEN"),
            admin_ids=list(map(int, env.list("ADMINS")))
        ),
        db=DbConfig(
            host=env.str('DB_HOST'),
            password=env.str('DB_PASS'),
            user=env.str('DB_USER'),
            database=env.str('DB_NAME')
        ),
        kafka=KafkaConfig(
            bootstrap_servers=env.str('KAFKA_BOOTSTRAP_SERVERS'),
            schema_registry_url=env.str('SCHEMA_REGISTRY_URL'),
            response_topic=env.str('RESPONSE_TOPIC'),
            group_id=env.str('GROUP_ID')
        ),
        api=ApiConfig(
            base_url=env.str('API_BASE_URL')
        )
    ) 