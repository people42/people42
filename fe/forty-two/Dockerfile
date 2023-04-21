FROM node:18.13.0 AS build
RUN mkdir -p /app
WORKDIR /app
COPY . .
RUN mkdir -p dist
RUN npm i
RUN npm run build

FROM nginx
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80 443
CMD ["nginx", "-g", "daemon off;"]