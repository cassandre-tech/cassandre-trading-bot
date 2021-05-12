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
        // Favicons.
        ['link', {rel: "apple-touch-icon", sizes: "57x57", href: "/assets/images/favicon/apple-icon-57x57.png"}],
        ['link', {rel: "apple-touch-icon", sizes: "60x60", href: "/assets/images/favicon/apple-icon-60x60.png"}],
        ['link', {rel: "apple-touch-icon", sizes: "72x72", href: "/assets/images/favicon/apple-icon-72x72.png"}],
        ['link', {rel: "apple-touch-icon", sizes: "76x76", href: "/assets/images/favicon/apple-icon-76x76.png"}],
        ['link', {rel: "apple-touch-icon", sizes: "114x114", href: "/assets/images/favicon/apple-icon-114x114.png"}],
        ['link', {rel: "apple-touch-icon", sizes: "120x120", href: "/assets/images/favicon/apple-icon-120x120.png"}],
        ['link', {rel: "apple-touch-icon", sizes: "144x144", href: "/assets/images/favicon/apple-icon-144x144.png"}],
        ['link', {rel: "apple-touch-icon", sizes: "152x152", href: "/assets/images/favicon/apple-icon-152x152.png"}],
        ['link', {rel: "apple-touch-icon", sizes: "180x180", href: "/assets/images/favicon/apple-icon-180x180.png"}],
        ['link', {rel: "icon", sizes: "192x192", href: "/assets/images/favicon/android-icon-192x192.png"}],
        ['link', {rel: "icon", sizes: "32x32", href: "/assets/images/favicon/favicon-32x32.pn"}],
        ['link', {rel: "icon", sizes: "96x96", href: "/assets/images/favicon/favicon-96x96.png"}],
        ['link', {rel: "icon", sizes: "16x16", href: "/assets/images/favicon/favicon-16x16.png"}],
        ['link', {rel: "manifest", href: "/assets/images/favicon/manifest.json"}],
        ['link', {rel: "msapplication-TileColor", content: "#fffff"}],
        ['link', {rel: "msapplication-TileImage", href: "/assets/images/favicon/ms-icon-144x144.png"}],
        // For social networks.
        ['meta', {property: 'og:url', content: 'https://trading-bot.cassandre.tech'}],
        ['meta', {property: 'og:title', content: 'Cassandre trading bot framework'}],
        ['meta', {
            property: 'og:description',
            content: 'Cassandre is an open-source framework that makes it easy to create your Java crypto trading bot'
        }],
        ['meta', {
            property: 'og:image',
            content: 'https://trading-bot.cassandre.tech/assets/images/social/cassandre-trading-bot-open-graph.png'
        }],
        ['meta', {property: 'og:site_name', content: 'Cassandre'}],
        ['meta', {name: 'twitter:title', content: 'Cassandre trading bot framework'}],
        ['meta', {name: 'twitter:card', content: 'summary_large_image'}],
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
                ariaLabel: 'Why Cassandre',
                items: [
                    {text: 'Overview', link: '/why-cassandre/overview'},
                    {text: 'Features & roadmap', link: '/why-cassandre/features-and-roadmap'},
                    {text: 'Supported exchanges', link: '/why-cassandre/supported-cryptocurrency-exchanges'}
                ]
            },
            {
                text: 'Learn',
                ariaLabel: 'Learn',
                items: [
                    {
                        text: 'Basics', items: [
                            {text: 'Quickstart', link: '/learn/quickstart'},
                            {text: 'Position management', link: '/learn/position-management'},
                            {text: 'Dry mode & backtesting', link: '/learn/dry-mode-and-backtesting'},
                        ]
                    },
                    {
                        text: 'Advanced', items: [
                            {text: 'Technical analysis', link: '/learn/technical-analysis'},
                        ]
                    },
                    {
                        text: 'Structure', items: [
                            {text: 'Database', link: '/learn/database-structure'},
                            {text: 'Architecture', link: '/learn/architecture'},
                        ]
                    }
                ]
            },
            {
                text: 'Deploy & run',
                ariaLabel: 'Deploy & run',
                items: [
                    {text: 'Using Docker', link: '/deploy-and-run/docker'},
                    {text: 'Using Qovery', link: '/deploy-and-run/qovery'}
                ]
            },
            {
                text: 'Ressources',
                ariaLabel: 'Ressources',
                items: [
                    {
                        text: 'Trading',
                        ariaLabel: 'Trading',
                        items: [
                            {text: 'Trading basics', link: '/ressources/trading-basics'},
                        ]
                    },
                    {
                        text: 'Things to read',
                        ariaLabel: 'Things to read',
                        items: [

                            {text: 'Articles', link: '/ressources/articles'},
                            {text: 'Books', link: '/ressources/books'},
                        ]
                    },
                    {
                        text: 'How-tos', items: [
                            {
                                text: 'Install development tools',
                                link: '/ressources/how-tos/how-to-install-development-tools'
                            },
                            {text: 'Build from sources', link: '/ressources/how-tos/how-to-build-from-sources'},
                            {text: 'Create a release', link: '/ressources/how-tos/how-to-create-a-release'},
                            {
                                text: 'Create a Kucoin account',
                                link: '/ressources/how-tos/how-to-create-a-kucoin-account'
                            },
                        ]
                    },
                ],
            },
            {
                text: 'Contact / Support',
                ariaLabel: 'Contact',
                items: [
                    {text: 'Email', link: 'mailto:contact@cassandre.tech'},
                    {text: 'Twitter', link: 'https://twitter.com/CassandreTech'},
                    {text: 'Discord', link: 'https://discord.gg/sv3VXuTgFS'},
                    {text: 'Newsletter', link: 'https://cassandre.substack.com/'},
                ],
            },
            {
                text: 'Support us',
                link: 'https://opencollective.com/cassandre-tech'
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
        ['@vuepress/plugin-back-to-top'],
        ['@vuepress/plugin-medium-zoom'],
        ['@vuepress/plugin-google-analytics', {'ga': 'UA-158255496-1'}]
    ]
}
