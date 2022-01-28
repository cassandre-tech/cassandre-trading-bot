---
lang: en-US
title: How to review code
description: How to review Cassandre code
---
# How to review code

## Core code (not tests & not archetypes)
* Check class comment and see if there is a right usage of {@link}.
* Instance variable should have the same name as its class like: `UserService userService`.
* No new line between method start ({) and method end (}).
* When a variable represents an id, say it in the name like 'carId'.
* When a variable is DTO, say it in the name like `carDTO`.
* Check logs texts.