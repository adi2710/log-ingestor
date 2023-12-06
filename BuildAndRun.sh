#!/bin/bash

echo "Step 1: Building Maven project with tests skipped..."
mvn clean install -DskipTests=true

if [ $? -ne 0 ]; then
  echo "Maven build failed. Exiting."
  exit 1
fi

echo "Step 2: Pulling or building MongoDB image..."
docker pull mongo:latest

if [ $? -ne 0 ]; then
  echo "MongoDB image pull failed. Trying to build locally..."
  docker build -t mongo:latest - <<EOF
FROM mongo:latest
EOF

  # Check if the MongoDB image build was successful
  if [ $? -ne 0 ]; then
    echo "MongoDB image build failed. Exiting."
    exit 1
  fi
fi

echo "Step 3: Building Docker image for logingestordocker..."
docker build -t logingestordocker:1.0 .

if [ $? -ne 0 ]; then
  echo "Docker image build failed. Exiting."
  exit 1
fi

# shellcheck disable=SC2164
cd src/main/resources/

echo "Step 4: Running Docker Compose..."
docker-compose up

if [ $? -ne 0 ]; then
  echo "Docker Compose failed. Exiting."
  exit 1
fi

echo "Build and run process completed successfully."