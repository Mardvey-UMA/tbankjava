import asyncio
import logging
from src.bot.bot import WeatherBot
from src.config.config import Config


logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)

async def main():

    config = Config()
    

    bot = WeatherBot(config)
    try:
        await bot.start()
    except Exception as e:
        logging.error(f"Ошибка при запуске бота: {e}")
        raise
    finally:
        await bot.stop()

if __name__ == "__main__":
    try:
        asyncio.run(main())
    except (KeyboardInterrupt, SystemExit):
        logging.info("Бот остановлен") 