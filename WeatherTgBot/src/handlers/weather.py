from aiogram import Router, F
from aiogram.types import Message, CallbackQuery
from aiogram.filters import Command
from aiogram.fsm.context import FSMContext
import logging
from typing import List
from datetime import datetime

from src.bot.states import WeatherStates
from src.keyboards.weather import (
    get_weather_options_keyboard,
    get_hour_keyboard,
    get_quick_dates_keyboard,
    get_date_range_keyboard
)
from src.keyboards.main_menu import get_main_menu_keyboard
from src.services.weather_service import WeatherService
from src.models.weather import WeatherRequest, WeatherForecast

logger = logging.getLogger(__name__)
router = Router()

@router.message(F.text == "🌤 Узнать погоду")
async def weather_menu(message: Message):
    """Показывает меню выбора типа прогноза"""
    await message.answer(
        "Выберите тип прогноза:",
        reply_markup=get_weather_options_keyboard()
    )

@router.callback_query(F.data == "weather_now")
async def weather_now(callback: CallbackQuery, state: FSMContext, weather_service: WeatherService):
    """Обработчик запроса текущей погоды"""
    logger.info("Запрос текущей погоды")
    # Устанавливаем текущую дату
    current_date = datetime.now().strftime("%Y-%m-%d")
    await state.set_state(WeatherStates.waiting_for_city)
    await state.update_data(weather_type="now", selected_date=current_date)
    await callback.message.edit_text("Введите название города:")
    await callback.answer()

def format_weather_message(forecasts: List[WeatherForecast], start_idx: int, end_idx: int) -> str:
    """Форматирование части прогноза погоды"""
    message = []
    for forecast in forecasts[start_idx:end_idx]:
        message.append(
            f"🕒 {forecast.time[:5]}\n"
            f"🌡 Температура: {forecast.temp:.1f}°C\n"
            f"🌡 Ощущается как: {forecast.feels_like:.1f}°C\n"
            f"💨 Ветер: {forecast.wind_speed:.1f} м/с\n"
            f"💧 Влажность: {forecast.humidity:.1f}%\n"
            f"🌪 Давление: {forecast.pressure:.1f} Па\n"
            f"☀️ УФ-индекс: {forecast.uv_index:.1f}\n"
        )
    return "\n".join(message)

@router.message(WeatherStates.waiting_for_city)
async def process_city(message: Message, state: FSMContext, weather_service: WeatherService):
    """Обработка введенного города"""
    city = message.text.strip()
    logger.info(f"Получен запрос погоды для города: {city}")
    
    try:
        # Получаем данные из состояния
        state_data = await state.get_data()
        weather_type = state_data.get('weather_type')
        selected_date = state_data.get('selected_date')
        selected_hour = state_data.get('selected_hour')
        start_date = state_data.get('start_date')
        end_date = state_data.get('end_date')
        
        logger.info(f"Отправка запроса к API для города {city}")
        
        # Формируем запрос в зависимости от типа прогноза
        request = WeatherRequest(city_name=city)
        
        if weather_type == 'now':
            # Для текущей погоды используем текущую дату
            request.date = selected_date
            response = await weather_service.get_weather(request)
            
            # Отправляем прогноз частями по 6 часов
            total_forecasts = len(response.forecasts)
            for i in range(0, total_forecasts, 6):
                end_idx = min(i + 6, total_forecasts)
                part_message = format_weather_message(response.forecasts, i, end_idx)
                await message.answer(
                    f"🌤 Прогноз погоды в городе {city} на {selected_date}:\n\n{part_message}"
                )
            
            await message.answer(
                "Выберите действие:",
                reply_markup=get_main_menu_keyboard()
            )
        else:
            if selected_date:
                request.date = selected_date
            if selected_hour:
                request.hour = selected_hour
            if start_date:
                request.start_date = start_date
            if end_date:
                request.end_date = end_date
            
            response = await weather_service.get_weather(request)
            
            if weather_type in ['date', 'date_time']:
                if selected_hour:
                    # Если выбран конкретный час, показываем прогноз только для него
                    forecast = response.forecasts[0]
                    await message.answer(
                        f"🌤 Прогноз погоды в городе {city} на {selected_date} в {selected_hour}:00:\n\n"
                        f"🌡 Температура: {forecast.temp:.1f}°C\n"
                        f"🌡 Ощущается как: {forecast.feels_like:.1f}°C\n"
                        f"💨 Ветер: {forecast.wind_speed:.1f} м/с\n"
                        f"💧 Влажность: {forecast.humidity:.1f}%\n"
                        f"🌪 Давление: {forecast.pressure:.1f} Па\n"
                        f"☀️ УФ-индекс: {forecast.uv_index:.1f}",
                        reply_markup=get_main_menu_keyboard()
                    )
                else:
                    # Отправляем прогноз частями по 6 часов
                    total_forecasts = len(response.forecasts)
                    for i in range(0, total_forecasts, 6):
                        end_idx = min(i + 6, total_forecasts)
                        part_message = format_weather_message(response.forecasts, i, end_idx)
                        await message.answer(
                            f"🌤 Прогноз погоды в городе {city} на {selected_date}:\n\n{part_message}"
                        )
            elif weather_type == 'range':
                # Для диапазона дат разбиваем на части по 6 часов
                total_forecasts = len(response.forecasts)
                current_date = None
                current_part = []
                
                for i, forecast in enumerate(response.forecasts):
                    if forecast.date != current_date:
                        # Если это новый день, отправляем накопленные прогнозы
                        if current_part:
                            part_message = format_weather_message(current_part, 0, len(current_part))
                            await message.answer(
                                f"🌤 Прогноз погоды в городе {city} на {current_date}:\n\n{part_message}"
                            )
                        current_date = forecast.date
                        current_part = [forecast]
                    else:
                        current_part.append(forecast)
                        # Если накопили 6 прогнозов, отправляем
                        if len(current_part) == 6:
                            part_message = format_weather_message(current_part, 0, 6)
                            await message.answer(
                                f"🌤 Прогноз погоды в городе {city} на {current_date}:\n\n{part_message}"
                            )
                            current_part = []
                
                # Отправляем оставшиеся прогнозы
                if current_part:
                    part_message = format_weather_message(current_part, 0, len(current_part))
                    await message.answer(
                        f"🌤 Прогноз погоды в городе {city} на {current_date}:\n\n{part_message}"
                    )
            
            if not selected_hour:  # Показываем меню только если не выбран конкретный час
                await message.answer(
                    "Выберите действие:",
                    reply_markup=get_main_menu_keyboard()
                )
        
        await state.clear()
        
    except Exception as e:
        logger.error(f"Ошибка при получении погоды для города {city}: {str(e)}")
        await message.answer(
            "❌ Произошла ошибка при получении прогноза погоды. Пожалуйста, попробуйте еще раз.",
            reply_markup=get_main_menu_keyboard()
        )
        await state.clear()

