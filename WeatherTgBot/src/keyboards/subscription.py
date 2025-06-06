from aiogram.types import ReplyKeyboardMarkup, KeyboardButton, InlineKeyboardMarkup, InlineKeyboardButton
from datetime import time

def get_subscription_menu_keyboard() -> InlineKeyboardMarkup:
    """Клавиатура меню подписок"""
    keyboard = [
        [
            InlineKeyboardButton(text="➕ Создать подписку", callback_data="create_subscription"),
            InlineKeyboardButton(text="✏️ Изменить подписку", callback_data="edit_subscription")
        ],
        [
            InlineKeyboardButton(text="❌ Удалить подписку", callback_data="delete_subscription"),
            InlineKeyboardButton(text="🔙 Назад", callback_data="back_to_main")
        ]
    ]
    return InlineKeyboardMarkup(inline_keyboard=keyboard)

def get_timezone_keyboard() -> InlineKeyboardMarkup:
    """Клавиатура выбора часового пояса"""
    keyboard = InlineKeyboardMarkup(
        inline_keyboard=[
            [
                InlineKeyboardButton(text="Москва (UTC+3)", callback_data="tz_Europe/Moscow"),
                InlineKeyboardButton(text="Калининград (UTC+2)", callback_data="tz_Europe/Kaliningrad")
            ],
            [
                InlineKeyboardButton(text="Екатеринбург (UTC+5)", callback_data="tz_Asia/Yekaterinburg"),
                InlineKeyboardButton(text="Новосибирск (UTC+7)", callback_data="tz_Asia/Novosibirsk")
            ],
            [
                InlineKeyboardButton(text="Владивосток (UTC+10)", callback_data="tz_Asia/Vladivostok"),
                InlineKeyboardButton(text="Камчатка (UTC+12)", callback_data="tz_Asia/Kamchatka")
            ],
            [InlineKeyboardButton(text="❌ Отмена", callback_data="cancel")]
        ]
    )
    return keyboard

def get_time_keyboard() -> InlineKeyboardMarkup:
    """Клавиатура выбора времени уведомления"""
    keyboard = InlineKeyboardMarkup(
        inline_keyboard=[
            [
                InlineKeyboardButton(text="07:00", callback_data="time_07:00"),
                InlineKeyboardButton(text="08:00", callback_data="time_08:00"),
                InlineKeyboardButton(text="09:00", callback_data="time_09:00")
            ],
            [
                InlineKeyboardButton(text="12:00", callback_data="time_12:00"),
                InlineKeyboardButton(text="13:00", callback_data="time_13:00"),
                InlineKeyboardButton(text="14:00", callback_data="time_14:00")
            ],
            [
                InlineKeyboardButton(text="18:00", callback_data="time_18:00"),
                InlineKeyboardButton(text="19:00", callback_data="time_19:00"),
                InlineKeyboardButton(text="20:00", callback_data="time_20:00")
            ],
            [InlineKeyboardButton(text="❌ Отмена", callback_data="cancel")]
        ]
    )
    return keyboard

def get_edit_subscription_keyboard() -> InlineKeyboardMarkup:
    """Клавиатура редактирования подписки"""
    keyboard = [
        [
            InlineKeyboardButton(text="🏙 Изменить город", callback_data="edit_city"),
            InlineKeyboardButton(text="🕒 Изменить время", callback_data="edit_time")
        ],
        [
            InlineKeyboardButton(text="🌍 Изменить часовой пояс", callback_data="edit_timezone"),
            InlineKeyboardButton(text="⏭ Не изменять", callback_data="skip_edit")
        ],
        [
            InlineKeyboardButton(text="🔙 Назад", callback_data="back_to_subscription")
        ]
    ]
    return InlineKeyboardMarkup(inline_keyboard=keyboard) 