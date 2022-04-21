import type { SidebarConfig } from 'vuepress-theme-mix'

export const sidebarFR: SidebarConfig = {

    // =================================================================================================================
    // "Trading basics".
    '/fr/trading_basics/': [
        {
            type: 'group',
            text: 'Les bases du trading',
            link: '',
            children: [
                '',
                'what-is-a-cryptocurrency',
                'what-is-an-exchange',
                'what-is-an-account',
                'what-is-a-ticker',
                'what-is-an-order',
            ],
        },
    ],

    // =================================================================================================================
    // "Cassandre basics".
    '/fr/cassandre_basics/': [
        {
            type: 'group',
            text: 'Les bases de Cassandre',
            link: '',
            children: [
                '',
                'features-and-roadmap',
                'what-is-a-strategy',
                'architecture',
                'strategy-creation',
                'strategy-events',
                'strategy-orders-management',
                'strategy-positions-management',
                'strategy-utils',
                'database-schema',
            ],
        },
    ],

    // =================================================================================================================
    // "Cassandre basics".
    '/fr/tutorial/': [
        {
            type: 'group',
            text: 'Créez votre premier bot!',
            link: '',
            children: [
                '',
            ],
        },
    ],

    // =================================================================================================================
    // "Guides".
    '/fr/guides/exchange': [
        {
            type: 'group',
            text: 'Guides',
            link: '',
            children: [
                '',
            ],
        },
    ],

    '/fr/guides/contributor': [
        {
            type: 'group',
            text: 'Guides',
            link: '',
            children: [
                '',
                'how-to-install-development-tools',
            ],
        },
    ],

}