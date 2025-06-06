from aiogram.types import InlineKeyboardMarkup, InlineKeyboardButton
from datetime import datetime, timedelta

def get_weather_options_keyboard() -> InlineKeyboardMarkup:
    """Клавиатура с опциями погоды"""
    keyboard = InlineKeyboardMarkup(
        inline_keyboard=[
            [
                InlineKeyboardButton(text="🌡 Сейчас", callback_data="weather_now"),
                InlineKeyboardButton(text="📅 На дату", callback_data="select_date")
            ],
            [
                InlineKeyboardButton(text="⏰ На время", callback_data="select_date_time"),
                InlineKeyboardButton(text="📆 На период", callback_data="select_date_range")
            ]
        ]
    )
    return keyboard

def get_hour_keyboard() -> InlineKeyboardMarkup:
    """Клавиатура выбора часа"""
    keyboard = []
    for hour in range(0, 24, 3):
        keyboard.append([
            InlineKeyboardButton(
                text=f"{hour:02d}:00",
                callback_data=f"hour_{hour:02d}"
            )
        ])
    keyboard.append([InlineKeyboardButton(text="❌ Отмена", callback_data="cancel")])
    return InlineKeyboardMarkup(inline_keyboard=keyboard)

def get_quick_dates_keyboard() -> InlineKeyboardMarkup:
    """Клавиатура быстрого выбора дат"""
    keyboard = []
    today = datetime.now()
    
    # Добавляем кнопки для следующих 7 дней
    for i in range(7):
        date = today + timedelta(days=i)
        keyboard.append([
            InlineKeyboardButton(
                text=date.strftime("%d.%m.%Y"),
                callback_data=f"date_{date.strftime('%Y-%m-%d')}"
            )
        ])
    
    keyboard.append([InlineKeyboardButton(text="❌ Отмена", callback_data="cancel")])
    return InlineKeyboardMarkup(inline_keyboard=keyboard)

def get_date_range_keyboard() -> InlineKeyboardMarkup:
    """Клавиатура выбора диапазона дат"""
    keyboard = []
    today = datetime.now()
    
    # Добавляем кнопки для следующих 7 дней
    for i in range(7):
        date = today + timedelta(days=i)
        keyboard.append([
            InlineKeyboardButton(
                text=date.strftime("%d.%m.%Y"),
                callback_data=f"range_date_{date.strftime('%Y-%m-%d')}"
            )
        ])
    
    keyboard.append([InlineKeyboardButton(text="❌ Отмена", callback_data="cancel")])
    return InlineKeyboardMarkup(inline_keyboard=keyboard) 