import{_ as e,r as o,o as p,c,b as s,a as t,e as i,d as n}from"./app.1a95bf67.js";const l={},u=i(`<h1 id="explorez-le-code-genere" tabindex="-1"><a class="header-anchor" href="#explorez-le-code-genere" aria-hidden="true">#</a> Explorez le code g\xE9n\xE9r\xE9</h1><p>Notre archetype a cr\xE9\xE9 une strat\xE9gie que vous trouverez dans le fichier <code>src/main/java/com/mycompany/bot/SimpleStrategy.java</code>:</p><div class="language-java ext-java line-numbers-mode"><pre class="language-java"><code><span class="token keyword">package</span> <span class="token namespace">com<span class="token punctuation">.</span>mycompany<span class="token punctuation">.</span>bot</span><span class="token punctuation">;</span>

<span class="token keyword">import</span> <span class="token import"><span class="token namespace">tech<span class="token punctuation">.</span>cassandre<span class="token punctuation">.</span>trading<span class="token punctuation">.</span>bot<span class="token punctuation">.</span>dto<span class="token punctuation">.</span>market<span class="token punctuation">.</span></span><span class="token class-name">TickerDTO</span></span><span class="token punctuation">;</span>
<span class="token keyword">import</span> <span class="token import"><span class="token namespace">tech<span class="token punctuation">.</span>cassandre<span class="token punctuation">.</span>trading<span class="token punctuation">.</span>bot<span class="token punctuation">.</span>dto<span class="token punctuation">.</span>position<span class="token punctuation">.</span></span><span class="token class-name">PositionDTO</span></span><span class="token punctuation">;</span>
<span class="token keyword">import</span> <span class="token import"><span class="token namespace">tech<span class="token punctuation">.</span>cassandre<span class="token punctuation">.</span>trading<span class="token punctuation">.</span>bot<span class="token punctuation">.</span>dto<span class="token punctuation">.</span>trade<span class="token punctuation">.</span></span><span class="token class-name">OrderDTO</span></span><span class="token punctuation">;</span>
<span class="token keyword">import</span> <span class="token import"><span class="token namespace">tech<span class="token punctuation">.</span>cassandre<span class="token punctuation">.</span>trading<span class="token punctuation">.</span>bot<span class="token punctuation">.</span>dto<span class="token punctuation">.</span>trade<span class="token punctuation">.</span></span><span class="token class-name">TradeDTO</span></span><span class="token punctuation">;</span>
<span class="token keyword">import</span> <span class="token import"><span class="token namespace">tech<span class="token punctuation">.</span>cassandre<span class="token punctuation">.</span>trading<span class="token punctuation">.</span>bot<span class="token punctuation">.</span>dto<span class="token punctuation">.</span>user<span class="token punctuation">.</span></span><span class="token class-name">AccountDTO</span></span><span class="token punctuation">;</span>
<span class="token keyword">import</span> <span class="token import"><span class="token namespace">tech<span class="token punctuation">.</span>cassandre<span class="token punctuation">.</span>trading<span class="token punctuation">.</span>bot<span class="token punctuation">.</span>dto<span class="token punctuation">.</span>util<span class="token punctuation">.</span></span><span class="token class-name">CurrencyPairDTO</span></span><span class="token punctuation">;</span>
<span class="token keyword">import</span> <span class="token import"><span class="token namespace">tech<span class="token punctuation">.</span>cassandre<span class="token punctuation">.</span>trading<span class="token punctuation">.</span>bot<span class="token punctuation">.</span>strategy<span class="token punctuation">.</span></span><span class="token class-name">BasicCassandreStrategy</span></span><span class="token punctuation">;</span>
<span class="token keyword">import</span> <span class="token import"><span class="token namespace">tech<span class="token punctuation">.</span>cassandre<span class="token punctuation">.</span>trading<span class="token punctuation">.</span>bot<span class="token punctuation">.</span>strategy<span class="token punctuation">.</span></span><span class="token class-name">CassandreStrategy</span></span><span class="token punctuation">;</span>

<span class="token keyword">import</span> <span class="token import"><span class="token namespace">java<span class="token punctuation">.</span>util<span class="token punctuation">.</span></span><span class="token class-name">Map</span></span><span class="token punctuation">;</span>
<span class="token keyword">import</span> <span class="token import"><span class="token namespace">java<span class="token punctuation">.</span>util<span class="token punctuation">.</span></span><span class="token class-name">Optional</span></span><span class="token punctuation">;</span>
<span class="token keyword">import</span> <span class="token import"><span class="token namespace">java<span class="token punctuation">.</span>util<span class="token punctuation">.</span></span><span class="token class-name">Set</span></span><span class="token punctuation">;</span>

<span class="token keyword">import</span> <span class="token keyword">static</span> <span class="token import static"><span class="token namespace">tech<span class="token punctuation">.</span>cassandre<span class="token punctuation">.</span>trading<span class="token punctuation">.</span>bot<span class="token punctuation">.</span>dto<span class="token punctuation">.</span>util<span class="token punctuation">.</span></span><span class="token class-name">CurrencyDTO</span><span class="token punctuation">.</span><span class="token static">BTC</span></span><span class="token punctuation">;</span>
<span class="token keyword">import</span> <span class="token keyword">static</span> <span class="token import static"><span class="token namespace">tech<span class="token punctuation">.</span>cassandre<span class="token punctuation">.</span>trading<span class="token punctuation">.</span>bot<span class="token punctuation">.</span>dto<span class="token punctuation">.</span>util<span class="token punctuation">.</span></span><span class="token class-name">CurrencyDTO</span><span class="token punctuation">.</span><span class="token static">USDT</span></span><span class="token punctuation">;</span>

<span class="token doc-comment comment">/**
 * Simple strategy.
 */</span>
<span class="token annotation punctuation">@CassandreStrategy</span><span class="token punctuation">(</span>strategyName <span class="token operator">=</span> <span class="token string">&quot;Simple strategy&quot;</span><span class="token punctuation">)</span>
<span class="token keyword">public</span> <span class="token keyword">final</span> <span class="token keyword">class</span> <span class="token class-name">SimpleStrategy</span> <span class="token keyword">extends</span> <span class="token class-name">BasicCassandreStrategy</span> <span class="token punctuation">{</span>

    <span class="token annotation punctuation">@Override</span>
    <span class="token keyword">public</span> <span class="token class-name">Set</span><span class="token generics"><span class="token punctuation">&lt;</span><span class="token class-name">CurrencyPairDTO</span><span class="token punctuation">&gt;</span></span> <span class="token function">getRequestedCurrencyPairs</span><span class="token punctuation">(</span><span class="token punctuation">)</span> <span class="token punctuation">{</span>
        <span class="token comment">// We only ask for BTC/USDT tickers (Base currency : BTC / Quote currency : USDT).</span>
        <span class="token keyword">return</span> <span class="token class-name">Set</span><span class="token punctuation">.</span><span class="token function">of</span><span class="token punctuation">(</span><span class="token keyword">new</span> <span class="token class-name">CurrencyPairDTO</span><span class="token punctuation">(</span>BTC<span class="token punctuation">,</span> USDT<span class="token punctuation">)</span><span class="token punctuation">)</span><span class="token punctuation">;</span>
    <span class="token punctuation">}</span>

    <span class="token annotation punctuation">@Override</span>
    <span class="token keyword">public</span> <span class="token class-name">Optional</span><span class="token generics"><span class="token punctuation">&lt;</span><span class="token class-name">AccountDTO</span><span class="token punctuation">&gt;</span></span> <span class="token function">getTradeAccount</span><span class="token punctuation">(</span><span class="token class-name">Set</span><span class="token generics"><span class="token punctuation">&lt;</span><span class="token class-name">AccountDTO</span><span class="token punctuation">&gt;</span></span> accounts<span class="token punctuation">)</span> <span class="token punctuation">{</span>
        <span class="token comment">// From all the accounts we have on the exchange, we must return the one we use for trading.</span>
        <span class="token keyword">if</span> <span class="token punctuation">(</span>accounts<span class="token punctuation">.</span><span class="token function">size</span><span class="token punctuation">(</span><span class="token punctuation">)</span> <span class="token operator">==</span> <span class="token number">1</span><span class="token punctuation">)</span> <span class="token punctuation">{</span>
            <span class="token comment">// If there is only one on the exchange, we choose this one.</span>
            <span class="token keyword">return</span> accounts<span class="token punctuation">.</span><span class="token function">stream</span><span class="token punctuation">(</span><span class="token punctuation">)</span><span class="token punctuation">.</span><span class="token function">findFirst</span><span class="token punctuation">(</span><span class="token punctuation">)</span><span class="token punctuation">;</span>
        <span class="token punctuation">}</span> <span class="token keyword">else</span> <span class="token punctuation">{</span>
            <span class="token comment">// If there are several accounts on the exchange, we choose the one whose name is &quot;trade&quot;.</span>
            <span class="token keyword">return</span> accounts<span class="token punctuation">.</span><span class="token function">stream</span><span class="token punctuation">(</span><span class="token punctuation">)</span>
                    <span class="token punctuation">.</span><span class="token function">filter</span><span class="token punctuation">(</span>a <span class="token operator">-&gt;</span> <span class="token string">&quot;trade&quot;</span><span class="token punctuation">.</span><span class="token function">equalsIgnoreCase</span><span class="token punctuation">(</span>a<span class="token punctuation">.</span><span class="token function">getName</span><span class="token punctuation">(</span><span class="token punctuation">)</span><span class="token punctuation">)</span><span class="token punctuation">)</span>
                    <span class="token punctuation">.</span><span class="token function">findFirst</span><span class="token punctuation">(</span><span class="token punctuation">)</span><span class="token punctuation">;</span>
        <span class="token punctuation">}</span>
    <span class="token punctuation">}</span>

    <span class="token annotation punctuation">@Override</span>
    <span class="token keyword">public</span> <span class="token keyword">void</span> <span class="token function">onAccountsUpdates</span><span class="token punctuation">(</span><span class="token keyword">final</span> <span class="token class-name">Map</span><span class="token generics"><span class="token punctuation">&lt;</span><span class="token class-name">String</span><span class="token punctuation">,</span> <span class="token class-name">AccountDTO</span><span class="token punctuation">&gt;</span></span> accounts<span class="token punctuation">)</span> <span class="token punctuation">{</span>
        <span class="token comment">// Here, we will receive an AccountDTO each time there is a change on your account.</span>
        accounts<span class="token punctuation">.</span><span class="token function">values</span><span class="token punctuation">(</span><span class="token punctuation">)</span><span class="token punctuation">.</span><span class="token function">forEach</span><span class="token punctuation">(</span>account <span class="token operator">-&gt;</span> <span class="token class-name">System</span><span class="token punctuation">.</span>out<span class="token punctuation">.</span><span class="token function">println</span><span class="token punctuation">(</span><span class="token string">&quot;Received information about an account: &quot;</span> <span class="token operator">+</span> account<span class="token punctuation">)</span><span class="token punctuation">)</span><span class="token punctuation">;</span>
    <span class="token punctuation">}</span>

    <span class="token annotation punctuation">@Override</span>
    <span class="token keyword">public</span> <span class="token keyword">void</span> <span class="token function">onTickersUpdates</span><span class="token punctuation">(</span><span class="token keyword">final</span> <span class="token class-name">Map</span><span class="token generics"><span class="token punctuation">&lt;</span><span class="token class-name">CurrencyPairDTO</span><span class="token punctuation">,</span> <span class="token class-name">TickerDTO</span><span class="token punctuation">&gt;</span></span> tickers<span class="token punctuation">)</span> <span class="token punctuation">{</span>
        <span class="token comment">// Here we will receive all tickers we required from the exchange.</span>
        tickers<span class="token punctuation">.</span><span class="token function">values</span><span class="token punctuation">(</span><span class="token punctuation">)</span><span class="token punctuation">.</span><span class="token function">forEach</span><span class="token punctuation">(</span>ticker <span class="token operator">-&gt;</span> <span class="token class-name">System</span><span class="token punctuation">.</span>out<span class="token punctuation">.</span><span class="token function">println</span><span class="token punctuation">(</span><span class="token string">&quot;Received information about a ticker: &quot;</span> <span class="token operator">+</span> ticker<span class="token punctuation">)</span><span class="token punctuation">)</span><span class="token punctuation">;</span>
    <span class="token punctuation">}</span>

    <span class="token annotation punctuation">@Override</span>
    <span class="token keyword">public</span> <span class="token keyword">void</span> <span class="token function">onOrdersUpdates</span><span class="token punctuation">(</span><span class="token keyword">final</span> <span class="token class-name">Map</span><span class="token generics"><span class="token punctuation">&lt;</span><span class="token class-name">String</span><span class="token punctuation">,</span> <span class="token class-name">OrderDTO</span><span class="token punctuation">&gt;</span></span> orders<span class="token punctuation">)</span> <span class="token punctuation">{</span>
        <span class="token comment">// Here, we will receive an OrderDTO each time order data has changed on the exchange.</span>
        orders<span class="token punctuation">.</span><span class="token function">values</span><span class="token punctuation">(</span><span class="token punctuation">)</span><span class="token punctuation">.</span><span class="token function">forEach</span><span class="token punctuation">(</span>order <span class="token operator">-&gt;</span> <span class="token class-name">System</span><span class="token punctuation">.</span>out<span class="token punctuation">.</span><span class="token function">println</span><span class="token punctuation">(</span><span class="token string">&quot;Received information about an order: &quot;</span> <span class="token operator">+</span> order<span class="token punctuation">)</span><span class="token punctuation">)</span><span class="token punctuation">;</span>
    <span class="token punctuation">}</span>

    <span class="token annotation punctuation">@Override</span>
    <span class="token keyword">public</span> <span class="token keyword">void</span> <span class="token function">onTradesUpdates</span><span class="token punctuation">(</span><span class="token keyword">final</span> <span class="token class-name">Map</span><span class="token generics"><span class="token punctuation">&lt;</span><span class="token class-name">String</span><span class="token punctuation">,</span> <span class="token class-name">TradeDTO</span><span class="token punctuation">&gt;</span></span> trades<span class="token punctuation">)</span> <span class="token punctuation">{</span>
        <span class="token comment">// Here, we will receive a TradeDTO each time trade data has changed on the exchange.</span>
        trades<span class="token punctuation">.</span><span class="token function">values</span><span class="token punctuation">(</span><span class="token punctuation">)</span><span class="token punctuation">.</span><span class="token function">forEach</span><span class="token punctuation">(</span>trade <span class="token operator">-&gt;</span> <span class="token class-name">System</span><span class="token punctuation">.</span>out<span class="token punctuation">.</span><span class="token function">println</span><span class="token punctuation">(</span><span class="token string">&quot;Received information about a trade: &quot;</span> <span class="token operator">+</span> trade<span class="token punctuation">)</span><span class="token punctuation">)</span><span class="token punctuation">;</span>
    <span class="token punctuation">}</span>

    <span class="token annotation punctuation">@Override</span>
    <span class="token keyword">public</span> <span class="token keyword">void</span> <span class="token function">onPositionsUpdates</span><span class="token punctuation">(</span><span class="token keyword">final</span> <span class="token class-name">Map</span><span class="token generics"><span class="token punctuation">&lt;</span><span class="token class-name">Long</span><span class="token punctuation">,</span> <span class="token class-name">PositionDTO</span><span class="token punctuation">&gt;</span></span> positions<span class="token punctuation">)</span> <span class="token punctuation">{</span>
        <span class="token comment">// Here, we will receive a PositionDTO each time a position has changed.</span>
        positions<span class="token punctuation">.</span><span class="token function">values</span><span class="token punctuation">(</span><span class="token punctuation">)</span><span class="token punctuation">.</span><span class="token function">forEach</span><span class="token punctuation">(</span>position <span class="token operator">-&gt;</span> <span class="token class-name">System</span><span class="token punctuation">.</span>out<span class="token punctuation">.</span><span class="token function">println</span><span class="token punctuation">(</span><span class="token string">&quot;Received information about a position: &quot;</span> <span class="token operator">+</span> position<span class="token punctuation">)</span><span class="token punctuation">)</span><span class="token punctuation">;</span>
    <span class="token punctuation">}</span>

    <span class="token annotation punctuation">@Override</span>
    <span class="token keyword">public</span> <span class="token keyword">void</span> <span class="token function">onPositionsStatusUpdates</span><span class="token punctuation">(</span><span class="token keyword">final</span> <span class="token class-name">Map</span><span class="token generics"><span class="token punctuation">&lt;</span><span class="token class-name">Long</span><span class="token punctuation">,</span> <span class="token class-name">PositionDTO</span><span class="token punctuation">&gt;</span></span> positions<span class="token punctuation">)</span> <span class="token punctuation">{</span>
        <span class="token comment">// Here, we will receive a PositionDTO each time a position status has changed.</span>
        positions<span class="token punctuation">.</span><span class="token function">values</span><span class="token punctuation">(</span><span class="token punctuation">)</span><span class="token punctuation">.</span><span class="token function">forEach</span><span class="token punctuation">(</span>position <span class="token operator">-&gt;</span> <span class="token class-name">System</span><span class="token punctuation">.</span>out<span class="token punctuation">.</span><span class="token function">println</span><span class="token punctuation">(</span><span class="token string">&quot;Received information about a position status: &quot;</span> <span class="token operator">+</span> position<span class="token punctuation">)</span><span class="token punctuation">)</span><span class="token punctuation">;</span>
    <span class="token punctuation">}</span>

<span class="token punctuation">}</span>
</code></pre><div class="line-numbers" aria-hidden="true"><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div></div></div>`,3),r=n("Une strategy Cassandre doit \xEAtre annot\xE9e avec l'annotation "),k={href:"https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/CassandreStrategy.html",target:"_blank",rel:"noopener noreferrer"},d=n("@CassandreStrategy"),m=n(" et elle doit h\xE9riter de "),v={href:"https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/BasicCassandreStrategy.html",target:"_blank",rel:"noopener noreferrer"},g=n("BasicCassandreStrategy"),h=n(" ."),b=s("p",null,"Voici comment cela marche :",-1),y=n("Dans la m\xE9thode "),_={href:"https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/CassandreStrategyInterface.html#getRequestedCurrencyPairs%28%29",target:"_blank",rel:"noopener noreferrer"},f=n("getRequestedCurrencyPairs()"),w=n(" , vous devez retourner la liste des paires de devises que votre strat\xE9gie veut recevoir de l'exchange."),S=n("Sur l'exchange que vous souhaitez utiliser, vous avez certainement plusieurs comptes et Cassandre doit savoir lequel vous utilisez pour le trading. Pour aider Cassandre \xE0 le d\xE9terminer, vous devez impl\xE9menter la m\xE9thode "),T={href:"https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/CassandreStrategyInterface.html#getTradeAccount%28java.util.Set%29",target:"_blank",rel:"noopener noreferrer"},O=n("getTradeAccount()"),C=n(" . Vous recevez en param\xE8tre la liste de vos comptes pr\xE9sents sur l'exchange et vous devez retourner celui utilis\xE9 par le trading."),D=n("S'il y a un changement sur l'un de vos comptes, la m\xE9thode "),q={href:"https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#onAccountsUpdates(java.util.Map)",target:"_blank",rel:"noopener noreferrer"},j=n("onAccountsUpdates()"),x=n(" sera appel\xE9e."),U=n("S'il y a un nouveau ticker, la m\xE9thode "),P={href:"https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#onTickersUpdates(java.util.Map)",target:"_blank",rel:"noopener noreferrer"},M=n("onTickersUpdates()"),z=n(" sera appel\xE9e."),B=n("S'il y a un changement sur l'un de vos ordres, la m\xE9thode "),A={href:"https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#onOrdersUpdates(java.util.Map)",target:"_blank",rel:"noopener noreferrer"},E=n("onOrdersUpdates()"),R=n(" sera appel\xE9e."),G=n("S'il y a un changement sur l'un de vos trades, la m\xE9thode "),I={href:"https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#onTradesUpdates(java.util.Map)",target:"_blank",rel:"noopener noreferrer"},N=n("onTradesUpdates()"),H=n(" sera appel\xE9e."),V=n("S'il y a un changement sur l'une de vos positions, la m\xE9thode "),L={href:"https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#onPositionsUpdates(java.util.Map)",target:"_blank",rel:"noopener noreferrer"},F=n("onPositionsUpdates()"),Q=n(" sera appel\xE9e."),W=n("S'il y a un changement du statut de l'une de vos positions, la m\xE9thode "),J={href:"https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#onPositionsStatusUpdates(java.util.Map)",target:"_blank",rel:"noopener noreferrer"},K=n("onPositionsStatusUpdates()"),X=n(" sera appel\xE9e.");function Y(Z,$){const a=o("ExternalLinkIcon");return p(),c("div",null,[u,s("p",null,[r,s("a",k,[d,t(a)]),m,s("a",v,[g,t(a)]),h]),b,s("ul",null,[s("li",null,[y,s("a",_,[f,t(a)]),w]),s("li",null,[S,s("a",T,[O,t(a)]),C]),s("li",null,[D,s("a",q,[j,t(a)]),x]),s("li",null,[U,s("a",P,[M,t(a)]),z]),s("li",null,[B,s("a",A,[E,t(a)]),R]),s("li",null,[G,s("a",I,[N,t(a)]),H]),s("li",null,[V,s("a",L,[F,t(a)]),Q]),s("li",null,[W,s("a",J,[K,t(a)]),X])])])}var sn=e(l,[["render",Y],["__file","explore-generated-code.html.vue"]]);export{sn as default};
