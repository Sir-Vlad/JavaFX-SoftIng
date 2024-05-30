#!/bin/bash

source ./Backend/.venv/bin/activate
# decommentare la prima volta che lancio lo script
# pip install -r ./Backend/packages.txt

# Define the backend command
backend_cmd="python3 ./Backend/manage.py runserver"

# Define the frontend command with the port number
frontend_cmd="mvn javafx:run -Dport=8000"

# Run the backend command in the background
$backend_cmd &

# Get the process ID of the backend command
backend_pid=$!

# Run the frontend command in the background
$frontend_cmd &

# Get the process ID of the frontend command
frontend_pid=$!

# Wait for both processes to finish
while ps -p $backend_pid > /dev/null && ps -p $frontend_pid > /dev/null; do
    sleep 1
done

# Kill both processes if they are still running
kill $backend_pid
kill $frontend_pid