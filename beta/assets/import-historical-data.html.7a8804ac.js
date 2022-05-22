import{_ as r,r as s,o as i,c as n,b as t,a,F as d,e as u,d as e}from"./app.9376bde5.js";const c={},h=u(`<h1 id="import-historical-data" tabindex="-1"><a class="header-anchor" href="#import-historical-data" aria-hidden="true">#</a> Import historical data</h1><h2 id="overview" tabindex="-1"><a class="header-anchor" href="#overview" aria-hidden="true">#</a> Overview</h2><p>This feature allows you to import historical data (tickers you selected) in Cassandre database, so you can initialise your strategy.</p><h2 id="data-file-format" tabindex="-1"><a class="header-anchor" href="#data-file-format" aria-hidden="true">#</a> Data file &amp; format</h2><p>At startup, Cassandre will search for all files starting with <code>tickers-to-import</code> and ending with <code>csv</code>.</p><p>This is how the file must be formatted:</p><div class="language-text ext-text line-numbers-mode"><pre class="language-text"><code>CURRENCY_PAIR,OPEN,LAST,BID,ASK,HIGH,LOW,VWAP,VOLUME,QUOTE_VOLUME,BID_SIZE,ASK_SIZE,TIMESTAMP
&quot;BTC/USDT&quot;,&quot;0.00000001&quot;,&quot;0.00000002&quot;,&quot;0.00000003&quot;,&quot;0.00000004&quot;,&quot;0.00000005&quot;,&quot;0.00000006&quot;,&quot;0.00000007&quot;,&quot;0.00000008&quot;,&quot;0.00000009&quot;,&quot;0.00000010&quot;,&quot;0.00000011&quot;,1508546000
&quot;BTC/USDT&quot;,&quot;1.00000001&quot;,&quot;1.00000002&quot;,&quot;1.00000003&quot;,&quot;1.00000004&quot;,&quot;1.00000005&quot;,&quot;1.00000006&quot;,&quot;1.00000007&quot;,&quot;1.00000008&quot;,&quot;1.00000009&quot;,&quot;1.00000010&quot;,&quot;1.00000011&quot;,1508446000
&quot;ETH/USDT&quot;,&quot;2.00000001&quot;,&quot;2.00000002&quot;,&quot;2.00000003&quot;,&quot;2.00000004&quot;,&quot;2.00000005&quot;,&quot;2.00000006&quot;,&quot;2.00000007&quot;,&quot;2.00000008&quot;,&quot;2.00000009&quot;,&quot;2.00000010&quot;,&quot;2.00000011&quot;,1508346000
</code></pre><div class="line-numbers" aria-hidden="true"><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div></div></div><h2 id="when-to-initialize-data" tabindex="-1"><a class="header-anchor" href="#when-to-initialize-data" aria-hidden="true">#</a> When to initialize data?</h2>`,8),l=e("In you strategy, you should implement the "),q={href:"https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#initialize()",target:"_blank",rel:"noopener noreferrer"},_=e("initialize()"),g=e(" method. This method is executed by Cassandre before any other data (tickers, orders, trades...) is pushed to the strategy."),m=t("h2",{id:"access-your-data-in-your-strategy",tabindex:"-1"},[t("a",{class:"header-anchor",href:"#access-your-data-in-your-strategy","aria-hidden":"true"},"#"),e(" Access your data in your strategy")],-1),p=t("p",null,"In your strategy, you can access the data with two methods:",-1),f={href:"https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#getImportedTickers()",target:"_blank",rel:"noopener noreferrer"},b=e("getImportedTickers()"),y=e("."),v={href:"https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#getImportedTickers(tech.cassandre.trading.bot.dto.util.CurrencyPairDTO)",target:"_blank",rel:"noopener noreferrer"},w=e("getImportedTickers(CurrencyPairDTO currencyPairDTO)"),T=e(".");function x(I,k){const o=s("ExternalLinkIcon");return i(),n(d,null,[h,t("p",null,[l,t("a",q,[_,a(o)]),g]),m,p,t("ul",null,[t("li",null,[t("a",f,[b,a(o)]),y]),t("li",null,[t("a",v,[w,a(o)]),T])])],64)}var S=r(c,[["render",x],["__file","import-historical-data.html.vue"]]);export{S as default};
