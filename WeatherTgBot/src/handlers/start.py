from aiogram import Router, F
from aiogram.types import Message
from aiogram.filters import Command
from aiogram.fsm.context import FSMContext

from src.keyboards.main_menu import get_main_menu_keyboard
from src.services.weather_service import WeatherService

router = Router()

@router.message(Command("start"))
async def cmd_start(message: Message):
    """Обработчик команды /start"""
    await message.answer(
        "👋 Привет! Я бот для получения прогноза погоды.\n\n"
        "🌤 Я могу показать:\n"
        "• Текущую погоду\n"
        "• Прогноз на конкретную дату\n"
        "• Прогноз на конкретное время\n"
        "• Прогноз на диапазон дат\n\n"
        "🔔 Также вы можете подписаться на ежедневные прогнозы погоды.\n\n"
        "Выберите действие:",
        reply_markup=get_main_menu_keyboard()
    )

@router.message(Command("start"))
async def cmd_start_old(message: Message, weather_service: WeatherService):
    """Обработчик команды /start"""
    try:
        # Регистрируем пользователя
        await weather_service.register_user(message.from_user.id)
        
        # Отправляем приветственное сообщение
        await message.answer(
            f"👋 Привет, {message.from_user.full_name}!\n\n"
            "Я бот для получения прогноза погоды. "
            "Вы можете узнать текущую погоду или подписаться на ежедневные прогнозы.\n\n"
            "Выберите действие в меню ниже:",
            reply_markup=get_main_menu_keyboard()
        )
    except Exception as e:
        await message.answer(
            "😔 Произошла ошибка при регистрации. "
            "Пожалуйста, попробуйте позже или обратитесь в поддержку."
        )
        raise 