FROM node:20.10.0 as build-stage
COPY package*.json ./
RUN rm -rf node_modules/
RUN npm install
COPY . .
RUN npm run build
FROM nginx:alpine as production-stage
COPY ./default.conf /etc/nginx/conf.d/default.conf
COPY --from=build-stage ./dist /usr/share/nginx/html

CMD ["nginx", "-g", "daemon off;"]

