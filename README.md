# NPP Extension (Forge 1.7.10)

## Сборка

Требования:
- Java 8
- Gradle 2.14.1

Команда:

```bash
gradle clean build
```

Готовый мод:

```text
build/libs/npp-extension-1.0.0.jar
```

Если снова всплывает старая ошибка `downloadClient` с `amazonaws.com`,
в `build.gradle` уже добавлен авто-bootstrapping нужных Minecraft 1.7.10 jar из Mojang manifest.
При необходимости можно запустить вручную:

```bash
gradle bootstrapLegacyMinecraftAssets
```