@router.callback_query(F.data == "select_date")
async def select_date(callback: CallbackQuery, state: FSMContext):
    """Обработчик выбора даты"""
    logger.info("Запрос прогноза на конкретную дату")
    await state.set_state(WeatherStates.waiting_for_date)
    await state.update_data(weather_type="date")
    await callback.message.edit_text(
        "Выберите дату:",
        reply_markup=get_quick_dates_keyboard()
    )
    await callback.answer()

@router.callback_query(F.data == "select_date_time")
async def select_date_time(callback: CallbackQuery, state: FSMContext):
    """Обработчик выбора даты и времени"""
    logger.info("Запрос прогноза на конкретную дату и время")
    await state.set_state(WeatherStates.waiting_for_date)
    await state.update_data(weather_type="date_time")
    await callback.message.edit_text(
        "Выберите дату:",
        reply_markup=get_quick_dates_keyboard()
    )
    await callback.answer()

@router.callback_query(F.data == "select_date_range")
async def select_date_range(callback: CallbackQuery, state: FSMContext):
    """Обработчик выбора диапазона дат"""
    logger.info("Запрос прогноза на диапазон дат")
    await state.set_state(WeatherStates.waiting_for_date_range_start)
    await state.update_data(weather_type="range")
    await callback.message.edit_text(
        "Выберите начальную дату:",
        reply_markup=get_date_range_keyboard()
    )
    await callback.answer()

@router.callback_query(F.data.startswith("date_"))
async def process_date(callback: CallbackQuery, state: FSMContext):
    """Обработчик выбора конкретной даты"""
    date = callback.data.split("_")[1]
    logger.info(f"Выбрана дата: {date}")
    await state.update_data(selected_date=date)
    
    # Получаем тип запроса
    data = await state.get_data()
    weather_type = data.get("weather_type")
    
    # Если это выбор даты и времени, показываем выбор часа
    if weather_type == "date_time":
        await state.set_state(WeatherStates.waiting_for_hour)
        await callback.message.edit_text(
            "Выберите час:",
            reply_markup=get_hour_keyboard()
        )
    else:
        await state.set_state(WeatherStates.waiting_for_city)
        await callback.message.edit_text("Введите название города:")
    
    await callback.answer()

@router.callback_query(F.data.startswith("hour_"))
async def process_hour(callback: CallbackQuery, state: FSMContext):
    """Обработчик выбора часа"""
    hour = callback.data.split("_")[1]
    logger.info(f"Выбран час: {hour}")
    await state.update_data(selected_hour=hour)
    await state.set_state(WeatherStates.waiting_for_city)
    await callback.message.edit_text("Введите название города:")
    await callback.answer()

@router.callback_query(F.data == "cancel")
async def cancel_weather(callback: CallbackQuery, state: FSMContext):
    """Отмена выбора погоды"""
    logger.info("Отмена запроса погоды")
    await state.clear()
    await callback.message.edit_text(
        "Выберите действие:",
        reply_markup=get_weather_options_keyboard()
    )
    await callback.answer()

@router.callback_query(F.data.startswith("range_date_"))
async def process_range_date(callback: CallbackQuery, state: FSMContext):
    """Обработчик выбора даты для диапазона"""
    date = callback.data.split("_")[2]
    logger.info(f"Выбрана дата для диапазона: {date}")
    
    # Получаем текущее состояние
    current_state = await state.get_state()
    
    if current_state == WeatherStates.waiting_for_date_range_start:
        # Если это первая дата, сохраняем её и запрашиваем вторую
        await state.update_data(start_date=date)
        await state.set_state(WeatherStates.waiting_for_date_range_end)
        await callback.message.edit_text(
            "Выберите конечную дату:",
            reply_markup=get_date_range_keyboard()
        )
    else:
        # Если это вторая дата, сохраняем её и запрашиваем город
        await state.update_data(end_date=date)
        await state.set_state(WeatherStates.waiting_for_city)
        await callback.message.edit_text("Введите название города:")
    
    await callback.answer() 