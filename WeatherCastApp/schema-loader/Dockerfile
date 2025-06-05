FROM alpine:3.19
RUN apk add --no-cache bash curl jq
COPY load-schemas.sh /loader/load-schemas.sh
ENTRYPOINT ["/loader/load-schemas.sh"]
