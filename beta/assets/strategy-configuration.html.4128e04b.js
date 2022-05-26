import{_ as t,r,o,c,b as a,a as i,e as n,d as e}from"./app.1a95bf67.js";const d={},l=n(`<h1 id="configuration" tabindex="-1"><a class="header-anchor" href="#configuration" aria-hidden="true">#</a> Configuration</h1><p>Le fichier de configuration de Cassandre se trouve ici : <code>src/main/resources/application.properties</code>.</p><p>Voici un exemple :</p><div class="language-properties ext-properties line-numbers-mode"><pre class="language-properties"><code><span class="token comment">#</span>
<span class="token comment"># Exchange configuration.</span>
<span class="token key attr-name">cassandre.trading.bot.exchange.driver-class-name</span><span class="token punctuation">=</span><span class="token value attr-value">org.knowm.xchange.kucoin.KucoinExchange</span>
<span class="token key attr-name">cassandre.trading.bot.exchange.username</span><span class="token punctuation">=</span><span class="token value attr-value">kucoin.cassandre.test@gmail.com</span>
<span class="token key attr-name">cassandre.trading.bot.exchange.passphrase</span><span class="token punctuation">=</span><span class="token value attr-value">cassandre</span>
<span class="token key attr-name">cassandre.trading.bot.exchange.key</span><span class="token punctuation">=</span><span class="token value attr-value">6054ad25365ac6000689a998</span>
<span class="token key attr-name">cassandre.trading.bot.exchange.secret</span><span class="token punctuation">=</span><span class="token value attr-value">af080d55-afe3-47c9-8ec1-4b479fbcc5e7</span>
<span class="token comment">#</span>
<span class="token comment"># Modes.</span>
<span class="token key attr-name">cassandre.trading.bot.exchange.modes.sandbox</span><span class="token punctuation">=</span><span class="token value attr-value">true</span>
<span class="token key attr-name">cassandre.trading.bot.exchange.modes.dry</span><span class="token punctuation">=</span><span class="token value attr-value">false</span>
<span class="token comment">#</span>
<span class="token comment"># Exchange API calls rates (In ms or standard ISO 8601 duration like &#39;PT5S&#39;).</span>
<span class="token key attr-name">cassandre.trading.bot.exchange.rates.account</span><span class="token punctuation">=</span><span class="token value attr-value">2000</span>
<span class="token key attr-name">cassandre.trading.bot.exchange.rates.ticker</span><span class="token punctuation">=</span><span class="token value attr-value">2000</span>
<span class="token key attr-name">cassandre.trading.bot.exchange.rates.trade</span><span class="token punctuation">=</span><span class="token value attr-value">2000</span>
<span class="token comment">#</span>
<span class="token comment"># Database configuration.</span>
<span class="token key attr-name">spring.datasource.driver-class-name</span><span class="token punctuation">=</span><span class="token value attr-value">org.hsqldb.jdbc.JDBCDriver</span>
<span class="token key attr-name">spring.datasource.url</span><span class="token punctuation">=</span><span class="token value attr-value">jdbc:hsqldb:mem:cassandre</span>
<span class="token key attr-name">spring.datasource.username</span><span class="token punctuation">=</span><span class="token value attr-value">sa</span>
<span class="token key attr-name">spring.datasource.password</span><span class="token punctuation">=</span>
</code></pre><div class="line-numbers" aria-hidden="true"><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div></div></div><h2 id="configuration-de-l-exchange" tabindex="-1"><a class="header-anchor" href="#configuration-de-l-exchange" aria-hidden="true">#</a> Configuration de l&#39;exchange</h2>`,5),p=e("La section "),u=a("code",null,"Exchange configuration",-1),m=e(" contient les param\xE8tres de connexion \xE0 l'exchange. Le premier, "),v=a("code",null,"driver-class-name",-1),h=e(", correspond \xE0 la classe "),g={href:"https://github.com/knowm/XChange",target:"_blank",rel:"noopener noreferrer"},k=e("XChange"),b=e(" que Cassandre doit utiliser pour se connecter."),f=n('<p>Les autres param\xE8tres sont les param\xE8tres de connexion que votre exchange vous fournira. Vous pouvez voir des exemples de configuration <a href="../guides/configuration/exchange-connection-configuration">ici</a>.</p><h2 id="modes" tabindex="-1"><a class="header-anchor" href="#modes" aria-hidden="true">#</a> Modes</h2><p>Cassandre offre de deux modes :</p><ul><li>Le mode <code>sandbox</code>, support\xE9 seulement par certains exchanges, permet d&#39;utiliser un &#39;faux compte&#39; sur l&#39;exchange et donc de travailler de mani\xE8re simul\xE9e. Kucoin &amp; Coinbase supportent cette fonctionnalit\xE9.</li><li>Le mode <code>dry</code> de Cassandre simule, \xE0 l&#39;int\xE9rieur de Cassandre, un exchange. Ce mode permet de tester votre strat\xE9gie en local sur des donn\xE9es historiques et de voir si elle g\xE9n\xE8re des b\xE9n\xE9fices ou des pertes. Vous pourrez donc valider vos id\xE9es avec des tests unitaires avant de vous lancer en production.</li></ul><h2 id="taux-de-rafraichissement" tabindex="-1"><a class="header-anchor" href="#taux-de-rafraichissement" aria-hidden="true">#</a> Taux de rafraichissement</h2><p>Les param\xE8tres <code>rates</code> permettent de dire \xE0 Cassandre \xE0 quelle fr\xE9quence il faut r\xE9cup\xE9rer les donn\xE9es depuis l&#39;exchange.</p><h2 id="configuration-de-la-base-de-donnees" tabindex="-1"><a class="header-anchor" href="#configuration-de-la-base-de-donnees" aria-hidden="true">#</a> Configuration de la base de donn\xE9es.</h2><p>Ces param\xE8tres indiquent \xE0 Cassandre dans quelle base de donn\xE9es Cassandre doit sauvegarder ses informations (Cassandre se charge de cr\xE9er la structure de la base au d\xE9marrage de votre application).</p>',8);function x(_,C){const s=r("ExternalLinkIcon");return o(),c("div",null,[l,a("p",null,[p,u,m,v,h,a("a",g,[k,i(s)]),b]),f])}var q=t(d,[["render",x],["__file","strategy-configuration.html.vue"]]);export{q as default};
