import{_ as c,r as a,o as l,c as p,a as s,w as t,F as i,b as e,d as n}from"./app.5775901d.js";const u={},m=e("h3",{id:"as-easy-as-1-2-3",tabindex:"-1"},[e("a",{class:"header-anchor",href:"#as-easy-as-1-2-3","aria-hidden":"true"},"#"),n(" As Easy as 1, 2, 3")],-1),b=e("div",{class:"language-bash ext-sh line-numbers-mode"},[e("pre",{class:"language-bash"},[e("code",null,[e("span",{class:"token comment"},"# Create a simple project with our archetype"),n(`
mvn archetype:generate `),e("span",{class:"token punctuation"},"\\"),n(`
-DarchetypeGroupId`),e("span",{class:"token operator"},"="),n("tech.cassandre.trading.bot "),e("span",{class:"token punctuation"},"\\"),n(`
-DarchetypeArtifactId`),e("span",{class:"token operator"},"="),n("cassandre-trading-bot-spring-boot-starter-basic-archetype "),e("span",{class:"token punctuation"},"\\"),n(`
-DarchetypeVersion`),e("span",{class:"token operator"},"="),e("span",{class:"token number"},"5.0"),n(".8 "),e("span",{class:"token punctuation"},"\\"),n(`
-DgroupId`),e("span",{class:"token operator"},"="),n("com.example "),e("span",{class:"token punctuation"},"\\"),n(`
-DartifactId`),e("span",{class:"token operator"},"="),n("my-bot "),e("span",{class:"token punctuation"},"\\"),n(`
-Dversion`),e("span",{class:"token operator"},"="),e("span",{class:"token number"},"1.0"),n("-SNAPSHOT "),e("span",{class:"token punctuation"},"\\"),n(`
-Dpackage`),e("span",{class:"token operator"},"="),n(`com.example

`),e("span",{class:"token comment"},"# Runs unit tests"),n(`
mvn -f my-bot/pom.xml `),e("span",{class:"token builtin class-name"},"test"),n(`
`)])]),e("div",{class:"line-numbers","aria-hidden":"true"},[e("span",{class:"line-number"},"1"),e("br"),e("span",{class:"line-number"},"2"),e("br"),e("span",{class:"line-number"},"3"),e("br"),e("span",{class:"line-number"},"4"),e("br"),e("span",{class:"line-number"},"5"),e("br"),e("span",{class:"line-number"},"6"),e("br"),e("span",{class:"line-number"},"7"),e("br"),e("span",{class:"line-number"},"8"),e("br"),e("span",{class:"line-number"},"9"),e("br"),e("span",{class:"line-number"},"10"),e("br"),e("span",{class:"line-number"},"11"),e("br"),e("span",{class:"line-number"},"12"),e("br")])],-1);function d(h,k){const o=a("CodeGroupItem"),r=a("CodeGroup");return l(),p(i,null,[m,s(r,null,{default:t(()=>[s(o,{title:"Command line",active:""},{default:t(()=>[b]),_:1})]),_:1})],64)}var f=c(u,[["render",d],["__file","index.html.vue"]]);export{f as default};
