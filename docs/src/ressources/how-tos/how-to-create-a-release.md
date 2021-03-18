# Create a release

## Prepare the release

* Fix [Codacy](https://app.codacy.com/gh/cassandre-tech/cassandre-trading-bot/issues) & Intellij warnings.
* Update [security.md](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/SECURITY.md).
* Write the release notes from [issues in milestones](https://github.com/cassandre-tech/cassandre-trading-bot/milestones).
* Write a [substack post](https://cassandre.substack.com/publish?utm_source=menu).
* Update & merge Gitbook documentation.

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

::: tip
The following documentation pages should also be updated : 

* [why-cassandre/features-and-roadmap](src/why-cassandre/features-and-roadmap.md)
* [learn/database-structure](src/learn/database-structure.md)
* [learn/quickstart\#create-your-project](src/learn/quickstart.md)
* [learn/dry-mode-and-backtesting](src/learn/dry-mode-and-backtesting.md)
* [learn/technical-analysis/create-the-project](src/learn/technical-analysis/create-the-project.md)
* [learn/technical-analysis/backtest-your-trading-strategy](src/learn/technical-analysis/backtest-your-trading-strategy.md)
:::

## Update Github

Close the corresponding [milestone in Github](https://github.com/cassandre-tech/cassandre-trading-bot/milestones).

