from aiogram import Router, F
from aiogram.types import Message, CallbackQuery
from aiogram.filters import Command
from aiogram.fsm.context import FSMContext
from datetime import datetime, time

from src.bot.states import SubscriptionStates
from src.keyboards.subscription import (
    get_subscription_menu_keyboard,
    get_timezone_keyboard,
    get_time_keyboard,
    get_edit_subscription_keyboard
)
from src.keyboards.main_menu import get_main_menu_keyboard
from src.services.weather_service import WeatherService
from src.models.weather import (
    SubscriptionRequestDTO,
    SubscriptionUpdateDTO
)

router = Router()

@router.message(F.text == "🔔 Подписаться на прогнозы")
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
        reply_markup=None
    )
    await state.set_state(SubscriptionStates.waiting_for_city)

@router.message(SubscriptionStates.waiting_for_city)
async def process_subscription_city(message: Message, state: FSMContext, weather_service: WeatherService):
    """Обработка города для подписки"""
    data = await state.get_data()
    is_edit = data.get("is_edit", False)
    
    try:
        if is_edit:
            request = SubscriptionUpdateDTO(
                city_name=message.text
            )
            
            response = await weather_service.update_subscription(
                message.from_user.id,
                request
            )
            await message.answer(
                f"✅ Подписка успешно обновлена!\n\n"
                f"Город: {message.text}\n\n"
                f"Следующее уведомление: {response.expected_next_notification_date_time_formatted.strftime('%Y-%m-%d %H:%M')}",
                reply_markup=get_subscription_menu_keyboard()
            )
            await state.clear()
        else:
            await state.update_data(city=message.text)
            await message.answer(
                "Выберите время уведомления или введите его вручную в формате ЧЧ:ММ (например, 09:30):",
                reply_markup=get_time_keyboard()
            )
            await state.set_state(SubscriptionStates.waiting_for_time)
    except Exception as e:
        await message.answer(
            "😔 Произошла ошибка при обновлении подписки. "
            "Пожалуйста, попробуйте позже или обратитесь в поддержку.",
            reply_markup=get_subscription_menu_keyboard()
        )
        await state.clear()
        raise

@router.message(SubscriptionStates.waiting_for_time)
async def process_subscription_time_input(message: Message, state: FSMContext, weather_service: WeatherService):
    """Обработка ручного ввода времени для подписки"""
    try:
        # Проверяем формат времени
        time_str = message.text.strip()
        if not time_str or ":" not in time_str:
            await message.answer(
                "❌ Неверный формат времени. Пожалуйста, используйте формат ЧЧ:ММ (например, 09:30):"
            )
            return
        
        hour, minute = map(int, time_str.split(":"))
        if not (0 <= hour <= 23 and 0 <= minute <= 59):
            await message.answer(
                "❌ Неверное время. Часы должны быть от 0 до 23, минуты от 0 до 59."
            )
            return
        
        data = await state.get_data()
        is_edit = data.get("is_edit", False)
        
        if is_edit:
            request = SubscriptionUpdateDTO(
                notification_time=time_str
            )
            
            response = await weather_service.update_subscription(
                message.from_user.id,
                request
            )
            
            await message.answer(
                f"✅ Подписка успешно обновлена!\n\n"
                f"Время уведомления: {time_str}\n\n"
                f"Следующее уведомление: {response.expected_next_notification_date_time_formatted.strftime('%Y-%m-%d %H:%M')}",
                reply_markup=get_subscription_menu_keyboard()
            )
            await state.clear()
        else:
            await state.update_data(notification_time=time_str)
            await message.answer(
                "Выберите часовой пояс или введите его вручную (например, Europe/Moscow):",
                reply_markup=get_timezone_keyboard()
            )
            await state.set_state(SubscriptionStates.waiting_for_timezone)
    except ValueError:
        await message.answer(
            "❌ Неверный формат времени. Пожалуйста, используйте формат ЧЧ:ММ (например, 09:30):"
        )
    except Exception as e:
        await message.answer(
            "😔 Произошла ошибка при обновлении подписки. "
            "Пожалуйста, попробуйте позже или обратитесь в поддержку.",
            reply_markup=get_subscription_menu_keyboard()
        )
        await state.clear()
        raise

