#!/bin/bash

# Load environment variables from .env
export $(grep -v '^#' .env | xargs)

# Run your Spring Boot app using Maven Wrapper
./mvnw spring-boot:run
