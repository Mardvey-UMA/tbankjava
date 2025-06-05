from aiogram.fsm.state import State, StatesGroup

class WeatherStates(StatesGroup):
    """Состояния для получения прогноза погоды"""
    waiting_for_city = State()
    waiting_for_date = State()
    waiting_for_hour = State()
    waiting_for_date_range_start = State()
    waiting_for_date_range_end = State()

class SubscriptionStates(StatesGroup):
    """Состояния для управления подписками"""
    waiting_for_city = State()
    waiting_for_time = State()
    waiting_for_timezone = State() 