from typing import List, Dict, Optional
import json
import os
from pathlib import Path

class CityCache:
    """Класс для работы с кешем городов"""
    
    def __init__(self, cache_dir: str = "cache"):
        self.cache_dir = Path(cache_dir)
        self.cache_dir.mkdir(exist_ok=True)
        self.cities_file = self.cache_dir / "cities.json"
        self.user_cities_file = self.cache_dir / "user_cities.json"
        self._load_cache()
    
    def _load_cache(self):
        """Загрузка кеша из файла"""
        if self.cities_file.exists():
            with open(self.cities_file, 'r', encoding='utf-8') as f:
                self.cities = json.load(f)
        else:
            self.cities = []
            self._save_cache()
            
        if self.user_cities_file.exists():
            with open(self.user_cities_file, 'r', encoding='utf-8') as f:
                self.user_cities = json.load(f)
        else:
            self.user_cities = {}
            self._save_user_cities()
    
    def _save_cache(self):
        """Сохранение кеша в файл"""
        with open(self.cities_file, 'w', encoding='utf-8') as f:
            json.dump(self.cities, f, ensure_ascii=False, indent=2)
    
    def _save_user_cities(self):
        """Сохранение кеша городов пользователей в файл"""
        with open(self.user_cities_file, 'w', encoding='utf-8') as f:
            json.dump(self.user_cities, f, ensure_ascii=False, indent=2)
    
    def add_city(self, city: str):
        """Добавление города в общий кеш"""
        if city not in self.cities:
            self.cities.append(city)
            self._save_cache()
    
    def add_user_city(self, user_id: int, city: str):
        """Добавление города в кеш пользователя"""
        if str(user_id) not in self.user_cities:
            self.user_cities[str(user_id)] = []
        
        if city not in self.user_cities[str(user_id)]:
            self.user_cities[str(user_id)].append(city)
            self._save_user_cities()
    
    def get_user_cities(self, user_id: int) -> List[str]:
        """Получение списка городов пользователя"""
        return self.user_cities.get(str(user_id), [])
    
    def get_all_cities(self) -> List[str]:
        """Получение всех городов из кеша"""
        return self.cities
    
    def search_cities(self, query: str) -> List[str]:
        """Поиск городов по запросу"""
        query = query.lower()
        return [city for city in self.cities if query in city.lower()] 