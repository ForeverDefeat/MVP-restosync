FROM node:24-alpine

WORKDIR /app

ARG VITE_API_BASE_URL=http://localhost:8080/api
ARG VITE_WS_URL=http://localhost:8080/ws
ENV VITE_API_BASE_URL=$VITE_API_BASE_URL
ENV VITE_WS_URL=$VITE_WS_URL

COPY package*.json ./
RUN npm ci

COPY . .
RUN npm run build

EXPOSE 4173

CMD ["npm", "run", "preview", "--", "--host", "0.0.0.0", "--port", "4173"]
