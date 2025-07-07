FROM nginx:stable-alpine
RUN rm -rf /usr/share/nginx/html/*
COPY /deploy/staging/frontend/nginx.conf /etc/nginx/conf.d/default.conf
COPY /deploy/staging/frontend/output/ /usr/share/nginx/html/
COPY /deploy/staging/frontend/assetlinks.json /usr/share/nginx/html/.well-known/assetlinks.json
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]