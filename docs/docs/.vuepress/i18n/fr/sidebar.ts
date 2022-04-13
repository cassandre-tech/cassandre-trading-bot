import type { SidebarConfig } from 'vuepress-theme-mix'

export const sidebarFR: SidebarConfig = {
    // "Trading basics".
    '/fr/trading_basics/': [
        {
            type: 'group',
            text: 'Les bases du trading',
            link: '',
            children: [
                '',
                'what-is-a-cryptocurrency',
                'what-is-trading',
                'what-is-an-exchange',
                'what-is-an-account',
                'what-is-a-ticker',
                'what-is-an-order',
            ],
        },
    ],
}