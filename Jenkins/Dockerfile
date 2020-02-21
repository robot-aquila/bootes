FROM ubuntu

RUN apt-get update \
    && apt-get install -y --no-install-recommends \
        xvfb x11vnc x11-xkb-utils xfonts-100dpi xfonts-75dpi xfonts-scalable xfonts-cyrillic x11-apps \
        openjdk-8-jdk \
    && apt-get install -y --no-install-recommends maven \
		net-tools iputils-ping \
    && rm -fr /var/lib/apt/lists/*

ENV DISPLAY :99
