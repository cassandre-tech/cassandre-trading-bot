---
title: Technical analysis overview
sidebar: cassandre_sidebar
permalink: technical_analysis_overview.html
---

{% include note.html content="we learned the basics of technical analysis thanks to [Technical Analysis For Dummies](https://amzn.to/38TWaQm)." %}

## Introduction.

Technical analysis (often referred to by ‘charting’, ‘market timing’ or ‘trend following’) is the study of price behavior in financial markets in order to forecast the next movement. The goal is to identify the market sentiment : optimistic (bullish), pessimistic (bearish) or uncertain.

The key idea is that prices move in trends most of the time. These trends can be identified with patterns you see repeatedly and with support and resistance trend lines. Primary trends (lasting months or years) are punctuated by secondary movements (lasting weeks or months) in the opposite direction of the primary trend. Trends remain in place until some major events happens (Part of [Dow theory](https://en.wikipedia.org/wiki/Dow_theory)). 

At a very basic level, this is about drawing lines linking the troughs, or the peaks to guess where things are going.  Of course, what happens depends on so many things that you can’t always be right. No line could have helped at guessing the effects of the Coronavirus but people continue to try to guess what’s next, it’s in human nature to try to reduce uncertainty.

Technical analysis focuses on the price of an asset rather than on its fundamentals.

## How does it look like ?

On a chart, the horizontal axis is time, and the vertical axis is the price. The price is presented as a bar representing a time interval, showing :
  * Open : the price at which a security first trades during that time interval.
  * High : the highest price at which a stock traded during the time interval.
  * Low : the lowest price at which a stock trades over the course of a time interval.
  * Close : the final price at which it trades for that time interval.
  
This is how it looks like :
{% include image.html file="technical_analysis/technical_analysis_chart.png" alt="Technical analysis chart" caption="Technical analysis chart" %}

After adding bars to this chart, you can draw lines to forecast future prices. For example, you can draw a line connecting the highest prices, expecting other traders will sell at this point.

## What will you try to do ?

Technical analysis is the art of identifying the behaviour of other traders and take advantage of it.

For example, the herd reacts to a news coming, for example, from Twitter, and they start to interpret it to buy or sell an asset. This will make the price go up or down, and we will want to join the crowd to take advantage of this momentum.

## Indicators.

Indicators are a way to identify and measure market sentiment without using your emotions. Their goal is to identify conditions by separating the signal from the noise.

At a very basic level, an indicator is a line, or a set of lines that you put on a chart to identify events that allows you to clarify and enhance your perception of the price move.

Assets' prices are sometimes trending, meaning they have a "direction" during a certain period. Indicators can help you anticipate and see a trend, for example : 
  * A trend is beginning : Moving Average Crossover Indicator.
  * A trend is strong or weak : Slope of linear regression.
  * A trend is ending : Breakout pattern.
  * ...

_note : Most indicators have a range of time in which research shows they work best._ 

Indicators give buy and sell signals, but they don't give you a clear decision rule. There are four type of signals : 
  * Crossovers : one line crossing another one, for example, price crossing the resistance line. This is most of the time a breakout.
  * Range limits : the price is nearing an extreme of its recent range. This is a warning of an overbought or oversold condition, and most of the time a signal for an impending potential retracement or reversal.
  * Convergence : two indicator lines coming closer to one another. Usually a warning that the direction or the strength of a trend is changing.
  * Divergence : two indicator lines moving farther apart. Usually a warning that the rising price is going to stop rising.

## Managing the trade.

No matter what indicators you will use, you will take losses. You also need a set of rules designating the conditions that must be met for trade entries and exits to occur.

A trading rule will instruct your bot to buy or sell on the condition a preset criterion is met (like the price moving average crossover).

Your trading plan with four rules : 
  * Determine whether a trend exists : choose indicators for this.
  * Establish rules for opening a position : create a rule that decides when to buy.
  * Manage the risk : choose if you add or remove money to the trade.
  * Establish rules for closing a position : set when to close the opening position.

Our goal is to identify your tradable trends and apply your trading rules.

