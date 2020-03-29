---
title: How to setup documentation
summary: How to use documentation-theme-jekyll to generate the static website hosted on github pages
sidebar: cassandre_sidebar
permalink: how_to_setup_documentation.html
---

## Installation.
  * In the github project, create a directory named `docs`. In github, click the Settings link of the project, scroll to GitHub Pages and choose `master branch /docs folder` for the `Source Option`.

  * Download or clone the theme from the [documentation-theme-jekyll Github repo](https://github.com/tomjoht/documentation-theme-jekyll). Most likely you won’t be pulling in updates once you start customizing the theme, so downloading the theme (instead of cloning it) probably makes the most sense. In Github, click the Clone or download button, and then click Download ZIP.

  * Copy the content of the ZIP files to the `/docs` directory.

  * Run the website by running `docker-compose up` in the `docs` directory.

  * Your site should now be running at [http://localhost:4000/](http://localhost:4000/).

## Configuration.
  * Edit `docs/_config.yml` and set your own parameters.
  
  * In the sidebar's parameters, add your sidebar name, for example : `cassandre_sidebar`. You can remove the others.
  
  * Create `cassandre_sidebar.yml` in `docs/_data/sidebars`.

```yaml
entries:
  - title: sidebar
    product:
    version:
    folders:

      - title: Overview
        output: web
        folderitems:

          - title: Introduction
            url: /cassandre_introduction.html
            output: web
            type: homepage

      - title: How-to guides
        output: web
        folderitems:

          - title: Documentation
            url: /cassandre_how_to_documentation.html
            output: web
            type: page
```

  * Create `cassandre_introduction.html` in `pages/cassandre`. 

```yaml
---
title: Introduction
tags: [documentation]
keywords: notes, tips, cautions, warnings, admonitions
summary: "My summary"
sidebar: cassandre_sidebar
permalink: cassandre_introduction.html
---

Hello !
``` 

  * Edit `docs/_data/topnav.yml` to choose which navigation you want to keep.
  
  * update `index.md` to setup your homepage.
