# Используем базовый образ OpenJDK
FROM openjdk:21-jdk-slim

# Устанавливаем рабочую директорию в контейнере
WORKDIR /app

# Копируем Gradle-артефакты и создаем jar-файл
COPY build/libs/zerde-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port
EXPOSE 8080

# Указываем точку входа
ENTRYPOINT ["java", "-jar", "app.jar"]