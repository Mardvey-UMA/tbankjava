from aiogram.types import InlineKeyboardMarkup, InlineKeyboardButton
from typing import List

def get_city_keyboard(cities: List[str]) -> InlineKeyboardMarkup:
    """Создание клавиатуры с городами из кеша"""
    keyboard = []
    
    # Добавляем кнопки с городами по 2 в ряд
    for i in range(0, len(cities), 2):
        row = []
        for j in range(2):
            if i + j < len(cities):
                city = cities[i + j]
                row.append(
                    InlineKeyboardButton(
                        text=city,
                        callback_data=f"city:{city}"
                    )
                )
        if row:
            keyboard.append(row)
    
    # Добавляем кнопку для ввода другого города
    keyboard.append([
        InlineKeyboardButton(
            text="✏️ Ввести другой город",
            callback_data="city:custom"
        )
    ])
    
    # Добавляем кнопку отмены
    keyboard.append([
        InlineKeyboardButton(
            text="❌ Отмена",
            callback_data="city:cancel"
        )
    ])
    
    return InlineKeyboardMarkup(inline_keyboard=keyboard) 