from aiogram import Router, F
from aiogram.types import Message
from aiogram.filters import Command

from src.keyboards.main_menu import get_main_menu_keyboard

router = Router()

@router.message(Command("start"))
async def cmd_start(message: Message):
    """Обработчик команды /start"""
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