(window["webpackJsonp"] = window["webpackJsonp"] || []).push([[24],{

/***/ "./node_modules/cache-loader/dist/cjs.js?{\"cacheDirectory\":\"node_modules/@vuepress/core/node_modules/.cache/vuepress\",\"cacheIdentifier\":\"4b61893c-vue-loader-template\"}!./node_modules/vue-loader/lib/loaders/templateLoader.js?!./node_modules/cache-loader/dist/cjs.js?!./node_modules/vue-loader/lib/index.js?!./node_modules/@vuepress/markdown-loader/index.js?!./src/ressources/how-tos/how-to-create-a-release.md?vue&type=template&id=b8f75cc0&":
/*!******************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************!*\
  !*** ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/@vuepress/core/node_modules/.cache/vuepress","cacheIdentifier":"4b61893c-vue-loader-template"}!./node_modules/vue-loader/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ref--1-0!./node_modules/vue-loader/lib??ref--1-1!./node_modules/@vuepress/markdown-loader??ref--1-2!./src/ressources/how-tos/how-to-create-a-release.md?vue&type=template&id=b8f75cc0& ***!
  \******************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************/
/*! exports provided: render, staticRenderFns */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "render", function() { return render; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "staticRenderFns", function() { return staticRenderFns; });
var render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('ContentSlotsDistributor',{attrs:{"slot-key":_vm.$parent.slotKey}},[_c('h1',{attrs:{"id":"create-a-release"}},[_c('a',{staticClass:"header-anchor",attrs:{"href":"#create-a-release"}},[_vm._v("#")]),_vm._v(" Create a release")]),_vm._v(" "),_c('h2',{attrs:{"id":"prepare-the-release"}},[_c('a',{staticClass:"header-anchor",attrs:{"href":"#prepare-the-release"}},[_vm._v("#")]),_vm._v(" Prepare the release")]),_vm._v(" "),_c('ul',[_c('li',[_vm._v("Fix "),_c('a',{attrs:{"href":"https://app.codacy.com/gh/cassandre-tech/cassandre-trading-bot/issues","target":"_blank","rel":"noopener noreferrer"}},[_vm._v("Codacy"),_c('OutboundLink')],1),_vm._v(" & Intellij warnings.")]),_vm._v(" "),_c('li',[_vm._v("Update "),_c('a',{attrs:{"href":"https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/SECURITY.md","target":"_blank","rel":"noopener noreferrer"}},[_vm._v("security.md"),_c('OutboundLink')],1),_vm._v(".")]),_vm._v(" "),_c('li',[_vm._v("Update documentation\n"),_c('ul',[_c('li',[_vm._v("Change release number in "),_c('code',[_vm._v("learn/quickstart.md")]),_vm._v(".")]),_vm._v(" "),_c('li',[_vm._v("Change release number in "),_c('code',[_vm._v("learn/dry-mode-and-backtesting.md")]),_vm._v(".")]),_vm._v(" "),_c('li',[_vm._v("Change release number in "),_c('code',[_vm._v("learn/technical-analysis/create-the-project.md")]),_vm._v(".")]),_vm._v(" "),_c('li',[_vm._v("Change release number in "),_c('code',[_vm._v("learn/technical-analysis/backtest-your-trading-strategy.md")]),_vm._v(".")]),_vm._v(" "),_c('li',[_vm._v("Change roadmap content in "),_c('code',[_vm._v("why-cassandre/features-and-roadmap.md")]),_vm._v(".")])])])]),_vm._v(" "),_c('h2',{attrs:{"id":"create-the-release-with-maven"}},[_c('a',{staticClass:"header-anchor",attrs:{"href":"#create-the-release-with-maven"}},[_vm._v("#")]),_vm._v(" Create the release with Maven")]),_vm._v(" "),_c('p',[_vm._v("You must be using "),_c('code',[_vm._v("ssh")]),_vm._v(" and not "),_c('code',[_vm._v("https")]),_vm._v(", to switch to "),_c('code',[_vm._v("ssh")]),_vm._v(", type :")]),_vm._v(" "),_c('div',{staticClass:"language-bash extra-class"},[_c('pre',{pre:true,attrs:{"class":"language-bash"}},[_c('code',[_c('span',{pre:true,attrs:{"class":"token function"}},[_vm._v("git")]),_vm._v(" remote set-url origin git@github.com:cassandre-tech/cassandre-trading-bot.git\n")])])]),_c('p',[_vm._v("Start the release with :")]),_vm._v(" "),_c('div',{staticClass:"language-bash extra-class"},[_c('pre',{pre:true,attrs:{"class":"language-bash"}},[_c('code',[_vm._v("mvn gitflow:release-start\n")])])]),_c('p',[_vm._v("After choosing the release number, finish the release, push branches and tags, with this command :")]),_vm._v(" "),_c('div',{staticClass:"language-bash extra-class"},[_c('pre',{pre:true,attrs:{"class":"language-bash"}},[_c('code',[_vm._v("mvn gitflow:release-finish\n")])])]),_c('h2',{attrs:{"id":"update"}},[_c('a',{staticClass:"header-anchor",attrs:{"href":"#update"}},[_vm._v("#")]),_vm._v(" Update")]),_vm._v(" "),_c('ul',[_c('li',[_vm._v("Close the corresponding "),_c('a',{attrs:{"href":"https://github.com/cassandre-tech/cassandre-trading-bot/milestones","target":"_blank","rel":"noopener noreferrer"}},[_vm._v("milestone in Github"),_c('OutboundLink')],1),_vm._v(".")]),_vm._v(" "),_c('li',[_vm._v("Write and post a "),_c('a',{attrs:{"href":"https://cassandre.substack.com/publish?utm_source=menu","target":"_blank","rel":"noopener noreferrer"}},[_vm._v("substack post"),_c('OutboundLink')],1),_vm._v(".")])])])}
var staticRenderFns = []



/***/ }),

