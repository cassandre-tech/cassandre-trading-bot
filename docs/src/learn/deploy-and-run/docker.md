# Deploy & run with Docker

## Server installation
We are starting with a fresh [Ubuntu 20.04.2 LTS (Focal Fossa)](https://releases.ubuntu.com/20.04/) installation on a dedicated server, and we will use [Docker](https://www.docker.com/) to run our trading bot and the other components.

This is how it works:

* Two images are started manually on the server:
  * A [Postgresql image](https://hub.docker.com/_/postgres) to store strategies, orders, trades & positions.
  * A [Postgresql backup image](https://hub.docker.com/r/prodrigestivill/postgres-backup-local) to backup Postgresql databases.
* Our trading bot is built as a Docker image and deployed to the server by our continuous integration server.

::: tip
We chose PostgreSQL as our database, but you can choose the one you want, just add the corresponding JDBC driver to your `pom.xml`.
:::

### Install useful & required tools
```bash
sudo apt update
sudo apt -y install apt-transport-https ca-certificates curl gnupg2 pass software-properties-common
sudo apt -y upgrade
```

### Install Docker & docker-compose
```bash
sudo curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"
sudo apt update
sudo apt-cache policy docker-ce
sudo apt -y install docker-ce docker-compose
sudo chmod 666 /var/run/docker.sock
```

### Add a user for the trading bot
Our bot will be deployed to this server from another server (in our case, our continuous integration server), so we need to create a user that can connect with SSH:

```bash
sudo useradd -m -d /home/sma-trading-bot sma-trading-bot
sudo passwd sma-trading-bot
sudo gpasswd -a sma-trading-bot docker
sudo usermod --shell /bin/bash sma-trading-bot
```

## Docker images on the server
Download the [docker-compose.yml](/assets/src/docker-compose.yml) file on your server, edit your preferences (password, timezone, backup settings...) and run it with the command:

```bash
sudo docker-compose up -d
```

::: tip
You can download it directly with the command : `curl -o docker-compose.yml https://trading-bot.cassandre.tech/assets/src/docker-compose.yml`
:::

### Network
```yaml
networks:
  cassandre:
    name: cassandre
```

This part declares a network named `cassandre`.

### Volumes
```yaml
volumes:
  cassandre_database:
  cassandre_database_backup:
```

This part declares two volumes (space on disk) :

* `cassandre_database` for the database.
* `cassandre_database_backup` for the database backups.

### Postgresql
```yaml
  cassandre-postgresql:
    image: library/postgres:13-alpine
    restart: always
    networks:
      - cassandre
    volumes:
      - cassandre_database:/var/lib/postgresql/data
    environment:
      - TZ=Europe/Paris
      - PGTZ=Europe/Paris
      - POSTGRES_DB=cassandre_trading_bot
      - POSTGRES_USER=cassandre_trading_bot
      - POSTGRES_PASSWORD=mypassword
```

This starts a Postgresql image where our trading bot will store its data (strategies, orders, trades & positions).

### Postgresql backup
```yaml
  cassandre-postgresql-backup:
    image: prodrigestivill/postgres-backup-local:13-alpine
    depends_on:
      - cassandre-postgresql
    restart: always
    networks:
      - cassandre
    volumes:
      - cassandre_database_backup:/backups
    environment:
      - TZ=Europe/Paris
      - POSTGRES_HOST=postgresql
      - POSTGRES_DB=cassandre_trading_bot
      - POSTGRES_USER=cassandre_trading_bot
      - POSTGRES_PASSWORD=mypassword
      - POSTGRES_EXTRA_OPTS=--schema=public
      - SCHEDULE=@hourly
      - BACKUP_KEEP_DAYS=7
      - BACKUP_KEEP_WEEKS=4
      - BACKUP_KEEP_MONTHS=0
      - HEALTHCHECK_PORT=8080
```

This starts an image that will connect to the Postgresql image and make backups according to the parameters: `SCHEDULE`, `BACKUP_KEEP_DAYS`, `BACKUP_KEEP_WEEKS` and `BACKUP_KEEP_MONTHS`.

## Your bot
There are several ways to do what we are trying to do, we choose this one:

* Our trading bot source code is hosted in a private [Github](https://github.com/) project.
* On every push, our [Github actions](https://github.com/features/actions) script does the following steps:  
  * Creates the docker image of our trading bot.
  * Login to our [docker hub repository](https://hub.docker.com/).
  * Push the image to our docker hub repository.
  * Connect to our private server via ssh.
  * Stop the previous running image of our bot and download/run the new image.

The source of our script is [here](/assets/src/deployment.yml) and this is what it does:

### Build the docker image
```yaml
- name: Build with Maven and creates the docker image
  run: mvn spring-boot:build-image
```

### Push image to our private docker hub
```yaml
- name: Push image to docker hub
  run: |
    echo ${{ secrets.DOCKER_HUB_PASSWORD }} | docker login -u ${{ secrets.DOCKER_HUB_USERNAME }} --password-stdin
    docker push straumat/trading-bot:latest
```

### Deploy to the production server
The CI script does the following:

* Connect to our production server with SSH.
* Login to our docker private account.
* Stop & delete the image of the previous trading bot (if it exists).
* Retrieve the new image from the docker hub.
* Run the image with all the parameters specified in Github secrets.

```yaml
- name: Deploy to production server
  uses: appleboy/ssh-action@master
  with:
    host: ${{ secrets.SSH_HOST }}
    port: ${{ secrets.SSH_PORT }}
    username: ${{ secrets.SSH_USERNAME }}
    password: ${{ secrets.SSH_PASSWORD }}
    script: |
      echo ${{ secrets.DOCKER_HUB_PASSWORD }} | docker login -u ${{ secrets.DOCKER_HUB_USERNAME }} --password-stdin
      docker stop $(docker ps -aq --filter "label=trading-bot")
      docker rm -f $(docker ps -aq --filter "label=trading-bot")
      docker pull straumat/trading-bot:latest
      docker run  -d \
                  --security-opt apparmor=unconfined \
                  --network="cassandre" \
                  -e TZ=Europe/Paris \
                  -e CASSANDRE_TRADING_BOT_EXCHANGE_NAME='${{ secrets.CASSANDRE_TRADING_BOT_EXCHANGE_NAME }}' \
                  -e CASSANDRE_TRADING_BOT_EXCHANGE_USERNAME='${{ secrets.CASSANDRE_TRADING_BOT_EXCHANGE_USERNAME }}' \
                  -e CASSANDRE_TRADING_BOT_EXCHANGE_PASSPHRASE='${{ secrets.CASSANDRE_TRADING_BOT_EXCHANGE_PASSPHRASE }}' \
                  -e CASSANDRE_TRADING_BOT_EXCHANGE_KEY='${{ secrets.CASSANDRE_TRADING_BOT_EXCHANGE_KEY }}' \
                  -e CASSANDRE_TRADING_BOT_EXCHANGE_SECRET='${{ secrets.CASSANDRE_TRADING_BOT_EXCHANGE_SECRET }}' \
                  -e CASSANDRE_TRADING_BOT_EXCHANGE_MODES_SANDBOX='${{ secrets.CASSANDRE_TRADING_BOT_EXCHANGE_MODES_SANDBOX }}' \
                  -e CASSANDRE_TRADING_BOT_EXCHANGE_MODES_DRY='${{ secrets.CASSANDRE_TRADING_BOT_EXCHANGE_MODES_DRY }}' \
                  -e CASSANDRE_TRADING_BOT_EXCHANGE_RATES_ACCOUNT='${{ secrets.CASSANDRE_TRADING_BOT_EXCHANGE_RATES_ACCOUNT }}' \
                  -e CASSANDRE_TRADING_BOT_EXCHANGE_RATES_TICKER='${{ secrets.CASSANDRE_TRADING_BOT_EXCHANGE_RATES_TICKER }}' \
                  -e CASSANDRE_TRADING_BOT_EXCHANGE_RATES_ORDER='${{ secrets.CASSANDRE_TRADING_BOT_EXCHANGE_RATES_ORDER }}' \
                  -e CASSANDRE_TRADING_BOT_DATABASE_DATASOURCE_DRIVER-CLASS-NAME=${{ secrets.CASSANDRE_TRADING_BOT_DATABASE_DATASOURCE_DRIVER_CLASS_NAME }} \
                  -e CASSANDRE_TRADING_BOT_DATABASE_DATASOURCE_URL=${{ secrets.CASSANDRE_TRADING_BOT_DATABASE_DATASOURCE_URL }} \
                  -e CASSANDRE_TRADING_BOT_DATABASE_DATASOURCE_USERNAME=${{ secrets.CASSANDRE_TRADING_BOT_DATABASE_DATASOURCE_USERNAME }} \
                  -e CASSANDRE_TRADING_BOT_DATABASE_DATASOURCE_PASSWORD=${{ secrets.CASSANDRE_TRADING_BOT_DATABASE_DATASOURCE_PASSWORD }} \
                  -e CASSANDRE_TRADING_BOT_DATABASE_TABLE-PREFIX=${{ secrets.CASSANDRE_TRADING_BOT_DATABASE_TABLE_PREFIX }} \
                  -l trading-bot \
                  straumat/trading-bot:latest
```

These are the parameters for the Postgresql connection:

| Parameter | Value |
| :--- | :--- |
| DRIVER-CLASS-NAME | org.postgresql.Driver |
| URL | jdbc:postgresql://cassandre-postgresql/cassandre\_trading\_bot |
| USERNAME | cassandre\_trading\_bot |
| PASSWORD | mypassword |

::: tip
On the server, thanks to the docker label, you can view the bot logs with the command : `docker logs $(docker ps -aq --filter "label=trading-bot") --follow`
:::
