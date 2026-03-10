# NPP Extension (Forge 1.7.10)

Мод добавляет GUI по клавише `ё`/`~` с 4 действиями:
1. Переключение времени (день/ночь)
2. Переключение режима игрока (выживание/творч)
3. Переключение сложности (мирный/сложный)
4. Старт/стоп глобального таймера подготовки на 30 минут

## Почему у тебя ошибки Gradle/Forge

Для `ForgeGradle 1.2` (Minecraft 1.7.10) нужна старая связка инструментов:
- **Java 8**
- **Gradle 2.14.1**

Если запускать новым Gradle (7/8+), появляются ошибки вроде:
- `Plugin with id 'maven' not found`
- `Could not create task ':reobf' ... TaskInputs.files(...)`

## Почему у тебя 27 ошибок `package ... does not exist`

Ты компилируешь `BattlePrepMod.java` отдельно, без classpath Forge/FML и без остальных исходников.
Для Forge-мода так нельзя: нужны
- все файлы из `src/main/java`
- jar-зависимости Forge/FML в `-cp`

Именно поэтому `cpw.mods.fml.*` и `ru.npp.extension.*` “не находятся”.

## Сборка без IDE

### Вариант A (рекомендуется): Gradle 2.14.1

1) Установи Java 8 (`java -version` должен показать 1.8)
2) Скачай локально совместимый Gradle:

```bash
./scripts/use-gradle-2.14.1.sh
```

3) Собери мод:

```bash
./scripts/build-no-ide.sh
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
- Для Gradle-сборки: Gradle 2.14.1
- Для ручной сборки: подготовленные зависимости в `libs/*.jar`
