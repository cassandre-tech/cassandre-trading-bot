import {defineUserConfig} from 'vuepress'
import type {DefaultThemeOptions} from 'vuepress'
import { defaultTheme } from '@vuepress/theme-default'
import {navbarFR} from "./i18n/fr/navbar";
import {sidebarFR} from "./i18n/fr/sidebar";
import {navbarEN} from "./i18n/en/navbar";
import {sidebarEN} from "./i18n/en/sidebar";

export default defineUserConfig<DefaultThemeOptions>({

    // =================================================================================================================
    // Site configuration.
    locales: {
        // English.
        '/': {
            lang: 'en-US',
            title: 'Cassandre trading bot framework',
            description: 'Create your Java crypto trading bot in minutes with our spring boot starter',
        },
        // French.
        '/fr/': {
            lang: 'fr-FR',
            title: 'Cassandre trading bot framework',
            description: 'Créer votre trading bot en java en quelques minutes avec notre spring boot starter',
        },
    },

    // =================================================================================================================
    // HTML Header.
    head: [
        // Meta.
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
        // Social networks.
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

    // =================================================================================================================
    // Theme and its configuration.
    theme: defaultTheme({
        lastUpdated: false,
        contributors: false,
        title: 'Cassandre trading bot',
        logo: 'assets/images/logo/cassandre-trading-bot-without-text.png',
        logoDark: 'assets/images/logo/cassandre-trading-bot-without-text-dark-mode.png',
        // =============================================================================================================
        // Menus.
        locales: {
            // =========================================================================================================
            // English.
            '/': {
                // Text for the language dropdown.
                selectLanguageText: 'Languages',
                selectText: 'Languages',
                // Label for this locale in the language dropdown.
                selectLanguageName: 'English',
                label: 'English',
                // Menu.
                navbar: navbarEN,
                sidebar: sidebarEN,
            },
            // =========================================================================================================
            // French.
            '/fr/': {
                // Text for the language dropdown.
                selectLanguageText: 'Langues',
                selectText: 'Langues',
                // Label for this locale in the language dropdown.
                selectLanguageName: 'Français',
                label: 'Français',
                // Menu.
                navbar: navbarFR,
                sidebar: sidebarFR,
            },
        },
    }),

    // =================================================================================================================
    // Plugins.
    plugins: [
        // Google Analytics.
        [
            '@vuepress/plugin-google-analytics',
            {
                id: 'UA-158255496-1',
            }
        ],
        // Doc search.
        [
            '@vuepress/plugin-docsearch',
            {
                appId: 'Z5EV5Y49BO',
                apiKey: '92c15c16c728a530fc095a798081e674',
                indexName: 'cassandre',
            },
        ],
    ]

})