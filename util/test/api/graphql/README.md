# Utils to test graphql during continuous integration

## Database
To test Cassandre GraphQL API, we will use existing data and run a postgres server. This way, we can check that the data in database are accessible throw GraphQL API calls.

To retrieve a database on our production server, we use this command:
̀```
docker exec -t YOUR_PASSWORD pg_dump -U cassandre_trading_bot cassandre_trading_bot > dump_cassandre_trading_bot.sql
̀```
_note: if you just want the data in your dump, add the following options: --column-inserts --data-only._

Then, with this command, we will start a postgres instance:
̀```
docker run -d \
        --name postgres \
        -p 5432:5432 \
        -e POSTGRES_DB=cassandre_trading_bot \
        -e POSTGRES_USER=cassandre_trading_bot \
        -e POSTGRES_PASSWORD=cassandre_trading_bot_password \
        library/postgres:13-alpine
̀```

To finish, we will restore our database dump with this command:
̀```
docker exec -i postgres psql -U cassandre_trading_bot cassandre_trading_bot < util/test/api/graphql/dump_cassandre_trading_bot.sql
̀```
_note: you can access psql with the command:docker exec -it postgres psql -U cassandre_trading_bot_

## Application
We will create a Cassandre trading bot thanks to the basic archetype:
̀```
mvn -B archetype:generate \
        -DarchetypeGroupId=tech.cassandre.trading.bot \
        -DarchetypeArtifactId=cassandre-trading-bot-spring-boot-starter-basic-archetype \
        -DarchetypeVersion=5.0.3-spring-boot-starter-api-graphQL-SNAPSHOT \
        -DgroupId=com.example \
        -DartifactId=archetype-test-api-graphql \
        -Dversion=1.0-SNAPSHOT \
        -Dpackage=com.example
̀```

Once the trading bot created, we will make those changes:
* Update `archetype-test-api-graphql/src/main/resources/applications.properties` (Change exchange, dry mode, rates and database) with this command: `cp util/test/api/graphql/application.properties archetype-test-api-graphql/src/main/resources/`.
* Add a fake trade account `archetype-test-api-graphql/src/main/resources/user-trade.tsv` with this command: `cp util/test/api/graphql/user-trade.tsv archetype-test-api-graphql/src/main/resources/`.

Then, we run the application with the following command:
̀```
mvn -f archetype-test-api-graphql/pom.xml spring-boot:run
̀```

## Tests
Tests are written and javascript and ran to see if the graphQL API replies correctly.

We retrieve the tests from util directory:
̀```
cp util/test/api/graphql/package.json .
cp util/test/api/graphql/*.js .
̀```
We install the required libraries:
̀```
npm install --save-dev jest isomorphic-fetch
̀```
And we run the tests with the command:
̀```
npm run test
̀```