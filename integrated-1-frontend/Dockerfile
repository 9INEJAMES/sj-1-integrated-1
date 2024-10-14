FROM node:20.10.0 AS build
COPY . .
RUN rm -rf node_modules/
RUN npm install
RUN npm run build

FROM nginx:alpine AS run
COPY --from=build dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]

