FROM moustack/ubuntu-base:liberty

# install and configure packages
COPY build-image /tmp/
RUN /tmp/build-image

# add runit libraries
COPY lib /usr/lib/

# add etc overlay
COPY etc /etc/
