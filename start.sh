#!/bin/bash

# 1. Start the database container
echo "Starting PostgreSQL container..."
podman compose up -d

# Wait for DB to be healthy (optional but recommended)
sleep 5

# 2. Run the Quarkus backend in the background
echo "Starting Quarkus backend in development mode..."
./mvnw compile quarkus:dev &

# Get the background job PID for later shutdown
QUARKUS_PID=$!

# Wait for Quarkus to boot and the server to be ready
sleep 15 

# 3. Open the browser on Windows (using wslview is often easiest)
echo "Opening frontend in Windows browser..."
wslview http://localhost:8080

# Keep the script running to keep the terminal open until the user manually
# stops the process (which can be done via the UI button, or Ctrl+C)
wait $QUARKUS_PID