/***/ "./src/ressources/how-tos/how-to-create-a-release.md":
/*!***********************************************************!*\
  !*** ./src/ressources/how-tos/how-to-create-a-release.md ***!
  \***********************************************************/
/*! exports provided: default */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _how_to_create_a_release_md_vue_type_template_id_b8f75cc0___WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./how-to-create-a-release.md?vue&type=template&id=b8f75cc0& */ "./src/ressources/how-tos/how-to-create-a-release.md?vue&type=template&id=b8f75cc0&");
/* harmony import */ var _node_modules_vue_loader_lib_runtime_componentNormalizer_js__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ../../../node_modules/vue-loader/lib/runtime/componentNormalizer.js */ "./node_modules/vue-loader/lib/runtime/componentNormalizer.js");

var script = {}


/* normalize component */

var component = Object(_node_modules_vue_loader_lib_runtime_componentNormalizer_js__WEBPACK_IMPORTED_MODULE_1__["default"])(
  script,
  _how_to_create_a_release_md_vue_type_template_id_b8f75cc0___WEBPACK_IMPORTED_MODULE_0__["render"],
  _how_to_create_a_release_md_vue_type_template_id_b8f75cc0___WEBPACK_IMPORTED_MODULE_0__["staticRenderFns"],
  false,
  null,
  null,
  null
  
)

/* harmony default export */ __webpack_exports__["default"] = (component.exports);

/***/ }),

/***/ "./src/ressources/how-tos/how-to-create-a-release.md?vue&type=template&id=b8f75cc0&":
/*!******************************************************************************************!*\
  !*** ./src/ressources/how-tos/how-to-create-a-release.md?vue&type=template&id=b8f75cc0& ***!
  \******************************************************************************************/
/*! exports provided: render, staticRenderFns */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _node_modules_cache_loader_dist_cjs_js_cacheDirectory_node_modules_vuepress_core_node_modules_cache_vuepress_cacheIdentifier_4b61893c_vue_loader_template_node_modules_vue_loader_lib_loaders_templateLoader_js_vue_loader_options_node_modules_cache_loader_dist_cjs_js_ref_1_0_node_modules_vue_loader_lib_index_js_ref_1_1_node_modules_vuepress_markdown_loader_index_js_ref_1_2_how_to_create_a_release_md_vue_type_template_id_b8f75cc0___WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! -!../../../node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/@vuepress/core/node_modules/.cache/vuepress","cacheIdentifier":"4b61893c-vue-loader-template"}!../../../node_modules/vue-loader/lib/loaders/templateLoader.js??vue-loader-options!../../../node_modules/cache-loader/dist/cjs.js??ref--1-0!../../../node_modules/vue-loader/lib??ref--1-1!../../../node_modules/@vuepress/markdown-loader??ref--1-2!./how-to-create-a-release.md?vue&type=template&id=b8f75cc0& */ "./node_modules/cache-loader/dist/cjs.js?{\"cacheDirectory\":\"node_modules/@vuepress/core/node_modules/.cache/vuepress\",\"cacheIdentifier\":\"4b61893c-vue-loader-template\"}!./node_modules/vue-loader/lib/loaders/templateLoader.js?!./node_modules/cache-loader/dist/cjs.js?!./node_modules/vue-loader/lib/index.js?!./node_modules/@vuepress/markdown-loader/index.js?!./src/ressources/how-tos/how-to-create-a-release.md?vue&type=template&id=b8f75cc0&");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "render", function() { return _node_modules_cache_loader_dist_cjs_js_cacheDirectory_node_modules_vuepress_core_node_modules_cache_vuepress_cacheIdentifier_4b61893c_vue_loader_template_node_modules_vue_loader_lib_loaders_templateLoader_js_vue_loader_options_node_modules_cache_loader_dist_cjs_js_ref_1_0_node_modules_vue_loader_lib_index_js_ref_1_1_node_modules_vuepress_markdown_loader_index_js_ref_1_2_how_to_create_a_release_md_vue_type_template_id_b8f75cc0___WEBPACK_IMPORTED_MODULE_0__["render"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "staticRenderFns", function() { return _node_modules_cache_loader_dist_cjs_js_cacheDirectory_node_modules_vuepress_core_node_modules_cache_vuepress_cacheIdentifier_4b61893c_vue_loader_template_node_modules_vue_loader_lib_loaders_templateLoader_js_vue_loader_options_node_modules_cache_loader_dist_cjs_js_ref_1_0_node_modules_vue_loader_lib_index_js_ref_1_1_node_modules_vuepress_markdown_loader_index_js_ref_1_2_how_to_create_a_release_md_vue_type_template_id_b8f75cc0___WEBPACK_IMPORTED_MODULE_0__["staticRenderFns"]; });



/***/ })

}]);
//# sourceMappingURL=24.b701f414.js.map