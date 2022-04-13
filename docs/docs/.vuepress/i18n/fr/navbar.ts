import type {NavbarConfig} from 'vuepress-theme-mix'

export const navbarFR: NavbarConfig = [

    // =================================================================================================================
    // "Home".
    {text: 'Accueil', link: '/fr/'},

    // =================================================================================================================
    // "Trading basics".
    {text: 'Bases du trading', link: '/fr/trading_basics/'},

    // =================================================================================================================
    // "Cassandre basics".
    {text: 'Bases de Cassandre', link: '/fr/cassandre_basics/'},

    // =================================================================================================================
    // "Tutorial".
    {text: 'Tutorial', link: '/fr/tutorial/'},

    // =================================================================================================================
    // "How-tos".
    {text: 'Guides', children: [
            {text: 'Email', link: 'mailto:contact@cassandre.tech'},
            {text: 'Single', link: '/fr/guides/contributor/'},
            {text: 'Single page', link: '/fr/guides/exchange/'},
        ]
    },

    // =================================================================================================================
    // "Contact & news".
    {
        text: 'Contact & news', children: [
            {text: 'Email', link: 'mailto:contact@cassandre.tech'},
            {text: 'Twitter', link: 'https://twitter.com/CassandreTech'},
            {text: 'Discord', link: 'https://discord.gg/sv3VXuTgFS'},
            {text: 'Lettre d\'information', link: 'https://cassandre.substack.com'},
        ]
    },

    // =================================================================================================================
    // Github.
    {text: 'Github', link: 'https://github.com/cassandre-tech/cassandre-trading-bot'},

]