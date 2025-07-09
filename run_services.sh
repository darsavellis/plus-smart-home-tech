#!/bin/bash

# Скрипт для запуска сервисов telemetry в фоновом режиме с мониторингом

# Функция завершения работы - останавливает все сервисы и делает docker compose down
cleanup() {
  echo "Завершение работы..."
  kill $(jobs -p) 2>/dev/null
  cd "$PROJECT_DIR" && docker compose down
  exit 0
}

# Регистрация функции cleanup для сигнала завершения
trap cleanup EXIT

# Директория проекта
PROJECT_DIR="/Users/sandro/IdeaProjects/plus-smart-home-tech"

# Запуск docker compose в фоне
echo "Запуск docker compose..."
cd "$PROJECT_DIR" && docker compose up -d

# Собрать проект (если необходимо)
echo "Сборка проекта..."
cd "$PROJECT_DIR" && mvn clean package -DskipTests

# Запуск collector в фоне
echo "Запуск collector..."
cd "$PROJECT_DIR/telemetry/collector" && \
mvn spring-boot:run > /tmp/collector.log 2>&1 &
COLLECTOR_PID=$!

# Запуск aggregator в фоне
echo "Запуск aggregator..."
cd "$PROJECT_DIR/telemetry/aggregator" && \
mvn spring-boot:run > /tmp/aggregator.log 2>&1 &
AGGREGATOR_PID=$!

# Запуск analyzer в фоне
echo "Запуск analyzer..."
cd "$PROJECT_DIR/telemetry/analyzer" && \
mvn spring-boot:run > /tmp/analyzer.log 2>&1 &
ANALYZER_PID=$!

echo "Все сервисы запущены в фоновом режиме"
echo ""

# Функция для проверки, запущен ли процесс
is_running() {
  kill -0 $1 2>/dev/null
  return $?
}

# Функция для проверки статуса Docker-контейнеров
check_docker_status() {
  echo "=== СТАТУС DOCKER КОНТЕЙНЕРОВ ==="
  cd "$PROJECT_DIR" && docker compose ps --format "table {{.Name}}\t{{.Status}}" | grep -v "NAME" || echo "Контейнеры не запущены"
  echo ""
}

# Настройка неблокирующего чтения клавиш
if [ -t 0 ]; then # проверка, что ввод - терминал
  stty -echo -icanon time 0 min 0
fi

# Функция для обработки клавиатурного ввода
check_input() {
  if read -t 0 key; then
    if [[ "$key" == "q" || "$key" == "Q" ]]; then
      echo "Нажата клавиша 'q'. Завершение работы..."
      cleanup
    fi
  fi
}

# Мониторинг состояния сервисов
while true; do
  clear
  echo "=== МОНИТОРИНГ СЕРВИСОВ ==="
  echo ""

  # Статус Docker-контейнеров
  check_docker_status

  echo "=== СТАТУС JAVA-СЕРВИСОВ ==="

  if is_running $COLLECTOR_PID; then
    echo "✅ Collector (PID $COLLECTOR_PID): запущен"
  else
    echo "❌ Collector: не запущен"
  fi

  if is_running $AGGREGATOR_PID; then
    echo "✅ Aggregator (PID $AGGREGATOR_PID): запущен"
  else
    echo "❌ Aggregator: не запущен"
  fi

  if is_running $ANALYZER_PID; then
    echo "✅ Analyzer (PID $ANALYZER_PID): запущен"
  else
    echo "❌ Analyzer: не запущен"
  fi

  echo ""
  echo "Нажмите 'q' для завершения всех сервисов и Docker-контейнеров"

  # Проверка ввода пользователя
  check_input

  # Ждем перед следующим обновлением
  sleep 3
done

