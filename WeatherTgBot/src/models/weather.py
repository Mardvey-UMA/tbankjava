from datetime import datetime, time
from typing import List, Optional
from pydantic import BaseModel, Field

class WeatherModel(BaseModel):
    date: datetime
    time: time
    temp: float
    feels_like: float = Field(alias="feelsLike")
    wind_speed: float = Field(alias="windSpeed")
    wind_dir: float = Field(alias="windDir")
    humidity: float
    pressure: float
    uv_index: float = Field(alias="uvIndex")

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

class SubscriptionRequest(BaseModel):
    """Модель запроса на создание подписки"""
    user_id: int = Field(alias="userId")
    city_name: str = Field(alias="cityName")
    notification_time: time = Field(alias="notificationTime")
    timezone: str
    
    class Config:
        populate_by_name = True

class SubscriptionResponse(BaseModel):
    """Модель ответа с информацией о подписке"""
    subscription_id: str = Field(alias="subscriptionId")
    user_id: int = Field(alias="userId")
    city_name: str = Field(alias="cityName")
    notification_time: time = Field(alias="notificationTime")
    timezone: str
    
    class Config:
        populate_by_name = True 