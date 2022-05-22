import{_ as r,r as i,o as d,c as t,b as s,a as c,F as l,e as a,d as e}from"./app.9376bde5.js";const o={},u=a(`<h1 id="construire-cassandre-a-partir-des-sources" tabindex="-1"><a class="header-anchor" href="#construire-cassandre-a-partir-des-sources" aria-hidden="true">#</a> Construire Cassandre \xE0 partir des sources</h1><h2 id="construire-cassandre" tabindex="-1"><a class="header-anchor" href="#construire-cassandre" aria-hidden="true">#</a> Construire Cassandre</h2><h3 id="recuperez-les-sources-depuis-github" tabindex="-1"><a class="header-anchor" href="#recuperez-les-sources-depuis-github" aria-hidden="true">#</a> R\xE9cup\xE9rez les sources depuis Github</h3><div class="language-bash ext-sh line-numbers-mode"><pre class="language-bash"><code><span class="token function">git</span> clone git@github.com:cassandre-tech/cassandre-trading-bot.git
</code></pre><div class="line-numbers" aria-hidden="true"><div class="line-number"></div></div></div><h3 id="allez-dans-le-repertoire-cassandre" tabindex="-1"><a class="header-anchor" href="#allez-dans-le-repertoire-cassandre" aria-hidden="true">#</a> Allez dans le r\xE9pertoire Cassandre</h3><div class="language-bash ext-sh line-numbers-mode"><pre class="language-bash"><code><span class="token builtin class-name">cd</span> cassandre-trading-bot
</code></pre><div class="line-numbers" aria-hidden="true"><div class="line-number"></div></div></div><h3 id="construire-sans-les-tests" tabindex="-1"><a class="header-anchor" href="#construire-sans-les-tests" aria-hidden="true">#</a> Construire sans les tests</h3><div class="language-bash ext-sh line-numbers-mode"><pre class="language-bash"><code>mvn <span class="token function">install</span> -Dgpg.skip -DskipTests
</code></pre><div class="line-numbers" aria-hidden="true"><div class="line-number"></div></div></div><h3 id="construire-avec-les-tests" tabindex="-1"><a class="header-anchor" href="#construire-avec-les-tests" aria-hidden="true">#</a> Construire avec les tests</h3><div class="language-bash ext-sh line-numbers-mode"><pre class="language-bash"><code>mvn <span class="token function">install</span> -Dgpg.skip
</code></pre><div class="line-numbers" aria-hidden="true"><div class="line-number"></div></div></div><h2 id="construire-la-documentation" tabindex="-1"><a class="header-anchor" href="#construire-la-documentation" aria-hidden="true">#</a> Construire la documentation</h2><h3 id="lancer-la-documentation-en-local" tabindex="-1"><a class="header-anchor" href="#lancer-la-documentation-en-local" aria-hidden="true">#</a> Lancer la documentation en local</h3><div class="language-bash ext-sh line-numbers-mode"><pre class="language-bash"><code>vuepress dev docs/src
</code></pre><div class="line-numbers" aria-hidden="true"><div class="line-number"></div></div></div>`,13),h=e("Le site web de documentation sera disponible \xE0 l'adresse "),b={href:"http://0.0.0.0:8080/",target:"_blank",rel:"noopener noreferrer"},p=e("http://0.0.0.0:8080/"),m=e("."),v=a(`<h3 id="construire-le-site-web-final" tabindex="-1"><a class="header-anchor" href="#construire-le-site-web-final" aria-hidden="true">#</a> Construire le site web final</h3><div class="language-bash ext-sh line-numbers-mode"><pre class="language-bash"><code><span class="token function">yarn</span> --cwd docs build
</code></pre><div class="line-numbers" aria-hidden="true"><div class="line-number"></div></div></div>`,2);function g(_,f){const n=i("ExternalLinkIcon");return d(),t(l,null,[u,s("p",null,[h,s("a",b,[p,c(n)]),m]),v],64)}var k=r(o,[["render",g],["__file","how-to-build-from-sources.html.vue"]]);export{k as default};
