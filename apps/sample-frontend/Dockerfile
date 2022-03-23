# nginx image
FROM registry.access.redhat.com/ubi8/nginx-120:1-21

# Copying compiled code and nginx config to different folder
COPY dist/sample-frontend /usr/share/nginx/html

#COPY /nginx.conf  /etc/nginx/conf.d/default.conf

# Exposing a port, here it means that inside the container 
# the app will be using Port 80 while running
EXPOSE 80
