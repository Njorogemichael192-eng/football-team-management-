FROM gradle:8.11.1-jdk17

WORKDIR /app

COPY . .

RUN chmod +x gradlew

RUN cd backend && ../gradlew test --no-daemon --max-workers=1

EXPOSE 10000

CMD ["sh", "-c", "cd backend && ../gradlew run --no-daemon --max-workers=1"]