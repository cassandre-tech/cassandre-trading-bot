---
title: How to make a new release
summary: How to make a new release on github with gitflow-maven-plugin
sidebar: cassandre_sidebar
permalink: how_to_male_a_new_release.html
---

## Create the release with Maven.

First of all, check if your github configuration is correct by running `git remote -v`. You must be using ssh and not https.

To switch to ssh, type `git remote set-url origin git@github.com:cassandre-tech/cassandre-trading-bot.git`.

Start the release with `mvn gitflow:release-start`. You will have to choose the release number.

To finish the release and push branches and tags, just run : `mvn gitflow:release-finish`.

## Update documentation.

  * Edit release number in [Create a new project with Cassandre archetype](create_and_run_create_with_archetype.html).
  * Edit release number in [Add Cassandre to an existing project](create_and_run_add_cassandre_to_an_existing_project.html).
  * Edit release number in [Technical analysis / Create project](technical_analysis_create_project.html).