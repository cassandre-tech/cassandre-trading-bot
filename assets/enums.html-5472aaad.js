import{_ as d,r as o,o as n,c as a,b as t,d as e,a as s,e as i}from"./app-bb6d68d1.js";const h={},p=t("h1",{id:"enums",tabindex:"-1"},[t("a",{class:"header-anchor",href:"#enums","aria-hidden":"true"},"#"),e(" Enums")],-1),g=t("h3",{id:"about-enums",tabindex:"-1"},[t("a",{class:"header-anchor",href:"#about-enums","aria-hidden":"true"},"#"),e(" About enums")],-1),c={href:"https://graphql.github.io/graphql-spec/June2018/#sec-Enums",target:"_blank",rel:"noopener noreferrer"},l=i('<h3 id="orderstatus" tabindex="-1"><a class="header-anchor" href="#orderstatus" aria-hidden="true">#</a> OrderStatus</h3><p>Order status.</p><table><tr><th>Value</th><th>Description</th></tr><tr><td><strong>CANCELED</strong></td><td><p>Removed from order book at exchange.</p></td></tr><tr><td><strong>CLOSED</strong></td><td><p>Order has been either filled or cancelled.</p></td></tr><tr><td><strong>EXPIRED</strong></td><td><p>Order has expired it’s time to live or trading session and been removed from order book.</p></td></tr><tr><td><strong>FILLED</strong></td><td><p>Fully match against opposite order on order book at exchange.</p></td></tr><tr><td><strong>NEW</strong></td><td><p>Initial order when placed on the order book at exchange.</p></td></tr><tr><td><strong>OPEN</strong></td><td><p>Order is open and waiting to be filled.</p></td></tr><tr><td><strong>PARTIALLY_CANCELED</strong></td><td><p>Order partially canceled at exchange.</p></td></tr><tr><td><strong>PARTIALLY_FILLED</strong></td><td><p>Partially match against opposite order on order book at exchange.</p></td></tr><tr><td><strong>PENDING_CANCEL</strong></td><td><p>Waiting to be removed from order book at exchange.</p></td></tr><tr><td><strong>PENDING_NEW</strong></td><td><p>Initial order when instantiated.</p></td></tr><tr><td><strong>PENDING_REPLACE</strong></td><td><p>Waiting to be replaced by another order on order book at exchange.</p></td></tr><tr><td><strong>REJECTED</strong></td><td><p>Order has been rejected by exchange and not place on order book.</p></td></tr><tr><td><strong>REPLACED</strong></td><td><p>Order has been replaced by another order on order book at exchange.</p></td></tr><tr><td><strong>STOPPED</strong></td><td><p>Order has been triggered at stop price.</p></td></tr><tr><td><strong>UNKNOWN</strong></td><td><p>The exchange returned a state which is not in the exchange’s API documentation. The state of the order cannot be confirmed.</p></td></tr></table><hr><h3 id="ordertype" tabindex="-1"><a class="header-anchor" href="#ordertype" aria-hidden="true">#</a> OrderType</h3><p>Order type.</p><table><tr><th>Value</th><th>Description</th></tr><tr><td><strong>ASK</strong></td><td><p>Selling.</p></td></tr><tr><td><strong>BID</strong></td><td><p>Buying.</p></td></tr></table><hr><h3 id="positionstatus" tabindex="-1"><a class="header-anchor" href="#positionstatus" aria-hidden="true">#</a> PositionStatus</h3><p>Position status.</p><table><tr><th>Value</th><th>Description</th></tr><tr><td><strong>CLOSED</strong></td><td><p>Closed - the sell order has been accepted.</p></td></tr><tr><td><strong>CLOSING</strong></td><td><p>Closing - a sell order has been made but not yet completed.</p></td></tr><tr><td><strong>CLOSING_FAILURE</strong></td><td><p>Closing failure - the sell order did not succeed.</p></td></tr><tr><td><strong>OPENED</strong></td><td><p>Opened - the buy order has been accepted.</p></td></tr><tr><td><strong>OPENING</strong></td><td><p>Opening - a position has been created, a buy order has been made but not yet completed.</p></td></tr><tr><td><strong>OPENING_FAILURE</strong></td><td><p>Opening failure - a position has been created, but the buy order did not succeed.</p></td></tr></table><hr><h3 id="positiontype" tabindex="-1"><a class="header-anchor" href="#positiontype" aria-hidden="true">#</a> PositionType</h3><p>Position type.</p><table><tr><th>Value</th><th>Description</th></tr><tr><td><strong>LONG</strong></td><td><p>Long position is nothing but buying share and selling them later for more.</p></td></tr><tr><td><strong>SHORT</strong></td><td><p>Short position is nothing but selling share and buying back later for less.</p></td></tr></table><hr><h3 id="tradetype" tabindex="-1"><a class="header-anchor" href="#tradetype" aria-hidden="true">#</a> TradeType</h3><p>Trade type.</p><table><tr><th>Value</th><th>Description</th></tr><tr><td><strong>ASK</strong></td><td><p>Selling.</p></td></tr><tr><td><strong>BID</strong></td><td><p>Buying.</p></td></tr></table><hr>',20);function b(u,E){const r=o("ExternalLinkIcon");return n(),a("div",null,[p,g,t("p",null,[t("a",c,[e("Enums"),s(r)]),e(" represent possible sets of values for a field.")]),l])}const f=d(h,[["render",b],["__file","enums.html.vue"]]);export{f as default};
