---
title: How to make a new release
summary: How to make a new release on github with gitflow-maven-plugin
sidebar: cassandre_sidebar
permalink: how_to_male_a_new_release.html
---

First of all, check if your github configuration is correct by running `git remote -v`. You must be using ssh and not https.

To switch to ssh, type `git remote set-url origin git@github.com:cassandre-tech/cassandre-trading-bot.git`.

Start the release with `mvn gitflow:release-start`. You will have to choose the release number.

To finish the release and push branches and tags, just run : `mvn gitflow:release-finish`.
