from datetime import datetime, time
from typing import List, Optional
from pydantic import BaseModel, Field

class WeatherModel(BaseModel):
    """Модель для десериализации данных о погоде из Kafka"""
    date: datetime
    time: time  # Возвращаем тип time, так как данные приходят в этом формате
    temp: float
    feels_like: float = Field(alias="feelsLike")
    wind_speed: float = Field(alias="windSpeed")
    wind_dir: float = Field(alias="windDir")
    humidity: float
    pressure: float
    uv_index: float = Field(alias="uvIndex")
    
    class Config:
        populate_by_name = True

class WeatherResponse(BaseModel):
    forecasts: List[WeatherModel]

class WeatherRequest(BaseModel):
    """Модель запроса прогноза погоды"""
    city_name: str = Field(alias="cityName")
    date: Optional[str] = None
    hour: Optional[str] = None
    start_date: Optional[str] = Field(None, alias="startDate")
    end_date: Optional[str] = Field(None, alias="endDate")
    
    class Config:
        populate_by_name = True

class WeatherForecast(BaseModel):
    """Модель прогноза погоды"""
    date: str
    time: str
    temp: float
    feels_like: float = Field(alias="feelsLike")
    wind_speed: float = Field(alias="windSpeed")
    humidity: float
    pressure: float
    uv_index: float = Field(alias="uvIndex")
    
    class Config:
        populate_by_name = True

class WeatherResponse(BaseModel):
    """Модель ответа с прогнозом погоды"""
    forecasts: List[WeatherForecast]

class SubscriptionRequestDTO(BaseModel):
    """Модель запроса на создание/обновление подписки"""
    city_name: str = Field(alias="cityName")
    notification_time: Optional[str] = Field(None, alias="notificationTime", pattern=r"^([01]?[0-9]|2[0-3]):[0-5][0-9]$")
    time_zone: Optional[str] = Field(None, alias="timeZone")
    
    class Config:
        populate_by_name = True

class SubscriptionResponseDTO(BaseModel):
    """Модель ответа с информацией о подписке"""
    expected_next_notification_date_time: Optional[datetime] = Field(None, alias="expectedNextNotificationDateTime")
    expected_next_notification_date_time_formatted: Optional[datetime] = Field(None, alias="expectedNextNotificationDateTimeFormatted")
    message: str
    
    class Config:
        populate_by_name = True

class KafkaWeatherResponseDTO(BaseModel):
    """Модель для десериализации Kafka сообщений"""
    forecasts: List[WeatherModel] 