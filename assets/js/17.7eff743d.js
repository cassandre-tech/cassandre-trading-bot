(window["webpackJsonp"] = window["webpackJsonp"] || []).push([[17],{

/***/ "../../../../.config/yarn/global/node_modules/cache-loader/dist/cjs.js?{\"cacheDirectory\":\"../../../../.config/yarn/global/node_modules/@vuepress/core/node_modules/.cache/vuepress\",\"cacheIdentifier\":\"4b61893c-vue-loader-template\"}!../../../../.config/yarn/global/node_modules/vue-loader/lib/loaders/templateLoader.js?!../../../../.config/yarn/global/node_modules/cache-loader/dist/cjs.js?!../../../../.config/yarn/global/node_modules/vue-loader/lib/index.js?!../../../../.config/yarn/global/node_modules/@vuepress/markdown-loader/index.js?!./src/learn/technical-analysis/create-the-project.md?vue&type=template&id=2e786ef2&":
/*!**************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************!*\
  !*** /home/runner/.config/yarn/global/node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"../../../../.config/yarn/global/node_modules/@vuepress/core/node_modules/.cache/vuepress","cacheIdentifier":"4b61893c-vue-loader-template"}!/home/runner/.config/yarn/global/node_modules/vue-loader/lib/loaders/templateLoader.js??vue-loader-options!/home/runner/.config/yarn/global/node_modules/cache-loader/dist/cjs.js??ref--1-0!/home/runner/.config/yarn/global/node_modules/vue-loader/lib??ref--1-1!/home/runner/.config/yarn/global/node_modules/@vuepress/markdown-loader??ref--1-2!./src/learn/technical-analysis/create-the-project.md?vue&type=template&id=2e786ef2& ***!
  \**************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************/
/*! exports provided: render, staticRenderFns */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "render", function() { return render; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "staticRenderFns", function() { return staticRenderFns; });
var render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('ContentSlotsDistributor',{attrs:{"slot-key":_vm.$parent.slotKey}},[_c('h1',{attrs:{"id":"create-the-project"}},[_c('a',{staticClass:"header-anchor",attrs:{"href":"#create-the-project"}},[_vm._v("#")]),_vm._v(" Create the project")]),_vm._v(" "),_c('h2',{attrs:{"id":"introduction"}},[_c('a',{staticClass:"header-anchor",attrs:{"href":"#introduction"}},[_vm._v("#")]),_vm._v(" Introduction")]),_vm._v(" "),_c('p',[_vm._v("We are going to use "),_c('a',{attrs:{"href":"https://ta4j.github.io/ta4j-wiki/","target":"_blank","rel":"noopener noreferrer"}},[_vm._v("ta4j"),_c('OutboundLink')],1),_vm._v(", an open-source Java library for technical analysis. It provides the basic components for the creation, evaluation, and execution of trading strategies.")]),_vm._v(" "),_c('h2',{attrs:{"id":"create-your-project"}},[_c('a',{staticClass:"header-anchor",attrs:{"href":"#create-your-project"}},[_vm._v("#")]),_vm._v(" Create your project")]),_vm._v(" "),_c('p',[_vm._v("Type this command :")]),_vm._v(" "),_c('div',{staticClass:"language-bash extra-class"},[_c('pre',{pre:true,attrs:{"class":"language-bash"}},[_c('code',[_vm._v("mvn -B archetype:generate -DarchetypeGroupId"),_c('span',{pre:true,attrs:{"class":"token operator"}},[_vm._v("=")]),_vm._v("tech.cassandre.trading.bot "),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v("\\")]),_vm._v("\n-DarchetypeArtifactId"),_c('span',{pre:true,attrs:{"class":"token operator"}},[_vm._v("=")]),_vm._v("cassandre-trading-bot-spring-boot-starter-basic-ta4j-archetype "),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v("\\")]),_vm._v("\n-DarchetypeVersion"),_c('span',{pre:true,attrs:{"class":"token operator"}},[_vm._v("=")]),_c('span',{pre:true,attrs:{"class":"token number"}},[_vm._v("4.1")]),_vm._v(".0 "),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v("\\")]),_vm._v("\n-DgroupId"),_c('span',{pre:true,attrs:{"class":"token operator"}},[_vm._v("=")]),_vm._v("com.example "),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v("\\")]),_vm._v("\n-DartifactId"),_c('span',{pre:true,attrs:{"class":"token operator"}},[_vm._v("=")]),_vm._v("ta4j-strategy "),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v("\\")]),_vm._v("\n-Dversion"),_c('span',{pre:true,attrs:{"class":"token operator"}},[_vm._v("=")]),_c('span',{pre:true,attrs:{"class":"token number"}},[_vm._v("1.0")]),_vm._v("-SNAPSHOT "),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v("\\")]),_vm._v("\n-Dpackage"),_c('span',{pre:true,attrs:{"class":"token operator"}},[_vm._v("=")]),_vm._v("com.example\n")])])]),_c('p',[_c('a',{attrs:{"href":"https://search.maven.org/search?q=g:%22tech.cassandre.trading.bot%22%20AND%20a:%22cassandre-trading-bot-spring-boot-starter%22","target":"_blank","rel":"noopener noreferrer"}},[_c('img',{attrs:{"src":"https://img.shields.io/maven-central/v/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-starter.svg?label=Maven%20Central","alt":"Maven Central"}}),_c('OutboundLink')],1)]),_vm._v(" "),_c('p',[_vm._v("The created project is a spring boot project with the following structure :")]),_vm._v(" "),_c('div',{staticClass:"language- extra-class"},[_c('pre',{pre:true,attrs:{"class":"language-text"}},[_c('code',[_vm._v("ta4j-strategy/\n├── pom.xml\n└── src\n    ├── main\n    │   ├── java\n    │   │   └── com\n    │   │       └── example\n    │   │           ├── Application.java\n    │   │           ├── package-info.java\n    │   │           └── SimpleTa4jStrategy.java\n    │   └── resources\n    │       ├── application.properties\n    │       ├── user-main.tsv\n    │       └── user-trade.tsv\n    └── test\n        ├── java\n        │   └── com\n        │       └── example\n        │           └── SimpleTa4jStrategyTest.java\n        └── resources\n            ├── application.properties\n            ├── tickers-btc-usdt.tsv\n            ├── user-main.tsv\n            └── user-trade.tsv\n")])])])])}
var staticRenderFns = []



