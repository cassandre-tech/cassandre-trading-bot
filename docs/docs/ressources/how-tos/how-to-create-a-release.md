---
lang: en-US
title: Create a release
description: How to create an official Cassandre release
---
# Create a release

## Prepare the release
* Fix [Codacy](https://app.codacy.com/gh/cassandre-tech/cassandre-trading-bot/issues) & Intellij warnings.
* Update [security.md](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/SECURITY.md).

## Create the release with Maven
You must be using `ssh` and not `https`. To switch to `ssh`, type : 
```bash
git remote set-url origin git@github.com:cassandre-tech/cassandre-trading-bot.git
```

Check that you are on the develop branch and that everything is committed:
```bash
git checkout development
git status
```

Start the release with:
```bash
mvn gitflow:release-start
```

After choosing the release number, finish the release, push branches and tags, with this command:
```bash
mvn gitflow:release-finish
```

## Update
* Close the corresponding [milestone in Github](https://github.com/cassandre-tech/cassandre-trading-bot/milestones?direction=asc&sort=due_date&state=open).
* Write and send a [substack post](https://cassandre.substack.com/publish?utm_source=menu).
* Update cassandre release number on production trading bots.
