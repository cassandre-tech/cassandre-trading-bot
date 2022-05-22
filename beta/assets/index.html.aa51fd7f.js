import{_ as r,r as a,o as l,c as i,a as s,w as t,F as p,b as e,d as n}from"./app.6769468a.js";const u={},d=e("h3",{id:"as-easy-as-1-2-3",tabindex:"-1"},[e("a",{class:"header-anchor",href:"#as-easy-as-1-2-3","aria-hidden":"true"},"#"),n(" As Easy as 1, 2, 3")],-1),m=e("div",{class:"language-bash ext-sh line-numbers-mode"},[e("pre",{class:"language-bash"},[e("code",null,[e("span",{class:"token comment"},"# Create a simple project with our archetype"),n(`
mvn archetype:generate `),e("span",{class:"token punctuation"},"\\"),n(`
-DarchetypeGroupId`),e("span",{class:"token operator"},"="),n("tech.cassandre.trading.bot "),e("span",{class:"token punctuation"},"\\"),n(`
-DarchetypeArtifactId`),e("span",{class:"token operator"},"="),n("cassandre-trading-bot-spring-boot-starter-basic-archetype "),e("span",{class:"token punctuation"},"\\"),n(`
-DarchetypeVersion`),e("span",{class:"token operator"},"="),e("span",{class:"token number"},"5.0"),n(".9-SNAPSHOT "),e("span",{class:"token punctuation"},"\\"),n(`
-DgroupId`),e("span",{class:"token operator"},"="),n("com.example "),e("span",{class:"token punctuation"},"\\"),n(`
-DartifactId`),e("span",{class:"token operator"},"="),n("my-bot "),e("span",{class:"token punctuation"},"\\"),n(`
-Dversion`),e("span",{class:"token operator"},"="),e("span",{class:"token number"},"1.0"),n("-SNAPSHOT "),e("span",{class:"token punctuation"},"\\"),n(`
-Dpackage`),e("span",{class:"token operator"},"="),n(`com.example

`),e("span",{class:"token comment"},"# Runs unit tests"),n(`
mvn -f my-bot/pom.xml `),e("span",{class:"token builtin class-name"},"test"),n(`
`)])]),e("div",{class:"line-numbers","aria-hidden":"true"},[e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"})])],-1);function b(h,k){const o=a("CodeGroupItem"),c=a("CodeGroup");return l(),i(p,null,[d,s(c,null,{default:t(()=>[s(o,{title:"Command line",active:""},{default:t(()=>[m]),_:1})]),_:1})],64)}var v=r(u,[["render",b],["__file","index.html.vue"]]);export{v as default};
