import logging
from typing import Callable, Dict, Any, Awaitable
from aiogram import BaseMiddleware
from aiogram.types import Message, CallbackQuery, TelegramObject
from aiohttp import ClientError
from datetime import datetime

logger = logging.getLogger(__name__)

class ErrorHandlerMiddleware(BaseMiddleware):
    async def __call__(
        self,
        handler: Callable[[TelegramObject, Dict[str, Any]], Awaitable[Any]],
        event: TelegramObject,
        data: Dict[str, Any]
    ) -> Any:
        try:
            return await handler(event, data)
        except ClientError as e:
            logger.error(f"API Error: {e}")
            await self._handle_api_error(event)
        except ValueError as e:
            logger.error(f"Validation Error: {e}")
            await self._handle_validation_error(event, str(e))
        except Exception as e:
            logger.error(f"Unexpected error: {e}", exc_info=True)
            await self._handle_unexpected_error(event)

    async def _handle_api_error(self, event: TelegramObject):
        """Обработка ошибок API"""
        message = (
            "😔 Произошла ошибка при обращении к сервису погоды. "
            "Пожалуйста, попробуйте позже или обратитесь в поддержку."
        )
        await self._send_error_message(event, message)

    async def _handle_validation_error(self, event: TelegramObject, error: str):
        """Обработка ошибок валидации"""
        message = f"❌ Ошибка ввода: {error}\nПожалуйста, попробуйте снова."
        await self._send_error_message(event, message)

    async def _handle_unexpected_error(self, event: TelegramObject):
        """Обработка неожиданных ошибок"""
        message = (
            "😔 Произошла непредвиденная ошибка. "
            "Пожалуйста, попробуйте позже или обратитесь в поддержку."
        )
        await self._send_error_message(event, message)

    async def _send_error_message(self, event: TelegramObject, message: str):
        """Отправка сообщения об ошибке"""
        if isinstance(event, Message):
            await event.answer(message)
        elif isinstance(event, CallbackQuery):
            await event.message.edit_text(message) 