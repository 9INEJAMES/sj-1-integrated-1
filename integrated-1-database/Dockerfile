FROM mysql:latest
ENV MYSQL_USER=dev
ENV MYSQL_PASSWORD=mysql@sit
ENV MYSQL_ROOT_PASSWORD=mysql@sit
ENV MYSQL_DATABASE=integrated
ENV MYSQL_CHARSET=utf8mb4
ENV MYSQL_COLLATION=utf8mb4_unicode_ci
EXPOSE 3306
COPY init.sql /docker-entrypoint-initdb.d/
COPY my.cnf /etc/my.cnf
