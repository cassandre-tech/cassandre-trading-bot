import{_ as o,r as i,o as r,c,b as n,a as t,d as e,e as a}from"./app.48b08d1c.js";const d={},l=n("h1",{id:"gestion-des-positions",tabindex:"-1"},[n("a",{class:"header-anchor",href:"#gestion-des-positions","aria-hidden":"true"},"#"),e(" Gestion des positions")],-1),p=n("p",null,"Cassandre fourni un moyen de g\xE9rer facilement et automatiquement des positions.",-1),u=n("h2",{id:"position-longue",tabindex:"-1"},[n("a",{class:"header-anchor",href:"#position-longue","aria-hidden":"true"},"#"),e(" Position longue")],-1),h=e("Dans votre strat\xE9gie, vous pouvez cr\xE9er une position longue avec la m\xE9thode "),g={href:"https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#createLongPosition%28tech.cassandre.trading.bot.dto.util.CurrencyPairDTO,java.math.BigDecimal,tech.cassandre.trading.bot.dto.position.PositionRulesDTO%29",target:"_blank",rel:"noopener noreferrer"},_=e("createLongPosition()"),b=n("p",null,"Cette m\xE9thode a trois param\xE8tres :",-1),v=n("ul",null,[n("li",null,[e("La paire de devise (Par exemple : "),n("code",null,"ETH/USDT"),e(").")]),n("li",null,[e("Le montant (par exemple : "),n("code",null,"0.5"),e(").")]),n("li",null,[e("Les r\xE8gles (par exemple : "),n("code",null,"100% de gain ou 50% de perte"),e(").")])],-1),m=e("Voici comment cr\xE9er les r\xE8gles d'une position avec l' objet "),k={href:"https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/dto/position/PositionRulesDTO.html",target:"_blank",rel:"noopener noreferrer"},f=e("PositionRulesDTO"),w=e(" :"),T=a(`<div class="language-java ext-java line-numbers-mode"><pre class="language-java"><code><span class="token class-name">PositionRulesDTO</span> rules<span class="token operator">=</span><span class="token class-name">PositionRulesDTO</span><span class="token punctuation">.</span><span class="token function">builder</span><span class="token punctuation">(</span><span class="token punctuation">)</span>
        <span class="token punctuation">.</span><span class="token function">stopGainPercentage</span><span class="token punctuation">(</span><span class="token number">100</span><span class="token punctuation">)</span>
        <span class="token punctuation">.</span><span class="token function">stopLossPercentage</span><span class="token punctuation">(</span><span class="token number">50</span><span class="token punctuation">)</span>
        <span class="token punctuation">.</span><span class="token function">build</span><span class="token punctuation">(</span><span class="token punctuation">)</span><span class="token punctuation">;</span>
</code></pre><div class="line-numbers" aria-hidden="true"><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div></div></div><p>Vous pouvez ensuite cr\xE9er la position de cette fa\xE7on:</p><div class="language-java ext-java line-numbers-mode"><pre class="language-java"><code><span class="token function">createLongPosition</span><span class="token punctuation">(</span><span class="token keyword">new</span> <span class="token class-name">CurrencyPairDTO</span><span class="token punctuation">(</span>ETH<span class="token punctuation">,</span>BTC<span class="token punctuation">)</span><span class="token punctuation">,</span>
        <span class="token keyword">new</span> <span class="token class-name">BigDecimal</span><span class="token punctuation">(</span><span class="token string">&quot;0.5&quot;</span><span class="token punctuation">)</span><span class="token punctuation">,</span>
        rules<span class="token punctuation">)</span><span class="token punctuation">;</span>
</code></pre><div class="line-numbers" aria-hidden="true"><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div></div></div>`,3),D=e("\xC0 ce moment, Cassandre va cr\xE9er un ordre d'achat de 0,5 ETH qui nous co\xFBtera 750 USDT (1 ETH co\xFBtant 1 500 USDT). Le statut de la position sera alors "),P={href:"https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/dto/position/PositionStatusDTO.html#OPENING",target:"_blank",rel:"noopener noreferrer"},O=e("OPENING"),j=e(" , et lorsque tous les trades correspondants \xE0 cet ordre seront arriv\xE9s, le statut passera \xE0 "),C={href:"https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/dto/position/PositionStatusDTO.html#OPENED",target:"_blank",rel:"noopener noreferrer"},S=e("OPENED"),x=e(" ."),L={class:"custom-container tip"},E=n("p",{class:"custom-container-title"},"TIP",-1),G=e("Si vous souhaitez v\xE9rifier que vous disposez de suffisamment de fonds (au moins 750 USDT dans notre cas) avant de cr\xE9er la position, vous pouvez utiliser la m\xE9thode "),q={href:"https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#canBuy%28tech.cassandre.trading.bot.dto.user.AccountDTO,tech.cassandre.trading.bot.dto.util.CurrencyPairDTO,java.math.BigDecimal%29",target:"_blank",rel:"noopener noreferrer"},y=e("canBuy()"),H=e(" ."),N=n("p",null,"D\xE9sormais, pour chaque ticker re\xE7u, Cassandre calculera le gain de la position. Si ce gain correspond une des r\xE8gles que l'on a fix\xE9es, elle sera automatiquement cl\xF4tur\xE9e.",-1),B=e(`Par exemple, si nous recevons un nouveau prix de 3 000 USDT pour 1 ETH, Cassandre calculera que si nous vendons notre position maintenant (ce que l'on appelle "fermer la position"), nous obtiendrons 1 500 USDT, soit un gain de 100%. Cassandre va donc cr\xE9er automatiquement un ordre de vente de nos 0,5 ETH. Le statut de la position passera \xE0 `),z={href:"https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/dto/position/PositionStatusDTO.html#CLOSING",target:"_blank",rel:"noopener noreferrer"},U=e("CLOSING"),I=e(" , et lorsque tous les trades correspondants seront arriv\xE9s, le statut passera \xE0 "),V={href:"https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/dto/position/PositionStatusDTO.html#CLOSED",target:"_blank",rel:"noopener noreferrer"},R=e("CLOSED"),A=e(" ."),M=e("Vous pourrez alors conna\xEEtre votre gain exact sur cette position en appelant la m\xE9thode "),F={href:"https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/dto/position/PositionDTO.html#getGain()",target:"_blank",rel:"noopener noreferrer"},J=e("getGain()"),K=e(" ."),Q=a(`<h2 id="position-courte" tabindex="-1"><a class="header-anchor" href="#position-courte" aria-hidden="true">#</a> Position courte</h2><p>Une position courte est l&#39;inverse d&#39;une position longue. Avec une position courte, vous pariez sur le fait que le prix va baisser.</p><p>Disons que vous cr\xE9ez une position courte sur 1 ETH avec ce code :</p><div class="language-java ext-java line-numbers-mode"><pre class="language-java"><code><span class="token function">createShortPosition</span><span class="token punctuation">(</span><span class="token keyword">new</span> <span class="token class-name">CurrencyPairDTO</span><span class="token punctuation">(</span>ETH<span class="token punctuation">,</span>BTC<span class="token punctuation">)</span><span class="token punctuation">,</span>
        <span class="token keyword">new</span> <span class="token class-name">BigDecimal</span><span class="token punctuation">(</span><span class="token string">&quot;1&quot;</span><span class="token punctuation">)</span><span class="token punctuation">,</span>
        rules<span class="token punctuation">)</span><span class="token punctuation">;</span>
</code></pre><div class="line-numbers" aria-hidden="true"><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div></div></div><p>Cassandre vendra 1 ETH, obtiendra 1 500 USDT et attendra que le prix baisse suffisamment pour acheter 2 ETH avec 1 500 USDT.</p><h2 id="gains" tabindex="-1"><a class="header-anchor" href="#gains" aria-hidden="true">#</a> Gains</h2>`,6),W=e("Sur l'objet "),X={href:"https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/dto/position/PositionDTO.html",target:"_blank",rel:"noopener noreferrer"},Y=e("PositionDTO"),Z=e(" , vous pouvez obtenir :"),$=e("Le gain calcul\xE9 le plus bas avec "),nn={href:"https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/dto/position/PositionDTO.html#getLowestCalculatedGain()",target:"_blank",rel:"noopener noreferrer"},en=e("getLowestCalculatedGain()"),sn=e("Le gain calcul\xE9 le plus \xE9lev\xE9 avec "),tn={href:"https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/dto/position/PositionDTO.html#getHighestCalculatedGain()",target:"_blank",rel:"noopener noreferrer"},an=e("getHighestCalculatedGain()"),on=e("Le dernier gain calcul\xE9 avec "),rn={href:"https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/dto/position/PositionDTO.html#getLatestCalculatedGain()",target:"_blank",rel:"noopener noreferrer"},cn=e("getLatestCalculatedGain()"),dn=e("Sur une position ferm\xE9e, vous pouvez obtenir le gain et les frais associ\xE9s avec la m\xE9thode "),ln={href:"https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/dto/position/PositionDTO.html#getGain()",target:"_blank",rel:"noopener noreferrer"},pn=e("getGain()"),un=e(" .");function hn(gn,_n){const s=i("ExternalLinkIcon");return r(),c("div",null,[l,p,u,n("p",null,[h,n("a",g,[_,t(s)])]),b,v,n("p",null,[m,n("a",k,[f,t(s)]),w]),T,n("p",null,[D,n("a",P,[O,t(s)]),j,n("a",C,[S,t(s)]),x]),n("div",L,[E,n("p",null,[G,n("a",q,[y,t(s)]),H])]),N,n("p",null,[B,n("a",z,[U,t(s)]),I,n("a",V,[R,t(s)]),A]),n("p",null,[M,n("a",F,[J,t(s)]),K]),Q,n("p",null,[W,n("a",X,[Y,t(s)]),Z]),n("ul",null,[n("li",null,[$,n("a",nn,[en,t(s)])]),n("li",null,[sn,n("a",tn,[an,t(s)])]),n("li",null,[on,n("a",rn,[cn,t(s)])])]),n("p",null,[dn,n("a",ln,[pn,t(s)]),un])])}var vn=o(d,[["render",hn],["__file","strategy-positions-management.html.vue"]]);export{vn as default};