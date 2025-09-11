# Keycloak Configuration and Extensions

This repository contains the configuration-as-code and custom extensions (Service Provider Interfaces - SPIs) for the Keycloak instance.

## Project Structure


## Prerequisites


## Local Development with Docker

This project uses Docker Compose to run a local Keycloak instance for development and testing of custom providers. The environment is configured for a rapid development feedback loop.

### One-Time Setup

These are the steps to get the environment running for the first time.

**Step 1: Build the Custom Providers**

This is the most important step. Before starting Docker, you **must** build your custom provider JAR files. If the JARs do not exist, Docker will create empty directories instead, which will cause Keycloak to fail.

```bash
# Navigate to the Maven parent module for the SPIs
cd keycloak-config/spis/

# Compile and package all provider modules
mvn clean install
```

**Step 2: Start the Keycloak Container**

```bash
# Navigate to the container configuration directory

cd keycloak-config/container/

# Start the services in detached mode
docker-compose up -d
```

**Step 3: Access Keycloak**

- Welcome Page: 'http://localhost:8080'
- Admin Console: 'http://localhost:8080/admin`
- Credentials: `admin`/`admin`

### Development
After the initial startup, follow this cycle to apply changes to your custom providers:

1.  **Make code changes** in your Java files.

2.  **Rebuild the JARs** to include your changes.
    ```bash
    # From the keycloak-config/spis/ directory
    mvn clean install
    ```

3.  **Restart the Container** to load the new JARs. This is faster than a full `down`/`up`.
    ```bash
    docker restart keycloak-dev
    ```