/***/ }),

/***/ "./src/learn/technical-analysis/create-the-project.md":
/*!************************************************************!*\
  !*** ./src/learn/technical-analysis/create-the-project.md ***!
  \************************************************************/
/*! exports provided: default */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _create_the_project_md_vue_type_template_id_2e786ef2___WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./create-the-project.md?vue&type=template&id=2e786ef2& */ "./src/learn/technical-analysis/create-the-project.md?vue&type=template&id=2e786ef2&");
/* harmony import */ var _config_yarn_global_node_modules_vue_loader_lib_runtime_componentNormalizer_js__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ../../../../../../../.config/yarn/global/node_modules/vue-loader/lib/runtime/componentNormalizer.js */ "../../../../.config/yarn/global/node_modules/vue-loader/lib/runtime/componentNormalizer.js");

var script = {}


/* normalize component */

var component = Object(_config_yarn_global_node_modules_vue_loader_lib_runtime_componentNormalizer_js__WEBPACK_IMPORTED_MODULE_1__["default"])(
  script,
  _create_the_project_md_vue_type_template_id_2e786ef2___WEBPACK_IMPORTED_MODULE_0__["render"],
  _create_the_project_md_vue_type_template_id_2e786ef2___WEBPACK_IMPORTED_MODULE_0__["staticRenderFns"],
  false,
  null,
  null,
  null
  
)

/* harmony default export */ __webpack_exports__["default"] = (component.exports);

/***/ }),

