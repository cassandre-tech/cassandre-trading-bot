export const navbarEN = [

    // =================================================================================================================
    // "Home".
    {text: 'Home', link: '/'},

    // =================================================================================================================
    // "Trading basics".
    {text: 'Trading basics', link: '/trading_basics/what-is-trading'},

    // =================================================================================================================
    // "Cassandre basics".
    {text: 'Cassandre basics', link: '/cassandre_basics/overview'},

    // =================================================================================================================
    // "Tutorial".
    {text: 'Tutorial', link: '/tutorial/create-your-project'},

    // =================================================================================================================
    // "Guides".
    {
        text: 'Guides', children: [
            {
                text: 'Configuration',
                children: [
                    {text: 'Fix common problems', link: '/guides/configuration/how-to-fix-common-problems'},
                    {text: 'Exchange configuration', link: '/guides/configuration/exchange-connection-configuration'},
                ]
            },
            {
                text: 'Advanced usage',
                children: [
                    {text: 'Loading data on startup', link: '/guides/advanced_usage/import-historical-data'},
                ]
            },
            {
                text: 'Contributor',
                children: [
                    {text: 'Install development tools', link: '/guides/contributor/how-to-install-development-tools'},
                    {text: 'Build from sources', link: '/guides/contributor/how-to-build-from-sources'},
                    {text: 'Create a release', link: '/guides/contributor/how-to-create-a-release'},
                ]
            },
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