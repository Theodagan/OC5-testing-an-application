FROM cypress/browsers:latest

USER root

# Install desktop environment and VNC
RUN apt-get update && apt-get install -y \
    xfce4 xfce4-goodies x11vnc xvfb \
    libgtk2.0-0 libgtk-3-0 libgbm-dev libnotify-dev libgconf-2-4 libnss3 libxss1 libasound2 \
 && apt-get clean \
 && rm -rf /var/lib/apt/lists/*

# Set password for VNC access
RUN mkdir -p ~/.vnc \
 && x11vnc -storepasswd 1234 ~/.vnc/passwd

# Set environment variables for display
ENV DISPLAY=:99
  
# Set up Cypress workdir and install
WORKDIR /e2e
COPY front/package*.json ./
RUN npm install --production && npm cache clean --force

COPY front/cypress.config.ts .
COPY front/cypress/ ./cypress/
COPY front/cypress/tsconfig.json ./cypress/

# Start VNC, XVFB and Cypress GUI
CMD /bin/bash -c "Xvfb :99 -screen 0 1920x1080x16 & xfce4-session & x11vnc -forever -usepw -display :99 -rfbport 5900 & npx cypress open"