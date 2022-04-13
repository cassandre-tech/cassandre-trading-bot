import type {NavbarConfig} from 'vuepress-theme-mix'

export const navbarFR: NavbarConfig = [
    // "Home".
    {text: 'Accueil', link: '/fr/'},

    // "Trading basics".
    {text: 'Bases du trading', link: '/fr/trading_basics/'},

    // "Cassandre basics".
    {text: 'Bases de Cassandre', link: 'https://opencollective.com/cassandre-tech'},

    // "Tutorial".
    {text: 'Tutorial', link: 'https://opencollective.com/cassandre-tech'},

    // "How-tos".
    {text: 'Guides', link: 'https://opencollective.com/cassandre-tech'},

    // "Contact & news".
    {
        text: 'Contact & news', children: [
            {text: 'Email', link: 'mailto:contact@cassandre.tech'},
            {text: 'Twitter', link: 'https://twitter.com/CassandreTech'},
            {text: 'Discord', link: 'https://discord.gg/sv3VXuTgFS'},
            {text: 'Lettre d\'information', link: 'https://cassandre.substack.com'},
        ]
    },

    // Github.
    {text: 'Github', link: 'https://github.com/cassandre-tech/cassandre-trading-bot'},
]