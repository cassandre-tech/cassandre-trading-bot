import{_ as s,M as r,p as i,q as t,R as a,t as e,N as c,a1 as o}from"./framework-7e1a102e.js";const l={},p=a("h1",{id:"creez-votre-project-cassandre",tabindex:"-1"},[a("a",{class:"header-anchor",href:"#creez-votre-project-cassandre","aria-hidden":"true"},"#"),e(" Créez votre project Cassandre")],-1),d={href:"https://search.maven.org/search?q=a:cassandre-trading-bot-spring-boot-starter-basic-archetype",target:"_blank",rel:"noopener noreferrer"},v=o(`<div class="language-bash line-numbers-mode" data-ext="sh"><pre class="language-bash"><code>mvn archetype:generate <span class="token parameter variable">-B</span> <span class="token punctuation">\\</span>
<span class="token parameter variable">-DarchetypeGroupId</span><span class="token operator">=</span>tech.cassandre.trading.bot <span class="token punctuation">\\</span>
<span class="token parameter variable">-DarchetypeArtifactId</span><span class="token operator">=</span>cassandre-trading-bot-spring-boot-starter-basic-archetype <span class="token punctuation">\\</span>
<span class="token parameter variable">-DarchetypeVersion</span><span class="token operator">=</span><span class="token number">6.0</span>.0 <span class="token punctuation">\\</span>
<span class="token parameter variable">-DgroupId</span><span class="token operator">=</span>com.mycompany.bot <span class="token punctuation">\\</span>
<span class="token parameter variable">-DartifactId</span><span class="token operator">=</span>my-trading-bot <span class="token punctuation">\\</span>
<span class="token parameter variable">-Dversion</span><span class="token operator">=</span><span class="token number">1.0</span>-SNAPSHOT <span class="token punctuation">\\</span>
<span class="token parameter variable">-Dpackage</span><span class="token operator">=</span>com.mycompany.bot
</code></pre><div class="line-numbers" aria-hidden="true"><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div></div></div><p>Voici la structure du projet qui va être créé pour vous :</p><div class="language-text line-numbers-mode" data-ext="text"><pre class="language-text"><code>my-trading-bot/
├── pom.xml
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── mycompany
    │   │           └── bot
    │   │               ├── Application.java
    │   │               ├── package-info.java
    │   │               └── SimpleStrategy.java
    │   └── resources
    │       └── application.properties
    └── test
        ├── java
        │   └── com
        │       └── mycompany
        │           └── bot
        │               └── SimpleStrategyTest.java
        └── resources
            ├── application.properties
            ├── candles-for-backtesting-BTC-USDT.csv
            ├── user-main.tsv
            ├── user-savings.csv
            └── user-trade.csv

</code></pre><div class="line-numbers" aria-hidden="true"><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div></div></div>`,3);function u(m,b){const n=r("ExternalLinkIcon");return i(),t("div",null,[p,a("p",null,[e("Si vous n'avez pas de projet spring boot existant, vous pouvez utiliser notre "),a("a",d,[e("archetype maven"),c(n)]),e(" pour en générer un :")]),v])}const h=s(l,[["render",u],["__file","create-your-project.html.vue"]]);export{h as default};
