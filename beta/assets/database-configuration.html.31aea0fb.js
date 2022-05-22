import{_ as a,e as s}from"./app.9376bde5.js";const n={},t=s(`<h1 id="database-configuration" tabindex="-1"><a class="header-anchor" href="#database-configuration" aria-hidden="true">#</a> Database configuration</h1><p>By default, Cassandre uses a non persistant hsqldb, if you want to run your strategies in production, you have to set a correct configuration.</p><p>For example, if you want to use PostgreSQL, you first have to add the JDBC driver to your <code>pom.xml</code>:</p><div class="language-xml ext-xml line-numbers-mode"><pre class="language-xml"><code><span class="token tag"><span class="token tag"><span class="token punctuation">&lt;</span>dependency</span><span class="token punctuation">&gt;</span></span>
    <span class="token tag"><span class="token tag"><span class="token punctuation">&lt;</span>groupId</span><span class="token punctuation">&gt;</span></span>org.postgresql<span class="token tag"><span class="token tag"><span class="token punctuation">&lt;/</span>groupId</span><span class="token punctuation">&gt;</span></span>
    <span class="token tag"><span class="token tag"><span class="token punctuation">&lt;</span>artifactId</span><span class="token punctuation">&gt;</span></span>postgresql<span class="token tag"><span class="token tag"><span class="token punctuation">&lt;/</span>artifactId</span><span class="token punctuation">&gt;</span></span>
    <span class="token tag"><span class="token tag"><span class="token punctuation">&lt;</span>version</span><span class="token punctuation">&gt;</span></span>42.3.3<span class="token tag"><span class="token tag"><span class="token punctuation">&lt;/</span>version</span><span class="token punctuation">&gt;</span></span>
<span class="token tag"><span class="token tag"><span class="token punctuation">&lt;/</span>dependency</span><span class="token punctuation">&gt;</span></span>
</code></pre><div class="line-numbers" aria-hidden="true"><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div></div></div><p>then, change the configuration in <code>src/main/resources/application.properties</code>:</p><div class="language-properties ext-properties line-numbers-mode"><pre class="language-properties"><code><span class="token key attr-name">spring.datasource.driver-class-name</span><span class="token punctuation">=</span><span class="token value attr-value">org.postgresql.Driver</span>
<span class="token key attr-name">spring.datasource.url</span><span class="token punctuation">=</span><span class="token value attr-value">jdbc:postgresql://cassandre-postgresql/cassandre_trading_bot_database</span>
<span class="token key attr-name">spring.datasource.username</span><span class="token punctuation">=</span><span class="token value attr-value">cassandre</span>
<span class="token key attr-name">spring.datasource.password</span><span class="token punctuation">=</span><span class="token value attr-value">XjeyL9876</span>
</code></pre><div class="line-numbers" aria-hidden="true"><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div></div></div>`,6);function e(p,o){return t}var i=a(n,[["render",e],["__file","database-configuration.html.vue"]]);export{i as default};
