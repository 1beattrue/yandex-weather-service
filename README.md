# yandex-weather-service

Консольный сервис: запрашивает прогноз у [Яндекс Погоды](https://yandex.ru/dev/weather/doc/ru/concepts/how-to) (`GET /v2/forecast`).

## Переменные окружения

| Переменная           | Обязательна | По умолчанию |
|----------------------|-------------|--------------|
| `YANDEX_WEATHER_KEY` | да          | —            |
| `WEATHER_QUERY_LAT`  | нет         | `55.75`      |
| `WEATHER_QUERY_LON`  | нет         | `37.62`      |
| `WEATHER_QUERY_DAYS` | нет         | `7`          |

## Локальный запуск

Требуется Java 17+. Ключ API передаётся через переменную окружения `YANDEX_WEATHER_KEY`:

```bash
export YANDEX_WEATHER_KEY=ваш_ключ
mvn spring-boot:run
```

В IntelliJ IDEA: `Run → Edit Configurations → Environment variables`.

## GitHub Actions

Workflow [`ci.yml`](.github/workflows/ci.yml): на push/PR — сборка и тесты (ключ не нужен).
Ручной запуск (`Run workflow`) дополнительно делает реальный запрос с ключом из секрета `YANDEX_WEATHER_KEY`:

```bash
gh secret set YANDEX_WEATHER_KEY
gh workflow run ci.yml -f lat=59.94 -f lon=30.31 -f days=5
```
