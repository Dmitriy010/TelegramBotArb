# TelegramBotArb
Регистрация webHook telegram: 
https://api.telegram.org/bot<TOKEN>/setWebhook?url=<URI>

# Docker
Скачать образ rabbitmq:
 - docker pull rabbitmq:3.11.0-management
Создать volume:
 - docker volume create rabbitmq_data
Запустить контейнер с rabbitmq:
 - docker run -d --hostname rabbitmq --name rabbitmq -p 5672:5672 -p 15672:15672 -v rabbitmq_data:/var/lib/rabbitmq --restart=unless-stopped rabbitmq:3.11.0-management
* Флаги:
1) --detach , -d   запустит контейнер в фоновом режиме и вернет идентификатор контейнера в терминал;
2) --hostname   адрес контейнера для подключения к нему внутри docker из других контейнеров;
3) --name   имя контейнера;
4) -p    порты: первый порт — тот, по которому мы будет подключаться снаружи docker, а второй — тот, который используется внутри контейнера;
5) -v   примонтировать volume (том), т. е. внешнее хранилище данных;
6) --restart=unless-stopped   контейнер будет подниматься заново при каждом перезапуске системы (точнее, при запуске docker);

Подключиться к контейнеру с rabbitmq:
 - docker exec -it rabbitmq /bin/bash
Внутри контейнера создать пользователя, сделать его админом и установить права:
 - rabbitmqctl add_user lds lds1488
 - rabbitmqctl set_user_tags lds administrator
 - rabbitmqctl set_permissions -p / lds ".*" ".*" ".*"

#Postgres

* Команда для разворачивания PostgreSQL в Docker:
- docker run -d --hostname pogreb --name pogreb -p 5432:5432 -e POSTGRES_USER=userok -e POSTGRES_PASSWORD=p@ssw0rd -e POSTGRES_DB=pogreb -v data:/var/lib/postgresql/data --restart=unless-stopped postgres:14.5
