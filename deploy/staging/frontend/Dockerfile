FROM nginx:stable-alpine
RUN rm -rf /usr/share/nginx/html/*
COPY /deploy/nginx.conf /etc/nginx/conf.d/default.conf
COPY /deploy/staging/frontend/output/ /usr/share/nginx/html/
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]