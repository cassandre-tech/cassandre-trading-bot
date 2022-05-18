import{_ as c,r as s,o as l,c as i,a,w as t,F as p,b as e,d as n}from"./app.d246f12a.js";const m={},u=e("h3",{id:"c-est-tres-simple",tabindex:"-1"},[e("a",{class:"header-anchor",href:"#c-est-tres-simple","aria-hidden":"true"},"#"),n(" C'est tr\xE8s simple!")],-1),d=e("div",{class:"language-bash ext-sh line-numbers-mode"},[e("pre",{class:"language-bash"},[e("code",null,[e("span",{class:"token comment"},"# Cr\xE9e simplement votre projet gr\xE2ce \xE0 notre archetype Maven"),n(`
mvn archetype:generate `),e("span",{class:"token punctuation"},"\\"),n(`
-DarchetypeGroupId`),e("span",{class:"token operator"},"="),n("tech.cassandre.trading.bot "),e("span",{class:"token punctuation"},"\\"),n(`
-DarchetypeArtifactId`),e("span",{class:"token operator"},"="),n("cassandre-trading-bot-spring-boot-starter-basic-archetype "),e("span",{class:"token punctuation"},"\\"),n(`
-DarchetypeVersion`),e("span",{class:"token operator"},"="),e("span",{class:"token number"},"5.0"),n(".9-SNAPSHOT "),e("span",{class:"token punctuation"},"\\"),n(`
-DgroupId`),e("span",{class:"token operator"},"="),n("com.example "),e("span",{class:"token punctuation"},"\\"),n(`
-DartifactId`),e("span",{class:"token operator"},"="),n("my-bot "),e("span",{class:"token punctuation"},"\\"),n(`
-Dversion`),e("span",{class:"token operator"},"="),e("span",{class:"token number"},"1.0"),n("-SNAPSHOT "),e("span",{class:"token punctuation"},"\\"),n(`
-Dpackage`),e("span",{class:"token operator"},"="),n(`com.example

`),e("span",{class:"token comment"},"# Lancez les tests unitaires"),n(`
mvn -f my-bot/pom.xml `),e("span",{class:"token builtin class-name"},"test"),n(`
`)])]),e("div",{class:"line-numbers","aria-hidden":"true"},[e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"}),e("div",{class:"line-number"})])],-1);function b(v,h){const o=s("CodeGroupItem"),r=s("CodeGroup");return l(),i(p,null,[u,a(r,null,{default:t(()=>[a(o,{title:"Command line",active:""},{default:t(()=>[d]),_:1})]),_:1})],64)}var _=c(m,[["render",b],["__file","index.html.vue"]]);export{_ as default};
