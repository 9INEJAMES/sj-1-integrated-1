FROM node:lts-alpine

# Use specific Node.js version as a base
FROM node:20.10.0 as build-stage

# Set working directory
WORKDIR /integrated-1-frontend

# Copy package.json and package-lock.json files
COPY package*.json ./

RUN rm -rf node_modules/

# Install dependencies
RUN npm install

# Copy the rest of the application code
COPY . .

# Build Vue.js application for production
RUN npm run build

# Use lightweight Node.js image for serving
FROM nginx:alpine as production-stage

# Copy built app from the previous stage
COPY --from=build-stage /integrated-1-frontend/dist /usr/share/nginx/html

# Expose port 80
EXPOSE 80

# Start Nginx
CMD ["nginx", "-g", "daemon off;"]

