# NPP Extension (Forge 1.7.10)

Мод добавляет GUI по клавише `ё`/`~` с 4 действиями:
1. Переключение времени (день/ночь)
2. Переключение режима игрока (выживание/творч)
3. Переключение сложности (мирный/сложный)
4. Старт/стоп глобального таймера подготовки на 30 минут

## Gradle обязателен?

Коротко:
- **Для упаковки `.class` -> `.jar`: не обязателен**.
- **Для нормальной компиляции Forge 1.7.10-мода из `src`: почти всегда обязателен** (или придется руками собирать очень длинный classpath из Forge/FML зависимостей).

То есть твой вариант “через cmd в 2 команды” — **можно**, если `.class` уже готовы.

## Как собрать без IDE

IDE не нужна вообще — достаточно терминала.

### 1) Что нужно установить
- **JDK 8** (именно 8, не 11/17/21/25)
- **Gradle** (лучше 6.x) — для шага компиляции

> Для ForgeGradle 1.2 (MC 1.7.10) новые версии Java обычно ломают сборку.

### 2) Проверить версии

```bash
java -version
javac -version
gradle -v
```

Если видишь не Java 8, переключи окружение:

```bash
export JAVA_HOME=/path/to/jdk8
export PATH="$JAVA_HOME/bin:$PATH"
java -version
```

### 3) Вариант А: полностью через Gradle (рекомендуется)

```bash
./scripts/build-no-ide.sh
```

или вручную:

```bash
gradle clean build
```

Итоговый файл:

```text
build/libs/npp-extension-1.0.0.jar
```

### 4) Вариант Б: как ты делал (из готовых `.class` в `.jar`)

Если классы уже есть (например после `gradle build`), можно упаковать **без gradle**:

```bash
./scripts/package-from-classes.sh
```

Итоговый файл:

```text
dist/npp-extension-1.0.0-manual.jar
```

#### Прямо “в 2 команды” (ручной аналог)

```bash
cp -r build/classes/main/. dist/tmp && cp -r src/main/resources/. dist/tmp
(cd dist/tmp && jar cf ../npp-extension-1.0.0-manual.jar .)
```

### 5) Windows PowerShell (полная сборка)

```powershell
$env:JAVA_HOME="C:\path\to\jdk8"
$env:Path="$env:JAVA_HOME\\bin;$env:Path"
java -version
gradle clean build
```

## Типичные ошибки

- `Unsupported class file major version ...`
  - Запущена не Java 8. Переключи `JAVA_HOME` на JDK 8.
- `gradle: command not found`
  - Установи Gradle и проверь, что он в `PATH`.
- `build/classes/main not found`
  - Нет готовых `.class`; сначала компиляция (`gradle build`).
