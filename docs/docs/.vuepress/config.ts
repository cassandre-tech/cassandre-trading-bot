import {defaultTheme, defineUserConfig} from 'vuepress'
import type {DefaultThemeOptions} from 'vuepress'

export default defineUserConfig<DefaultThemeOptions>({
    lang: 'en-US',
    title: 'Cassandre trading bot',
    description: 'Create your Java crypto trading bot in minutes',

    // Head
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

    theme: defaultTheme({
        lastUpdated: false,
        contributors: false,
        logo: 'assets/images/logo/cassandre-trading-bot-without-text.png',
        logoDark: 'assets/images/logo/cassandre-trading-bot-without-text-dark-mode.png',
        navbar: [
            // =========================================================================================================
            // Why Cassandre ?
            {
                text: 'Why Cassandre ?',
                children: [
                    {text: 'Overview', link: '/why-cassandre/overview'},
                    {text: 'Features & roadmap', link: '/why-cassandre/features-and-roadmap'},
                    {text: 'Supported exchanges', link: '/why-cassandre/supported-cryptocurrency-exchanges'},
                ]
            },
            // =========================================================================================================
            // Learn
            {
                text: 'Learn',
                children: [
                    // =================================================================================================
                    {
                        text: 'Basics',
                        children: [
                            {text: 'Quickstart', link: '/learn/quickstart'},
                            {text: 'Exchange configuration', link: '/learn/exchange-connection-configuration'},
                            {text: 'Position management', link: '/learn/position-management'},
                            {text: 'Dry mode & backtesting', link: '/learn/dry-mode-and-backtesting'}
                        ]
                    },
                    // =================================================================================================
                    {
                        text: 'Advanced',
                        children: [
                            {text: 'Technical analysis', link: '/learn/technical-analysis'},
                            {text: 'Data importation', link: '/learn/import-historical-data'},
                            {text: 'GraphQL API', link: '/learn/graphql-api'}
                        ]
                    },
                    // =================================================================================================
                    {
                        text: 'Structure',
                        children: [
                            {text: 'Database', link: '/learn/database-structure'},
                            {text: 'Architecture', link: '/learn/architecture'},
                        ]
                    }
                ]
            },
            // =========================================================================================================
            // Deploy & run
            {
                text: 'Deploy & run',
                children: [
                    {text: 'Using Docker', link: '/deploy-and-run/docker'},
                ]
            },
            // =========================================================================================================
            // Ressources
            {
                text: 'Ressources',
                children: [
                    // =================================================================================================
                    {
                        text: 'Trading',
                        children: [
                            {text: 'Trading basics', link: '/ressources/trading-basics'}
                        ]
                    },
                    // =================================================================================================
                    {
                        text: 'Things to read',
                        children: [
                            {text: 'Articles', link: '/ressources/articles'},
                            {text: 'Books', link: '/ressources/books'}
                        ]
                    },
                    // =================================================================================================
                    {
                        text: 'How-tos',
                        children: [
                            {
                                text: 'Fix common problems',
                                link: '/ressources/how-tos/how-to-fix-common-problems.md'
                            },
                            {
                                text: 'Install development tools',
                                link: '/ressources/how-tos/how-to-install-development-tools'
                            },
                            {text: 'Build from sources', link: '/ressources/how-tos/how-to-build-from-sources'},
                            {
                                text: 'Review code',
                                link: '/ressources/how-tos/how-to-review-code'
                            },
                            {text: 'Create a release', link: '/ressources/how-tos/how-to-create-a-release'},
                            {
                                text: 'Create a Kucoin account',
                                link: '/ressources/how-tos/how-to-create-a-kucoin-account'
                            }
                        ]
                    }
                ]
            },
            // =========================================================================================================
            // Contact
            {
                text: 'Contact / News',
                children: [
                    {text: 'Email', link: 'mailto:contact@cassandre.tech'},
                    {text: 'Twitter', link: 'https://twitter.com/CassandreTech'},
                    {text: 'Discord', link: 'https://discord.gg/sv3VXuTgFS'},
                    {text: 'Newsletter', link: 'https://cassandre.substack.com/'}
                ]
            },
            // =========================================================================================================
            // Support us
            {
                text: 'Support us',
                link: 'https://opencollective.com/cassandre-tech'
            },
            // =========================================================================================================
            // Github
            {
                text: 'Github',
                link: 'https://github.com/cassandre-tech/cassandre-trading-bot'
            }
        ],
    },),

    // Plugins
    plugins: [
        // Google analytics
        ['@vuepress/plugin-google-analytics', {'id': 'UA-158255496-1'}],
        // Docsearch
        [
            '@vuepress/plugin-docsearch',
            {
                appId: 'Z5EV5Y49BO',
                apiKey: '92c15c16c728a530fc095a798081e674',
                indexName: 'cassandre'
            },
        ],
    ]

})