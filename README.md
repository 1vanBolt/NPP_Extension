# NPP Extension (Forge 1.7.10)

Мод добавляет GUI по клавише `ё`/`~` с 4 действиями:
1. Переключение времени (день/ночь)
2. Переключение режима игрока (выживание/творч)
3. Переключение сложности (мирный/сложный)
4. Старт/стоп глобального таймера подготовки на 30 минут

## Почему у тебя 27 ошибок `package ... does not exist`

Ты компилируешь `BattlePrepMod.java` отдельно, без classpath Forge/FML и без остальных исходников.
Для Forge-мода так нельзя: нужны
- все файлы из `src/main/java`
- jar-зависимости Forge/FML в `-cp`

Именно поэтому `cpw.mods.fml.*` и `ru.npp.extension.*` “не находятся”.

## Gradle обязателен?

Коротко:
- **Для упаковки `.class` -> `.jar`: не обязателен**.
- **Для компиляции мода из `src`: обычно проще через Gradle**, потому что он сам подтягивает Forge/FML зависимости.
- **Без Gradle тоже можно**, но тогда ты сам готовишь `libs/*.jar` и classpath.

## Сборка без IDE

### Вариант A (рекомендуется): через Gradle 6.9.4

```bash
./scripts/build-no-ide.sh
```

или вручную:

```bash
gradle clean build
```

Итоговый jar:

```text
build/libs/npp-extension-1.0.0.jar
```

### Вариант B: без Gradle (ручная компиляция + упаковка)

1) Положи Forge/FML jar-файлы в папку `libs/`.
2) Скомпилируй source:

```bash
./scripts/compile-from-src-no-gradle.sh
```

3) Упакуй `.class` в jar:

```bash
./scripts/package-from-classes.sh
```

Итоговый jar:

```text
dist/npp-extension-1.0.0-manual.jar
```

## CMD (Windows) минимальный пример вручную

```cmd
REM 1) компиляция всех source, а не одного BattlePrepMod.java
javac -encoding UTF-8 -source 1.8 -target 1.8 -cp "libs/*" -d build\classes\main src\main\java\ru\npp\extension\**\*.java

REM 2) упаковка
jar cf dist\npp-extension-1.0.0-manual.jar -C build\classes\main . -C src\main\resources .
```

> Важно: на Windows `**` может не раскрываться в `cmd` на старых конфигурациях. Тогда используй список файлов или PowerShell.

## Требования

- JDK 8 (не 11/17/21/25)
- Для Gradle-сборки: установленный `gradle`
- Для ручной сборки: подготовленные зависимости в `libs/*.jar`

## Типичные ошибки

- `Plugin with id 'maven' not found`
  - Запущен слишком новый Gradle (7+). Для Forge 1.7.10 используй Gradle 6.9.4.
