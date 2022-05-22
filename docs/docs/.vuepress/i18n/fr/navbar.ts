export const navbarFR = [

    // =================================================================================================================
    // "Home".
    {text: 'Accueil', link: '/fr/'},

    // =================================================================================================================
    // "Trading basics".
    {text: 'Bases du trading', link: '/fr/trading_basics/what-is-trading'},

    // =================================================================================================================
    // "Cassandre basics".
    {text: 'Bases de Cassandre', link: '/fr/cassandre_basics/overview'},

    // =================================================================================================================
    // "Tutorial".
    {text: 'Tutorial', link: '/fr/tutorial/create-your-project'},

    // =================================================================================================================
    // "Guides".
    {
        text: 'Guides', children: [
            {
                text: 'Contributeur',
                children: [
                    {text: 'Installer les outils de dev', link: '/fr/guides/contributor/how-to-install-development-tools'},
                    {text: 'Construire les sources', link: '/fr/guides/contributor/how-to-build-from-sources'},
                    {text: 'Créer une version', link: '/fr/guides/contributor/how-to-create-a-release'},
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