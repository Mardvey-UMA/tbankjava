import logging
from typing import Optional
import aiohttp
from datetime import datetime, time

from src.config.config import Config
from src.models.weather import (
    WeatherRequest,
    WeatherResponse,
    SubscriptionRequestDTO,
    SubscriptionResponseDTO,
    WeatherForecast,
    SubscriptionUpdateDTO
)

logger = logging.getLogger(__name__)

class WeatherService:
    """Сервис для работы с API погоды"""
    
    def __init__(self, config: Config):
        self.config = config
        self.base_url = config.api.base_url.rstrip('/')  # Убираем trailing slash
        self.session: Optional[aiohttp.ClientSession] = None
    
    async def _get_session(self) -> aiohttp.ClientSession:
        """Получение или создание сессии"""
        if self.session is None or self.session.closed:
            logger.info("Создание новой сессии для API")
            self.session = aiohttp.ClientSession()
        return self.session
    
    async def get_current_weather(self, city: str) -> WeatherForecast:
        """Получение текущей погоды"""
        logger.info(f"Запрос текущей погоды для города {city}")
        request = WeatherRequest(city_name=city)
        response = await self.get_weather(request)
        logger.info(f"Получен ответ для текущей погоды: {response}")
        return response.forecasts[0]
    
    async def get_weather(self, request: WeatherRequest) -> WeatherResponse:
        """Получение прогноза погоды"""
        logger.info(f"Отправка запроса к API: {request}")
        
        session = await self._get_session()
        try:
            # Преобразуем запрос в словарь и убираем None значения
            request_data = {
                "cityName": request.city_name,
                "date": request.date,
                "hour": request.hour,
                "startDate": request.start_date,
                "endDate": request.end_date
            }
            # Убираем None значения
            request_data = {k: v for k, v in request_data.items() if v is not None}
            
            logger.info(f"Данные запроса: {request_data}")
            
            async with session.get(
                f"{self.base_url}/weather",
                json=request_data
            ) as response:
                logger.info(f"Получен ответ от API: {response.status}")
                if response.status == 200:
                    data = await response.json()
                    logger.info(f"Данные от API: {data}")
                    return WeatherResponse.parse_obj(data)
                else:
                    error_text = await response.text()
                    logger.error(f"Ошибка API: {error_text}")
                    raise Exception(f"Ошибка API: {error_text}")
        except Exception as e:
            logger.error(f"Ошибка при запросе к API: {str(e)}")
            raise
    
    async def create_subscription(self, telegram_id: int, request: SubscriptionRequestDTO) -> SubscriptionResponseDTO:
        """Создание подписки на прогноз погоды"""
        session = await self._get_session()
        
        try:
            request_data = request.dict(by_alias=True)
            
            logger.info(f"Отправка POST запроса на создание подписки:")
            logger.info(f"URL: {self.base_url}/subscriptions")
            logger.info(f"Headers: {{'X-Telegram-Id': {telegram_id}}}")
            logger.info(f"Request data: {request_data}")
            
            async with session.post(
                f"{self.base_url}/subscriptions",
                json=request_data,
                headers={"X-Telegram-Id": str(telegram_id)}
            ) as response:
                response_text = await response.text()
                logger.info(f"Response status: {response.status}")
                logger.info(f"Response headers: {response.headers}")
                logger.info(f"Response body: {response_text}")
                
                if response.status == 200:
                    data = await response.json()
                    return SubscriptionResponseDTO.parse_obj(data)
                else:
                    raise Exception(f"Ошибка API: {response.status} - {response_text}")
        except aiohttp.ClientError as e:
            logger.error(f"Ошибка при создании подписки: {e}")
            raise
    
    async def update_subscription(self, telegram_id: int, request: SubscriptionUpdateDTO) -> SubscriptionResponseDTO:
        """Обновление подписки на прогноз погоды"""
        session = await self._get_session()
        
        try:
            # Фильтруем None значения и используем by_alias=True для camelCase
            request_data = {k: v for k, v in request.dict(by_alias=True).items() if v is not None}
            
            logger.info(f"Отправка PUT запроса на обновление подписки:")
            logger.info(f"URL: {self.base_url}/subscriptions")
            logger.info(f"Headers: {{'X-Telegram-Id': {telegram_id}}}")
            logger.info(f"Request data: {request_data}")
            
            async with session.put(
                f"{self.base_url}/subscriptions",
                json=request_data,
                headers={"X-Telegram-Id": str(telegram_id)}
            ) as response:
                response_text = await response.text()
                logger.info(f"Response status: {response.status}")
                logger.info(f"Response headers: {response.headers}")
                logger.info(f"Response body: {response_text}")
                
                if response.status == 200:
                    data = await response.json()
                    return SubscriptionResponseDTO.parse_obj(data)
                else:
                    raise Exception(f"Ошибка API: {response.status} - {response_text}")
        except aiohttp.ClientError as e:
            logger.error(f"Ошибка при обновлении подписки: {e}")
            raise
    
    async def delete_subscription(self, telegram_id: int) -> None:
        """Удаление подписки на прогноз погоды"""
        session = await self._get_session()
        
        try:
            logger.info(f"Отправка DELETE запроса на удаление подписки:")
            logger.info(f"URL: {self.base_url}/subscriptions")
            logger.info(f"Headers: {{'X-Telegram-Id': {telegram_id}}}")
            
            async with session.delete(
                f"{self.base_url}/subscriptions",
                headers={"X-Telegram-Id": str(telegram_id)}
            ) as response:
                response_text = await response.text()
                logger.info(f"Response status: {response.status}")
                logger.info(f"Response headers: {response.headers}")
                logger.info(f"Response body: {response_text}")
                
                if response.status != 200:
                    raise Exception(f"Ошибка API: {response.status} - {response_text}")
        except aiohttp.ClientError as e:
            logger.error(f"Ошибка при удалении подписки: {e}")
            raise
    
    async def close(self):
        """Закрытие сессии"""
        if self.session and not self.session.closed:
            logger.info("Закрытие сессии API")
            await self.session.close()

    async def create_user(self, telegram_id: int) -> None:
        """Создание пользователя"""
        session = await self._get_session()
        
        try:
            async with session.post(
                f"{self.base_url}/users",
                headers={"X-Telegram-Id": str(telegram_id)}
            ) as response:
                response.raise_for_status()
                logger.info(f"Пользователь {telegram_id} успешно создан")
        except aiohttp.ClientError as e:
            logger.error(f"Ошибка при создании пользователя: {e}")
            raise

    async def get_subscription_info(self, telegram_id: int) -> dict:
        """Получение информации о текущей подписке"""
        session = await self._get_session()
        
        try:
            logger.info(f"Отправка GET запроса на получение информации о подписке:")
            logger.info(f"URL: {self.base_url}/users")
            logger.info(f"Headers: {{'X-Telegram-Id': {telegram_id}}}")
            
            async with session.get(
                f"{self.base_url}/users",
                headers={"X-Telegram-Id": str(telegram_id)}
            ) as response:
                response_text = await response.text()
                logger.info(f"Response status: {response.status}")
                logger.info(f"Response headers: {response.headers}")
                logger.info(f"Response body: {response_text}")
                
                if response.status == 200:
                    data = await response.json()
                    return data
                else:
                    raise Exception(f"Ошибка API: {response.status} - {response_text}")
        except aiohttp.ClientError as e:
            logger.error(f"Ошибка при получении информации о подписке: {e}")
            raise 