FROM mysql:latest
ENV MYSQL_USER=dev
ENV MYSQL_PASSWORD=Mysql@sit123
ENV MYSQL_ROOT_PASSWORD=Mysql@sit123
ENV MYSQL_DATABASE=integrated
ENV MYSQL_CHARSET=utf8mb4
ENV MYSQL_COLLATION=utf8mb4_unicode_ci
EXPOSE 3306
COPY scripts/ /docker-entrypoint-initdb.d/
COPY dbscripts/ /dbscripts/

COPY my.cnf /etc/my.cnf
