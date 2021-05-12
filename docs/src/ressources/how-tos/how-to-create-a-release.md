# Create a release

## Prepare the release
* Fix [Codacy](https://app.codacy.com/gh/cassandre-tech/cassandre-trading-bot/issues) & Intellij warnings.
* Update [security.md](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/SECURITY.md).
* Update libraries used by archetypes:
  * [trading-bot-archetypes/basic-archetype/src/main/resources/archetype-resources/pom.xml](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/trading-bot-archetypes/basic-archetype/src/main/resources/archetype-resources/pom.xml).
  * [trading-bot-archetypes/basic-ta4j-archetype/src/main/resources/archetype-resources/pom.xml](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/trading-bot-archetypes/basic-ta4j-archetype/src/main/resources/archetype-resources/pom.xml).

## Create the release with Maven
You must be using `ssh` and not `https`, to switch to `ssh`, type : 
```bash
git remote set-url origin git@github.com:cassandre-tech/cassandre-trading-bot.git
```

Start the release with :
```bash
mvn gitflow:release-start
```

After choosing the release number, finish the release, push branches and tags, with this command :
```bash
mvn gitflow:release-finish
```

## Update
* Close the corresponding [milestone in Github](https://github.com/cassandre-tech/cassandre-trading-bot/milestones?direction=asc&sort=due_date&state=open).
* Write and send a [substack post](https://cassandre.substack.com/publish?utm_source=menu).