@router.message(SubscriptionStates.waiting_for_timezone)
async def process_subscription_timezone_input(message: Message, state: FSMContext, weather_service: WeatherService):
    """Обработка ручного ввода часового пояса и создание/обновление подписки"""
    timezone = message.text.strip()
    data = await state.get_data()
    
    try:
        if data.get("is_edit", False):
            request = SubscriptionUpdateDTO(
                time_zone=timezone
            )
            
            response = await weather_service.update_subscription(
                message.from_user.id,
                request
            )
            await message.answer(
                f"✅ Подписка успешно обновлена!\n\n"
                f"Город: {response.city_name}\n"
                f"Время уведомления: {response.notification_time}\n"
                f"Часовой пояс: {timezone}\n\n"
                f"Следующее уведомление: {response.expected_next_notification_date_time_formatted.strftime('%Y-%m-%d %H:%M')}",
                reply_markup=get_subscription_menu_keyboard()
            )
        else:
            request = SubscriptionRequestDTO(
                city_name=data.get("city"),
                notification_time=data.get("notification_time"),
                time_zone=timezone
            )
            response = await weather_service.create_subscription(
                message.from_user.id,
                request
            )
            await message.answer(
                f"✅ Подписка успешно создана!\n\n"
                f"Город: {data.get('city')}\n"
                f"Время уведомления: {data.get('notification_time')}\n"
                f"Часовой пояс: {timezone}\n\n"
                f"Следующее уведомление: {response.expected_next_notification_date_time_formatted.strftime('%Y-%m-%d %H:%M')}",
                reply_markup=get_subscription_menu_keyboard()
            )
        
    except Exception as e:
        await message.answer(
            "😔 Произошла ошибка при обновлении подписки. "
            "Пожалуйста, попробуйте позже или обратитесь в поддержку.",
            reply_markup=get_subscription_menu_keyboard()
        )
        raise
    finally:
        await state.clear()

@router.callback_query(F.data == "edit_subscription")
async def edit_subscription_start(callback: CallbackQuery, state: FSMContext):
    """Начало редактирования подписки"""
    await callback.message.edit_text(
        "Выберите, что хотите изменить:",
        reply_markup=get_edit_subscription_keyboard()
    )

@router.callback_query(F.data == "edit_city")
async def edit_subscription_city(callback: CallbackQuery, state: FSMContext):
    """Редактирование города подписки"""
    await state.update_data(is_edit=True)
    await callback.message.edit_text(
        "Введите новое название города:",
        reply_markup=None
    )
    await state.set_state(SubscriptionStates.waiting_for_city)

@router.callback_query(F.data == "edit_time")
async def edit_subscription_time(callback: CallbackQuery, state: FSMContext):
    """Редактирование времени подписки"""
    await state.update_data(is_edit=True)
    await callback.message.edit_text(
        "Выберите новое время уведомления или введите его вручную в формате ЧЧ:ММ (например, 09:30):",
        reply_markup=get_time_keyboard()
    )
    await state.set_state(SubscriptionStates.waiting_for_time)

@router.callback_query(F.data == "edit_timezone")
async def edit_subscription_timezone(callback: CallbackQuery, state: FSMContext):
    """Редактирование часового пояса подписки"""
    await state.update_data(is_edit=True)
    await callback.message.edit_text(
        "Выберите новый часовой пояс или введите его вручную (например, Europe/Moscow):",
        reply_markup=get_timezone_keyboard()
    )
    await state.set_state(SubscriptionStates.waiting_for_timezone)

@router.callback_query(F.data == "skip_edit")
async def skip_edit(callback: CallbackQuery, state: FSMContext):
    """Пропуск редактирования"""
    await callback.message.edit_text(
        "✅ Редактирование отменено",
        reply_markup=get_subscription_menu_keyboard()
    )
    await state.clear()

@router.callback_query(F.data == "back_to_subscription")
async def back_to_subscription(callback: CallbackQuery, state: FSMContext):
    """Возврат в меню подписок"""
    await state.clear()
    await callback.message.edit_text(
        "Выберите действие:",
        reply_markup=get_subscription_menu_keyboard()
    )

