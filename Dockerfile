FROM nginx:latest

# Remove default NGINX configuration
RUN rm /etc/nginx/conf.d/default.conf

# Copy our custom NGINX configuration
COPY nginx.conf /etc/nginx/conf.d/

# Create the uploads directory inside the container
RUN mkdir -p /uploads/slides

# Set the uploads directory permissions
RUN chmod -R 755 /uploads/slides

# Expose port 80
EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
