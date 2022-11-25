import{_ as s,r,o as i,c as t,b as a,d as e,a as c,e as l}from"./app.4d6819e9.js";const o={},d=a("h1",{id:"create-your-cassandre-project",tabindex:"-1"},[a("a",{class:"header-anchor",href:"#create-your-cassandre-project","aria-hidden":"true"},"#"),e(" Create your Cassandre project")],-1),p={href:"https://search.maven.org/search?q=a:cassandre-trading-bot-spring-boot-starter-basic-archetype",target:"_blank",rel:"noopener noreferrer"},v=l(`<div class="language-bash line-numbers-mode" data-ext="sh"><pre class="language-bash"><code>mvn archetype:generate <span class="token parameter variable">-B</span> <span class="token punctuation">\\</span>
<span class="token parameter variable">-DarchetypeGroupId</span><span class="token operator">=</span>tech.cassandre.trading.bot <span class="token punctuation">\\</span>
<span class="token parameter variable">-DarchetypeArtifactId</span><span class="token operator">=</span>cassandre-trading-bot-spring-boot-starter-basic-archetype <span class="token punctuation">\\</span>
<span class="token parameter variable">-DarchetypeVersion</span><span class="token operator">=</span><span class="token number">6.0</span>.0 <span class="token punctuation">\\</span>
<span class="token parameter variable">-DgroupId</span><span class="token operator">=</span>com.mycompany.bot <span class="token punctuation">\\</span>
<span class="token parameter variable">-DartifactId</span><span class="token operator">=</span>my-trading-bot <span class="token punctuation">\\</span>
<span class="token parameter variable">-Dversion</span><span class="token operator">=</span><span class="token number">1.0</span>-SNAPSHOT <span class="token punctuation">\\</span>
<span class="token parameter variable">-Dpackage</span><span class="token operator">=</span>com.mycompany.bot
</code></pre><div class="line-numbers" aria-hidden="true"><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div></div></div><p>The project will have the following structure and files:</p><div class="language-text line-numbers-mode" data-ext="text"><pre class="language-text"><code>my-trading-bot/
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

</code></pre><div class="line-numbers" aria-hidden="true"><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div></div></div>`,3);function u(m,b){const n=r("ExternalLinkIcon");return i(),t("div",null,[d,a("p",null,[e("If you don't have an existing spring boot project, you can use our "),a("a",p,[e("maven archetype"),c(n)]),e(" to generate one:")]),v])}const k=s(o,[["render",u],["__file","create-your-project.html.vue"]]);export{k as default};
