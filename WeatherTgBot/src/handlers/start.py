from aiogram import Router, F
from aiogram.types import Message
from aiogram.filters import Command
import logging

from src.keyboards.main_menu import get_main_menu_keyboard
from src.services.weather_service import WeatherService

logger = logging.getLogger(__name__)
router = Router()

@router.message(Command("start"))
async def cmd_start(message: Message, weather_service: WeatherService):
    """Обработчик команды /start"""
    try:
        # Создаем пользователя
        await weather_service.create_user(message.from_user.id)
        
        await message.answer(
            f"👋 Привет, {message.from_user.full_name}!\n\n"
            "Я бот для получения прогноза погоды.\n\n"
            "🌤 Я могу показать:\n"
            "• Текущую погоду\n"
            "• Прогноз на конкретную дату\n"
            "• Прогноз на конкретное время\n"
            "• Прогноз на диапазон дат\n\n"
            "🔔 Также вы можете подписаться на ежедневные прогнозы погоды.\n\n"
            "Выберите действие:",
            reply_markup=get_main_menu_keyboard()
        )
    except Exception as e:
        logger.error(f"Ошибка при создании пользователя: {e}")
        await message.answer(
            "😔 Произошла ошибка при регистрации. "
            "Пожалуйста, попробуйте позже или обратитесь в поддержку."
        ) 