/***/ "./src/learn/technical-analysis/create-the-project.md?vue&type=template&id=2e786ef2&":
/*!*******************************************************************************************!*\
  !*** ./src/learn/technical-analysis/create-the-project.md?vue&type=template&id=2e786ef2& ***!
  \*******************************************************************************************/
/*! exports provided: render, staticRenderFns */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _config_yarn_global_node_modules_cache_loader_dist_cjs_js_cacheDirectory_config_yarn_global_node_modules_vuepress_core_node_modules_cache_vuepress_cacheIdentifier_4b61893c_vue_loader_template_config_yarn_global_node_modules_vue_loader_lib_loaders_templateLoader_js_vue_loader_options_config_yarn_global_node_modules_cache_loader_dist_cjs_js_ref_1_0_config_yarn_global_node_modules_vue_loader_lib_index_js_ref_1_1_config_yarn_global_node_modules_vuepress_markdown_loader_index_js_ref_1_2_create_the_project_md_vue_type_template_id_2e786ef2___WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! -!../../../../../../../.config/yarn/global/node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"../../../../.config/yarn/global/node_modules/@vuepress/core/node_modules/.cache/vuepress","cacheIdentifier":"4b61893c-vue-loader-template"}!../../../../../../../.config/yarn/global/node_modules/vue-loader/lib/loaders/templateLoader.js??vue-loader-options!../../../../../../../.config/yarn/global/node_modules/cache-loader/dist/cjs.js??ref--1-0!../../../../../../../.config/yarn/global/node_modules/vue-loader/lib??ref--1-1!../../../../../../../.config/yarn/global/node_modules/@vuepress/markdown-loader??ref--1-2!./create-the-project.md?vue&type=template&id=2e786ef2& */ "../../../../.config/yarn/global/node_modules/cache-loader/dist/cjs.js?{\"cacheDirectory\":\"../../../../.config/yarn/global/node_modules/@vuepress/core/node_modules/.cache/vuepress\",\"cacheIdentifier\":\"4b61893c-vue-loader-template\"}!../../../../.config/yarn/global/node_modules/vue-loader/lib/loaders/templateLoader.js?!../../../../.config/yarn/global/node_modules/cache-loader/dist/cjs.js?!../../../../.config/yarn/global/node_modules/vue-loader/lib/index.js?!../../../../.config/yarn/global/node_modules/@vuepress/markdown-loader/index.js?!./src/learn/technical-analysis/create-the-project.md?vue&type=template&id=2e786ef2&");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "render", function() { return _config_yarn_global_node_modules_cache_loader_dist_cjs_js_cacheDirectory_config_yarn_global_node_modules_vuepress_core_node_modules_cache_vuepress_cacheIdentifier_4b61893c_vue_loader_template_config_yarn_global_node_modules_vue_loader_lib_loaders_templateLoader_js_vue_loader_options_config_yarn_global_node_modules_cache_loader_dist_cjs_js_ref_1_0_config_yarn_global_node_modules_vue_loader_lib_index_js_ref_1_1_config_yarn_global_node_modules_vuepress_markdown_loader_index_js_ref_1_2_create_the_project_md_vue_type_template_id_2e786ef2___WEBPACK_IMPORTED_MODULE_0__["render"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "staticRenderFns", function() { return _config_yarn_global_node_modules_cache_loader_dist_cjs_js_cacheDirectory_config_yarn_global_node_modules_vuepress_core_node_modules_cache_vuepress_cacheIdentifier_4b61893c_vue_loader_template_config_yarn_global_node_modules_vue_loader_lib_loaders_templateLoader_js_vue_loader_options_config_yarn_global_node_modules_cache_loader_dist_cjs_js_ref_1_0_config_yarn_global_node_modules_vue_loader_lib_index_js_ref_1_1_config_yarn_global_node_modules_vuepress_markdown_loader_index_js_ref_1_2_create_the_project_md_vue_type_template_id_2e786ef2___WEBPACK_IMPORTED_MODULE_0__["staticRenderFns"]; });



/***/ })

}]);
//# sourceMappingURL=17.7eff743d.js.map