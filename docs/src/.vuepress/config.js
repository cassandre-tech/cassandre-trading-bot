const {description} = require('../../package')

module.exports = {
    /**
     * Ref：https://v1.vuepress.vuejs.org/config/#title
     */
    title: 'Cassandre',
    /**
     * Ref：https://v1.vuepress.vuejs.org/config/#description
     */
    description: 'Cassandre makes it easy to create your Java crypto trading bot. Our Spring boot starter takes care of exchange connections, accounts, orders, trades, and positions',

    /**
     * Extra tags to be injected to the page HTML `<head>`
     *
     * ref：https://v1.vuepress.vuejs.org/config/#head
     */
    head: [
        ['meta', {name: 'theme-color', content: '#3eaf7c'}],
        ['meta', {name: 'apple-mobile-web-app-capable', content: 'yes'}],
        ['meta', {name: 'apple-mobile-web-app-status-bar-style', content: 'black'}],
        // For social networks.
        ['meta', {property: 'og:url', content: 'https://trading-bot.cassandre.tech'}],
        ['meta', {property: 'og:title', content: 'Cassandre trading bot framework'}],
        ['meta', {property: 'og:description', content: 'Cassandre is an open-source framework that makes it easy to create your Java crypto trading bot'}],
        ['meta', {property: 'og:image', content: 'https://trading-bot.cassandre.tech/assets/images/social/cassandre-trading-bot-open-graph.png'}],
        ['meta', {property: 'og:site_name', content: 'Cassandre'}],
        ['meta', {name: 'twitter:card', content: 'summary'}],
        ['meta', {name: 'twitter:site', content: '@cassandretech'}],
        ['meta', {name: 'twitter:image:alt', content: 'Cassandre'}]
    ],

    /**
     * Theme configuration, here is the default theme configuration for VuePress.
     *
     * ref：https://v1.vuepress.vuejs.org/theme/default-theme-config.html
     */
    themeConfig: {
        repo: '',
        editLinks: false,
        docsDir: '',
        editLinkText: '',
        lastUpdated: false,
        nav: [
            {
                text: 'Why Cassandre ?',
                ariaLabel: 'Why Cassandre menu',
                items: [
                    {text: 'Overview', link: '/why-cassandre/overview'},
                    {text: 'Architecture', link: '/why-cassandre/architecture'},
                    {text: 'Features & roadmap', link: '/why-cassandre/features-and-roadmap'}
                ]
            },
            {
                text: 'Learn',
                ariaLabel: 'Learn menu',
                items: [
                    {text: 'Quickstart', link: '/learn/quickstart'},
                    {text: 'Position management', link: '/learn/position-management'},
                    {text: 'Dry mode & backtesting', link: '/learn/dry-mode-and-backtesting'},
                    {text: 'Database structure', link: '/learn/database-structure'},
                    {
                        text: 'Deploy & run', items: [
                            {text: 'Using docker', link: '/learn/deploy-and-run/docker'}
                        ]
                    },
                    {
                        text: 'Technical analysis', items: [
                            {text: 'Overview', link: '/learn/technical-analysis/overview'},
                            {text: 'Create the project', link: '/learn/technical-analysis/create-the-project'},
                            {text: 'Create your strategy', link: '/learn/technical-analysis/create-your-strategy'},
                            {text: 'React to signals', link: '/learn/technical-analysis/react-to-signals'},
                            {text: 'Backtest your strategy', link: '/learn/technical-analysis/backtest-your-trading-strategy'},
                        ]
                    }
                ]
            },
            {
                text: 'Ressources',
                ariaLabel: 'Ressources menu',
                items: [
                    {text: 'Trading basics', link: '/ressources/trading-basics'},
                    {text: 'Articles', link: '/ressources/articles'},
                    {text: 'Books', link: '/ressources/books'},
                    {
                        text: 'How-tos', items: [
                            {text: 'Install development tools', link: '/ressources/how-tos/how-to-install-development-tools'},
                            {text: 'Build from sources', link: '/ressources/how-tos/how-to-build-from-sources'},
                            {text: 'Create a release', link: '/ressources/how-tos/how-to-create-a-release'},
                            {text: 'Create a Kucoin account', link: '/ressources/how-tos/how-to-create-a-kucoin-account'},
                        ]
                    },
                ],
            },
            {
                text: 'Contact / help',
                ariaLabel: 'Contact menu',
                items: [
                    {text: 'Email', link: 'mailto:contact@cassandre.tech'},
                    {text: 'Twitter', link: 'https://twitter.com/CassandreTech'},
                    {text: 'Discord', link: 'https://discord.gg/sv3VXuTgFS'},
                    {text: 'Newsletter', link: 'https://cassandre.substack.com/'},
                ],
            },
            {
                text: 'Github',
                link: 'https://github.com/cassandre-tech/cassandre-trading-bot'
            }
        ],
        sidebar: 'auto'
    },

    /**
     * Apply plugins，ref：https://v1.vuepress.vuejs.org/zh/plugin/
     */
    plugins: [
        '@vuepress/plugin-back-to-top',
        '@vuepress/plugin-medium-zoom',
    ]
}
