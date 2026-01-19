# Stage 1: Build der Anwendung
FROM node:lts-alpine as build-stage
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# Stage 2: Auslieferung über Nginx
FROM nginx:stable-alpine as production-stage
# Kopiere die gebauten Dateien in das Nginx-Verzeichnis
COPY --from=build-stage /app/dist /usr/share/nginx/html
# Port 80 für das Frontend öffnen
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]