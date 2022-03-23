FROM nginx:1.21.6-alpine

# Copying nginx config to different folder
COPY nginx.conf /etc/nginx/nginx.conf 

# Copying compiled code to different folder
COPY dist/sample-frontend /usr/share/nginx/html

EXPOSE 8080