@router.callback_query(F.data == "delete_subscription")
async def delete_subscription(
    callback: CallbackQuery,
    weather_service: WeatherService
):
    """Удаление подписки"""
    try:
        await weather_service.delete_subscription(callback.from_user.id)
        await callback.message.edit_text(
            "✅ Подписка успешно удалена!",
            reply_markup=get_subscription_menu_keyboard()
        )
    except Exception as e:
        await callback.message.edit_text(
            "😔 Произошла ошибка при удалении подписки. "
            "Пожалуйста, попробуйте позже или обратитесь в поддержку.",
            reply_markup=get_subscription_menu_keyboard()
        )
        raise

@router.callback_query(F.data == "back_to_main")
async def back_to_main(callback: CallbackQuery, state: FSMContext):
    """Возврат в главное меню"""
    await state.clear()
    await callback.message.edit_text(
        "Выберите действие в меню:",
        reply_markup=get_main_menu_keyboard()
    )

@router.callback_query(F.data.startswith("time_"))
async def process_subscription_time(callback: CallbackQuery, state: FSMContext):
    """Обработка времени для подписки"""
    time_str = callback.data.split("_")[1]
    await state.update_data(notification_time=time_str)
    await callback.message.edit_text(
        "Выберите часовой пояс или введите его вручную (например, Europe/Moscow):",
        reply_markup=get_timezone_keyboard()
    )
    await state.set_state(SubscriptionStates.waiting_for_timezone)

@router.callback_query(F.data == "subscription_info")
async def show_subscription_info(callback: CallbackQuery, weather_service: WeatherService):
    """Отображение информации о текущей подписке"""
    try:
        info = await weather_service.get_subscription_info(callback.from_user.id)
        
        if info:
            await callback.message.edit_text(
                f"ℹ️ Информация о вашей подписке:\n\n"
                f"🏙 Город: {info['cityName']}\n"
                f"🕒 Время уведомления: {info['notificationTime']}\n"
                f"🌍 Часовой пояс: {info['timeZone']}",
                reply_markup=get_subscription_menu_keyboard()
            )
        else:
            await callback.message.edit_text(
                "❌ У вас пока нет активной подписки.\n"
                "Создайте подписку, чтобы получать ежедневные прогнозы погоды.",
                reply_markup=get_subscription_menu_keyboard()
            )
    except Exception as e:
        await callback.message.edit_text(
            "😔 Произошла ошибка при получении информации о подписке. "
            "Пожалуйста, попробуйте позже или обратитесь в поддержку.",
            reply_markup=get_subscription_menu_keyboard()
        )
        raise

@router.callback_query(F.data.startswith("timezone_"))
async def process_subscription_timezone(callback: CallbackQuery, state: FSMContext, weather_service: WeatherService):
    """Обработка выбора часового пояса"""
    timezone = callback.data.split("_")[1]
    data = await state.get_data()
    
    try:
        if data.get("is_edit", False):
            request = SubscriptionUpdateDTO(
                time_zone=timezone
            )
            
            response = await weather_service.update_subscription(
                callback.from_user.id,
                request
            )
            await callback.message.edit_text(
                f"✅ Подписка успешно обновлена!\n\n"
                f"Часовой пояс: {timezone}\n\n"
                f"Следующее уведомление: {response.expected_next_notification_date_time_formatted.strftime('%Y-%m-%d %H:%M')}",
                reply_markup=get_subscription_menu_keyboard()
            )
        else:
            request = SubscriptionRequestDTO(
                city_name=data.get("city"),
                notification_time=data.get("notification_time"),
                time_zone=timezone
            )
            response = await weather_service.create_subscription(
                callback.from_user.id,
                request
            )
            await callback.message.edit_text(
                f"✅ Подписка успешно создана!\n\n"
                f"Город: {data.get('city')}\n"
                f"Время уведомления: {data.get('notification_time')}\n"
                f"Часовой пояс: {timezone}\n\n"
                f"Следующее уведомление: {response.expected_next_notification_date_time_formatted.strftime('%Y-%m-%d %H:%M')}",
                reply_markup=get_subscription_menu_keyboard()
            )
        
    except Exception as e:
        await callback.message.edit_text(
            "😔 Произошла ошибка при обновлении подписки. "
            "Пожалуйста, попробуйте позже или обратитесь в поддержку.",
            reply_markup=get_subscription_menu_keyboard()
        )
        raise
    finally:
        await state.clear() 