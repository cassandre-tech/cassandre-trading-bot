import{_ as t,r as p,o,c,b as n,a as e,F as l,d as a,e as r}from"./app.18e19bd2.js";const i={},u=n("h1",{id:"configure-exchange-connection",tabindex:"-1"},[n("a",{class:"header-anchor",href:"#configure-exchange-connection","aria-hidden":"true"},"#"),a(" Configure exchange connection")],-1),d=n("h2",{id:"how-does-it-works",tabindex:"-1"},[n("a",{class:"header-anchor",href:"#how-does-it-works","aria-hidden":"true"},"#"),a(" How does it works?")],-1),g=n("p",null,"There are two steps to configure an exchange connection in Cassandre.",-1),k=a("Cassandre uses "),h={href:"https://github.com/knowm/XChange",target:"_blank",rel:"noopener noreferrer"},m=a("XChange"),b=a(", a Java library providing a streamlined API for interacting with 60+ Bitcoin and Altcoin exchanges. The first thing you have to do is to find the XChange library suited for the Exchange you chose. The list is "),v={href:"https://search.maven.org/search?q=org.knowm.xchange",target:"_blank",rel:"noopener noreferrer"},x=a("here"),y=a("."),f=r(`<p>For example, for a Coinbase connection, you have to add this to your <code>pom.xml</code>:</p><div class="language-xml ext-xml line-numbers-mode"><pre class="language-xml"><code><span class="token tag"><span class="token tag"><span class="token punctuation">&lt;</span>dependency</span><span class="token punctuation">&gt;</span></span>
    <span class="token tag"><span class="token tag"><span class="token punctuation">&lt;</span>groupId</span><span class="token punctuation">&gt;</span></span>org.knowm.xchange<span class="token tag"><span class="token tag"><span class="token punctuation">&lt;/</span>groupId</span><span class="token punctuation">&gt;</span></span>
    <span class="token tag"><span class="token tag"><span class="token punctuation">&lt;</span>artifactId</span><span class="token punctuation">&gt;</span></span>xchange-coinbasepro<span class="token tag"><span class="token tag"><span class="token punctuation">&lt;/</span>artifactId</span><span class="token punctuation">&gt;</span></span>
    <span class="token tag"><span class="token tag"><span class="token punctuation">&lt;</span>version</span><span class="token punctuation">&gt;</span></span>5.0.12<span class="token tag"><span class="token tag"><span class="token punctuation">&lt;/</span>version</span><span class="token punctuation">&gt;</span></span>
<span class="token tag"><span class="token tag"><span class="token punctuation">&lt;/</span>dependency</span><span class="token punctuation">&gt;</span></span>
</code></pre><div class="line-numbers" aria-hidden="true"><span class="line-number">1</span><br><span class="line-number">2</span><br><span class="line-number">3</span><br><span class="line-number">4</span><br><span class="line-number">5</span><br></div></div><p>The second step is to update those properties in your <code>application.properties</code>:</p><div class="language-properties ext-properties line-numbers-mode"><pre class="language-properties"><code><span class="token comment"># Exchange configuration.</span>
<span class="token key attr-name">cassandre.trading.bot.exchange.driver-class-name</span><span class="token punctuation">=</span><span class="token value attr-value">org.knowm.xchange.coinbasepro.CoinbaseProExchange</span>
<span class="token key attr-name">cassandre.trading.bot.exchange.username</span><span class="token punctuation">=</span><span class="token value attr-value">kucoin.cassandre.test@gmail.com</span>
<span class="token key attr-name">cassandre.trading.bot.exchange.passphrase</span><span class="token punctuation">=</span><span class="token value attr-value">cassandre</span>
<span class="token key attr-name">cassandre.trading.bot.exchange.key</span><span class="token punctuation">=</span><span class="token value attr-value">6054ad25365ac6000689a998</span>
<span class="token key attr-name">cassandre.trading.bot.exchange.secret</span><span class="token punctuation">=</span><span class="token value attr-value">af080d55-afe3-47c9-8ec1-4b479fbcc5e7</span>
</code></pre><div class="line-numbers" aria-hidden="true"><span class="line-number">1</span><br><span class="line-number">2</span><br><span class="line-number">3</span><br><span class="line-number">4</span><br><span class="line-number">5</span><br><span class="line-number">6</span><br></div></div><p>For <code>cassandre.trading.bot.exchange.driver-class-name</code>, you have to set the main class inside the XChange library.</p><p>The other parameters <code>username</code>, <code>passphrase</code>, <code>key</code> and<code>secret</code> are authentication parameters given by the Exchange when you will create your API access in your account.</p><h2 id="configuration-examples" tabindex="-1"><a class="header-anchor" href="#configuration-examples" aria-hidden="true">#</a> Configuration examples</h2><h3 id="kucoin" tabindex="-1"><a class="header-anchor" href="#kucoin" aria-hidden="true">#</a> Kucoin</h3><p>Add this dependency to your \u0300pom.xml\`:</p><div class="language-xml ext-xml line-numbers-mode"><pre class="language-xml"><code><span class="token tag"><span class="token tag"><span class="token punctuation">&lt;</span>dependency</span><span class="token punctuation">&gt;</span></span>
    <span class="token tag"><span class="token tag"><span class="token punctuation">&lt;</span>groupId</span><span class="token punctuation">&gt;</span></span>org.knowm.xchange<span class="token tag"><span class="token tag"><span class="token punctuation">&lt;/</span>groupId</span><span class="token punctuation">&gt;</span></span>
    <span class="token tag"><span class="token tag"><span class="token punctuation">&lt;</span>artifactId</span><span class="token punctuation">&gt;</span></span>xchange-kucoin<span class="token tag"><span class="token tag"><span class="token punctuation">&lt;/</span>artifactId</span><span class="token punctuation">&gt;</span></span>
    <span class="token tag"><span class="token tag"><span class="token punctuation">&lt;</span>version</span><span class="token punctuation">&gt;</span></span>5.0.12<span class="token tag"><span class="token tag"><span class="token punctuation">&lt;/</span>version</span><span class="token punctuation">&gt;</span></span>
<span class="token tag"><span class="token tag"><span class="token punctuation">&lt;/</span>dependency</span><span class="token punctuation">&gt;</span></span>
</code></pre><div class="line-numbers" aria-hidden="true"><span class="line-number">1</span><br><span class="line-number">2</span><br><span class="line-number">3</span><br><span class="line-number">4</span><br><span class="line-number">5</span><br></div></div><p>and update your <code>application.properties</code>:</p><div class="language-properties ext-properties line-numbers-mode"><pre class="language-properties"><code><span class="token key attr-name">cassandre.trading.bot.exchange.driver-class-name</span><span class="token punctuation">=</span><span class="token value attr-value">kucoin</span>
</code></pre><div class="line-numbers" aria-hidden="true"><span class="line-number">1</span><br></div></div><h3 id="coinbase" tabindex="-1"><a class="header-anchor" href="#coinbase" aria-hidden="true">#</a> Coinbase</h3><p>Add this dependency to your \u0300pom.xml\`:</p><div class="language-xml ext-xml line-numbers-mode"><pre class="language-xml"><code><span class="token tag"><span class="token tag"><span class="token punctuation">&lt;</span>dependency</span><span class="token punctuation">&gt;</span></span>
    <span class="token tag"><span class="token tag"><span class="token punctuation">&lt;</span>groupId</span><span class="token punctuation">&gt;</span></span>org.knowm.xchange<span class="token tag"><span class="token tag"><span class="token punctuation">&lt;/</span>groupId</span><span class="token punctuation">&gt;</span></span>
    <span class="token tag"><span class="token tag"><span class="token punctuation">&lt;</span>artifactId</span><span class="token punctuation">&gt;</span></span>xchange-coinbasepro<span class="token tag"><span class="token tag"><span class="token punctuation">&lt;/</span>artifactId</span><span class="token punctuation">&gt;</span></span>
    <span class="token tag"><span class="token tag"><span class="token punctuation">&lt;</span>version</span><span class="token punctuation">&gt;</span></span>5.0.12<span class="token tag"><span class="token tag"><span class="token punctuation">&lt;/</span>version</span><span class="token punctuation">&gt;</span></span>
<span class="token tag"><span class="token tag"><span class="token punctuation">&lt;/</span>dependency</span><span class="token punctuation">&gt;</span></span>
</code></pre><div class="line-numbers" aria-hidden="true"><span class="line-number">1</span><br><span class="line-number">2</span><br><span class="line-number">3</span><br><span class="line-number">4</span><br><span class="line-number">5</span><br></div></div><p>and update your <code>application.properties</code>:</p><div class="language-properties ext-properties line-numbers-mode"><pre class="language-properties"><code><span class="token key attr-name">cassandre.trading.bot.exchange.driver-class-name</span><span class="token punctuation">=</span><span class="token value attr-value">org.knowm.xchange.coinbasepro.CoinbaseProExchange</span>
</code></pre><div class="line-numbers" aria-hidden="true"><span class="line-number">1</span><br></div></div><h3 id="binance" tabindex="-1"><a class="header-anchor" href="#binance" aria-hidden="true">#</a> Binance</h3><p>Add this dependency to your \u0300pom.xml\`:</p><div class="language-xml ext-xml line-numbers-mode"><pre class="language-xml"><code><span class="token tag"><span class="token tag"><span class="token punctuation">&lt;</span>dependency</span><span class="token punctuation">&gt;</span></span>
    <span class="token tag"><span class="token tag"><span class="token punctuation">&lt;</span>groupId</span><span class="token punctuation">&gt;</span></span>org.knowm.xchange<span class="token tag"><span class="token tag"><span class="token punctuation">&lt;/</span>groupId</span><span class="token punctuation">&gt;</span></span>
    <span class="token tag"><span class="token tag"><span class="token punctuation">&lt;</span>artifactId</span><span class="token punctuation">&gt;</span></span>xchange-binance<span class="token tag"><span class="token tag"><span class="token punctuation">&lt;/</span>artifactId</span><span class="token punctuation">&gt;</span></span>
    <span class="token tag"><span class="token tag"><span class="token punctuation">&lt;</span>version</span><span class="token punctuation">&gt;</span></span>5.0.12<span class="token tag"><span class="token tag"><span class="token punctuation">&lt;/</span>version</span><span class="token punctuation">&gt;</span></span>
<span class="token tag"><span class="token tag"><span class="token punctuation">&lt;/</span>dependency</span><span class="token punctuation">&gt;</span></span>
</code></pre><div class="line-numbers" aria-hidden="true"><span class="line-number">1</span><br><span class="line-number">2</span><br><span class="line-number">3</span><br><span class="line-number">4</span><br><span class="line-number">5</span><br></div></div><p>and update your <code>application.properties</code>:</p><div class="language-properties ext-properties line-numbers-mode"><pre class="language-properties"><code><span class="token key attr-name">cassandre.trading.bot.exchange.driver-class-name</span><span class="token punctuation">=</span><span class="token value attr-value">org.knowm.xchange.binance.BinanceExchange</span>
</code></pre><div class="line-numbers" aria-hidden="true"><span class="line-number">1</span><br></div></div><p>On Binance, you should not ask for data too often, or you will get a <code>Way too much request weight used</code> error, use those parameters in your <code>application.properties</code>:</p><div class="language-properties ext-properties line-numbers-mode"><pre class="language-properties"><code><span class="token key attr-name">cassandre.trading.bot.exchange.rates.account</span><span class="token punctuation">=</span><span class="token value attr-value">PT30S</span>
<span class="token key attr-name">cassandre.trading.bot.exchange.rates.ticker</span><span class="token punctuation">=</span><span class="token value attr-value">PT30S</span>
<span class="token key attr-name">cassandre.trading.bot.exchange.rates.trade</span><span class="token punctuation">=</span><span class="token value attr-value">PT30S</span>
</code></pre><div class="line-numbers" aria-hidden="true"><span class="line-number">1</span><br><span class="line-number">2</span><br><span class="line-number">3</span><br></div></div>`,24);function _(w,I){const s=p("ExternalLinkIcon");return o(),c(l,null,[u,d,g,n("p",null,[k,n("a",h,[m,e(s)]),b,n("a",v,[x,e(s)]),y]),f],64)}var E=t(i,[["render",_],["__file","exchange-connection-configuration.html.vue"]]);export{E as default};