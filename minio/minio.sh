#!/usr/bin/env bash
# Pull an image
docker pull minio/minio
# Run container with changed credentials
docker run -p 9000:9000 --name anyservice-minio \
  -e "MINIO_ACCESS_KEY=minioadmin" \
  -e "MINIO_SECRET_KEY=minioadmin" \
  minio/minio server /data

