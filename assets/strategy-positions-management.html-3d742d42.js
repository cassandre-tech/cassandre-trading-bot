import{_ as o,r as i,o as r,c,b as a,d as n,a as s,e as t}from"./app-bb6d68d1.js";const l={},d=a("h1",{id:"gestion-des-positions",tabindex:"-1"},[a("a",{class:"header-anchor",href:"#gestion-des-positions","aria-hidden":"true"},"#"),n(" Gestion des positions")],-1),p=a("p",null,"Cassandre fourni un moyen de gérer facilement et automatiquement des positions.",-1),u=a("h2",{id:"position-longue",tabindex:"-1"},[a("a",{class:"header-anchor",href:"#position-longue","aria-hidden":"true"},"#"),n(" Position longue")],-1),g={href:"https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#createLongPosition%28tech.cassandre.trading.bot.dto.util.CurrencyPairDTO,java.math.BigDecimal,tech.cassandre.trading.bot.dto.position.PositionRulesDTO%29",target:"_blank",rel:"noopener noreferrer"},h=a("p",null,"Cette méthode a trois paramètres :",-1),b=a("ul",null,[a("li",null,[n("La paire de devise (Par exemple : "),a("code",null,"ETH/USDT"),n(").")]),a("li",null,[n("Le montant (par exemple : "),a("code",null,"0.5"),n(").")]),a("li",null,[n("Les règles (par exemple : "),a("code",null,"100% de gain ou 50% de perte"),n(").")])],-1),v={href:"https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/dto/position/PositionRulesDTO.html",target:"_blank",rel:"noopener noreferrer"},m=t(`<div class="language-java line-numbers-mode" data-ext="java"><pre class="language-java"><code><span class="token class-name">PositionRulesDTO</span> rules<span class="token operator">=</span><span class="token class-name">PositionRulesDTO</span><span class="token punctuation">.</span><span class="token function">builder</span><span class="token punctuation">(</span><span class="token punctuation">)</span>
        <span class="token punctuation">.</span><span class="token function">stopGainPercentage</span><span class="token punctuation">(</span><span class="token number">100</span><span class="token punctuation">)</span>
        <span class="token punctuation">.</span><span class="token function">stopLossPercentage</span><span class="token punctuation">(</span><span class="token number">50</span><span class="token punctuation">)</span>
        <span class="token punctuation">.</span><span class="token function">build</span><span class="token punctuation">(</span><span class="token punctuation">)</span><span class="token punctuation">;</span>
</code></pre><div class="line-numbers" aria-hidden="true"><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div></div></div><p>Vous pouvez ensuite créer la position de cette façon:</p><div class="language-java line-numbers-mode" data-ext="java"><pre class="language-java"><code><span class="token function">createLongPosition</span><span class="token punctuation">(</span><span class="token keyword">new</span> <span class="token class-name">CurrencyPairDTO</span><span class="token punctuation">(</span><span class="token constant">ETH</span><span class="token punctuation">,</span><span class="token constant">BTC</span><span class="token punctuation">)</span><span class="token punctuation">,</span>
        <span class="token keyword">new</span> <span class="token class-name">BigDecimal</span><span class="token punctuation">(</span><span class="token string">&quot;0.5&quot;</span><span class="token punctuation">)</span><span class="token punctuation">,</span>
        rules<span class="token punctuation">)</span><span class="token punctuation">;</span>
</code></pre><div class="line-numbers" aria-hidden="true"><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div></div></div>`,3),k={href:"https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/dto/position/PositionStatusDTO.html#OPENING",target:"_blank",rel:"noopener noreferrer"},_={href:"https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/dto/position/PositionStatusDTO.html#OPENED",target:"_blank",rel:"noopener noreferrer"},f={class:"custom-container tip"},w=a("p",{class:"custom-container-title"},"TIP",-1),T={href:"https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#canBuy%28tech.cassandre.trading.bot.dto.user.AccountDTO,tech.cassandre.trading.bot.dto.util.CurrencyPairDTO,java.math.BigDecimal%29",target:"_blank",rel:"noopener noreferrer"},D=a("p",null,"Désormais, pour chaque ticker reçu, Cassandre calculera le gain de la position. Si ce gain correspond une des règles que l'on a fixées, elle sera automatiquement clôturée.",-1),P={href:"https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/dto/position/PositionStatusDTO.html#CLOSING",target:"_blank",rel:"noopener noreferrer"},O={href:"https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/dto/position/PositionStatusDTO.html#CLOSED",target:"_blank",rel:"noopener noreferrer"},j={href:"https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/dto/position/PositionDTO.html#getGain()",target:"_blank",rel:"noopener noreferrer"},C=t(`<h2 id="position-courte" tabindex="-1"><a class="header-anchor" href="#position-courte" aria-hidden="true">#</a> Position courte</h2><p>Une position courte est l&#39;inverse d&#39;une position longue. Avec une position courte, vous pariez sur le fait que le prix va baisser.</p><p>Disons que vous créez une position courte sur 1 ETH avec ce code :</p><div class="language-java line-numbers-mode" data-ext="java"><pre class="language-java"><code><span class="token function">createShortPosition</span><span class="token punctuation">(</span><span class="token keyword">new</span> <span class="token class-name">CurrencyPairDTO</span><span class="token punctuation">(</span><span class="token constant">ETH</span><span class="token punctuation">,</span><span class="token constant">BTC</span><span class="token punctuation">)</span><span class="token punctuation">,</span>
        <span class="token keyword">new</span> <span class="token class-name">BigDecimal</span><span class="token punctuation">(</span><span class="token string">&quot;1&quot;</span><span class="token punctuation">)</span><span class="token punctuation">,</span>
        rules<span class="token punctuation">)</span><span class="token punctuation">;</span>
</code></pre><div class="line-numbers" aria-hidden="true"><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div></div></div><p>Cassandre vendra 1 ETH, obtiendra 1 500 USDT et attendra que le prix baisse suffisamment pour acheter 2 ETH avec 1 500 USDT.</p><h2 id="gains" tabindex="-1"><a class="header-anchor" href="#gains" aria-hidden="true">#</a> Gains</h2>`,6),S={href:"https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/dto/position/PositionDTO.html",target:"_blank",rel:"noopener noreferrer"},x={href:"https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/dto/position/PositionDTO.html#getLowestCalculatedGain()",target:"_blank",rel:"noopener noreferrer"},L={href:"https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/dto/position/PositionDTO.html#getHighestCalculatedGain()",target:"_blank",rel:"noopener noreferrer"},E={href:"https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/dto/position/PositionDTO.html#getLatestCalculatedGain()",target:"_blank",rel:"noopener noreferrer"},G={href:"https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/dto/position/PositionDTO.html#getGain()",target:"_blank",rel:"noopener noreferrer"};function q(y,H){const e=i("ExternalLinkIcon");return r(),c("div",null,[d,p,u,a("p",null,[n("Dans votre stratégie, vous pouvez créer une position longue avec la méthode "),a("a",g,[n("createLongPosition()"),s(e)])]),h,b,a("p",null,[n("Voici comment créer les règles d'une position avec l' objet "),a("a",v,[n("PositionRulesDTO"),s(e)]),n(" :")]),m,a("p",null,[n("À ce moment, Cassandre va créer un ordre d'achat de 0,5 ETH qui nous coûtera 750 USDT (1 ETH coûtant 1 500 USDT). Le statut de la position sera alors "),a("a",k,[n("OPENING"),s(e)]),n(" , et lorsque tous les trades correspondants à cet ordre seront arrivés, le statut passera à "),a("a",_,[n("OPENED"),s(e)]),n(" .")]),a("div",f,[w,a("p",null,[n("Si vous souhaitez vérifier que vous disposez de suffisamment de fonds (au moins 750 USDT dans notre cas) avant de créer la position, vous pouvez utiliser la méthode "),a("a",T,[n("canBuy()"),s(e)]),n(" .")])]),D,a("p",null,[n(`Par exemple, si nous recevons un nouveau prix de 3 000 USDT pour 1 ETH, Cassandre calculera que si nous vendons notre position maintenant (ce que l'on appelle "fermer la position"), nous obtiendrons 1 500 USDT, soit un gain de 100%. Cassandre va donc créer automatiquement un ordre de vente de nos 0,5 ETH. Le statut de la position passera à `),a("a",P,[n("CLOSING"),s(e)]),n(" , et lorsque tous les trades correspondants seront arrivés, le statut passera à "),a("a",O,[n("CLOSED"),s(e)]),n(" .")]),a("p",null,[n("Vous pourrez alors connaître votre gain exact sur cette position en appelant la méthode "),a("a",j,[n("getGain()"),s(e)]),n(" .")]),C,a("p",null,[n("Sur l'objet "),a("a",S,[n("PositionDTO"),s(e)]),n(" , vous pouvez obtenir :")]),a("ul",null,[a("li",null,[n("Le gain calculé le plus bas avec "),a("a",x,[n("getLowestCalculatedGain()"),s(e)])]),a("li",null,[n("Le gain calculé le plus élevé avec "),a("a",L,[n("getHighestCalculatedGain()"),s(e)])]),a("li",null,[n("Le dernier gain calculé avec "),a("a",E,[n("getLatestCalculatedGain()"),s(e)])])]),a("p",null,[n("Sur une position fermée, vous pouvez obtenir le gain et les frais associés avec la méthode "),a("a",G,[n("getGain()"),s(e)]),n(" .")])])}const B=o(l,[["render",q],["__file","strategy-positions-management.html.vue"]]);export{B as default};