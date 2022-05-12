import{_ as c,r as s,o as l,c as p,a,w as t,F as i,b as e,d as n}from"./app.c1425658.js";const m={},u=e("h3",{id:"c-est-tres-simple",tabindex:"-1"},[e("a",{class:"header-anchor",href:"#c-est-tres-simple","aria-hidden":"true"},"#"),n(" C'est tr\xE8s simple!")],-1),b=e("div",{class:"language-bash ext-sh line-numbers-mode"},[e("pre",{class:"language-bash"},[e("code",null,[e("span",{class:"token comment"},"# Cr\xE9e simplement votre projet gr\xE2ce \xE0 notre archetype Maven"),n(`
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
`)])]),e("div",{class:"line-numbers","aria-hidden":"true"},[e("span",{class:"line-number"},"1"),e("br"),e("span",{class:"line-number"},"2"),e("br"),e("span",{class:"line-number"},"3"),e("br"),e("span",{class:"line-number"},"4"),e("br"),e("span",{class:"line-number"},"5"),e("br"),e("span",{class:"line-number"},"6"),e("br"),e("span",{class:"line-number"},"7"),e("br"),e("span",{class:"line-number"},"8"),e("br"),e("span",{class:"line-number"},"9"),e("br"),e("span",{class:"line-number"},"10"),e("br"),e("span",{class:"line-number"},"11"),e("br"),e("span",{class:"line-number"},"12"),e("br")])],-1);function d(h,k){const r=s("CodeGroupItem"),o=s("CodeGroup");return l(),p(i,null,[u,a(o,null,{default:t(()=>[a(r,{title:"Command line",active:""},{default:t(()=>[b]),_:1})]),_:1})],64)}var f=c(m,[["render",d],["__file","index.html.vue"]]);export{f as default};
