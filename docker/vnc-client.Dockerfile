# Use Alpine as base image (small and lightweight)
FROM alpine:latest

# Install the necessary packages: a VNC client (e.g., TigerVNC)
RUN apk update && apk add --no-cache \
    tigervnc-viewer \
    bash \
    && rm -rf /var/cache/apk/*

# Default command to start the VNC client (you can specify the VNC server address here)
CMD ["vncviewer"]