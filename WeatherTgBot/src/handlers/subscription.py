from aiogram import Router, F
from aiogram.types import Message, CallbackQuery
from aiogram.filters import Command
from aiogram.fsm.context import FSMContext
from datetime import datetime, time

from src.bot.states import SubscriptionStates
from src.keyboards.subscription import (
    get_subscription_menu_keyboard,
    get_timezone_keyboard,
    get_time_keyboard
)
from src.keyboards.main_menu import get_cancel_keyboard
from src.services.weather_service import WeatherService
from src.models.weather import SubscriptionRequest

router = Router()

@router.message(F.text == "📅 Подписаться на прогноз")
async def subscription_menu(message: Message):
    """Показываем меню подписок"""
    await message.answer(
        "Выберите действие:",
        reply_markup=get_subscription_menu_keyboard()
    )

@router.callback_query(F.data == "create_subscription")
async def create_subscription_start(callback: CallbackQuery, state: FSMContext):
    """Начало создания подписки"""
    await callback.message.edit_text(
        "Введите название города:",
        reply_markup=get_cancel_keyboard()
    )
    await state.set_state(SubscriptionStates.waiting_for_city)

@router.message(SubscriptionStates.waiting_for_city)
async def process_subscription_city(message: Message, state: FSMContext):
    """Обработка города для подписки"""
    await state.update_data(city=message.text)
    await message.answer(
        "Выберите время уведомления:",
        reply_markup=get_time_keyboard()
    )
    await state.set_state(SubscriptionStates.waiting_for_time)

@router.callback_query(F.data.startswith("time_"))
async def process_subscription_time(callback: CallbackQuery, state: FSMContext):
    """Обработка времени для подписки"""
    time_str = callback.data.split("_")[1]
    hour, minute = map(int, time_str.split(":"))
    notification_time = time(hour=hour, minute=minute)
    
    await state.update_data(notification_time=notification_time)
    await callback.message.edit_text(
        "Выберите часовой пояс:",
        reply_markup=get_timezone_keyboard()
    )
    await state.set_state(SubscriptionStates.waiting_for_timezone)

@router.callback_query(F.data.startswith("tz_"))
async def process_subscription_timezone(
    callback: CallbackQuery,
    state: FSMContext,
    weather_service: WeatherService
):
    """Обработка часового пояса и создание подписки"""
    timezone = callback.data.split("_")[1]
    data = await state.get_data()
    
    try:
        request = SubscriptionRequest(
            city_name=data["city"],
            notification_time=data["notification_time"],
            time_zone=timezone
        )
        
        response = await weather_service.create_subscription(
            callback.from_user.id,
            request
        )
        
        await callback.message.edit_text(
            f"✅ Подписка успешно создана!\n\n"
            f"Город: {data['city']}\n"
            f"Время уведомления: {data['notification_time'].strftime('%H:%M')}\n"
            f"Часовой пояс: {timezone}\n\n"
            f"Следующее уведомление: {response.expected_next_notification_date_time_formatted.strftime('%Y-%m-%d %H:%M')}"
        )
        
    except Exception as e:
        await callback.message.edit_text(
            "😔 Произошла ошибка при создании подписки. "
            "Пожалуйста, попробуйте позже или обратитесь в поддержку."
        )
        raise
    finally:
        await state.clear()

@router.callback_query(F.data == "delete_subscription")
async def delete_subscription(
    callback: CallbackQuery,
    weather_service: WeatherService
):
    """Удаление подписки"""
    try:
        await weather_service.delete_subscription(callback.from_user.id)
        await callback.message.edit_text(
            "✅ Подписка успешно удалена!"
        )
    except Exception as e:
        await callback.message.edit_text(
            "😔 Произошла ошибка при удалении подписки. "
            "Пожалуйста, попробуйте позже или обратитесь в поддержку."
        )
        raise

@router.callback_query(F.data == "back_to_main")
async def back_to_main(callback: CallbackQuery, state: FSMContext):
    """Возврат в главное меню"""
    await state.clear()
    await callback.message.edit_text(
        "Выберите действие в меню:"
    ) 