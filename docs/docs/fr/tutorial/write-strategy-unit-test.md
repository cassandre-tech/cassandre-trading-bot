---
lang: fr-FR
title: Écrivez le test de votre stratégie
description: Cassandre tutorial - Écrivez le test de votre stratégie
---

# Écrivez le test de votre stratégie

## Qu'est-ce que nous voulons faire ?

Notre objectif est de vérifier que notre stratégie génère des gains. Comme nous ne pouvons pas prédire le futur, nous
allons tester notre stratégie sur des données historiques et avec un exchange "simulé" fourni par Cassandre (Notre
[dry mode](../cassandre_basics/dry-mode-and-backtesting.md)).

Il y a trois étapes :

- Configurez les montants d'actifs que vous utilisez pendant les tests.
- Téléchargez et importer les données historiques pour que Cassandre les envoie à votre stratégie.
- Écrivez le test unitaire qui vérifie les gains réalisés quand tous les tickers auront été traitées par votre stratégie.

## Configurez vos comptes.

Pour simuler correctement le comportement de votre stratégie, vous avez besoin de dire au mode dry de Cassandre de
combien d'actifs vous allez disposer pour prendre vos positions.

Ceci se fait en créant des fichiers CSV commençant par `user-` et finissant par `csv` dans le répertoire `src/test/resources`.

Si vous ouvrez le fichier `src/test/resources/user-trade.csv`, vous verrez :

```
BTC,0.99962937
USDT,1000
ETH,10
```

Grâce à ce fichier, quand Cassandre démarrera en mode "dry", votre stratégie agira comme si l'exchange lui avait dit que
votre compte avait 0,99962937 BTC, 1 000 USDT et 10 ETH. Bien sûr, lors du test de votre stratégie, ces montants
évolueront automatiquement en fonction de vos achats/ventes.

## Téléchargez un historique de données de marché

Comme je l'ai indiqué un peu plus haut, nous allons tester le comportement de notre stratégie sur un historique de
données.
Pour ce faire, nous devons mettre les données que nous voulons utiliser dans des fichiers commençant par 
`candles-for-backtesting`, finissant par `.csv` et se trouvant dans le dossier `src/test/resources/`.

Voici un exemple du contenu d'un de ses fichiers :

```
"TIMESTAMP","OPEN","CLOSE","HIGH","LOW","VOLUME","QUOTE_VOLUME","CURRENCY_PAIR"
"1508371200","10000","10000","10000","10000","10000","10000","BTC-USDT"
```

Vous pouvez bien sûr créer ces fichiers avec les outils/sources de données de votre choix, mais voici comment je le fais
facilement, rapidement et en ligne de commande :

```bash
SYMBOL=BTC-USDT
START_DATE=`date --date="3 months ago" +"%s"`
END_DATE=`date +"%s"`
echo '"TIMESTAMP", "OPEN", "CLOSE", "HIGH", "LOW", "VOLUME", "QUOTE_VOLUME", "CURRENCY_PAIR"' > src/test/resources/candles-for-backtesting-btc-usdt.csv
curl -s "https://api.kucoin.com/api/v1/market/candles?type=15min&symbol=${SYMBOL}&startAt=${START_DATE}&endAt=${END_DATE}" \
| jq --arg SYMBOL "$SYMBOL" -r -c '.data[] | . + [$SYMBOL] | @csv' \
| tac $1 >> src/test/resources/candles-for-backtesting-btc-usdt.csv
```

::: tip
Vous pouvez ajouter autant de fichiers de backtesting que vous voulez.
:::

## Écrivez votre test

Le code du test est assez facile à comprendre :

```java

@SpringBootTest
@ActiveProfiles("test")
@Import(TickerFluxMock.class)
@DisplayName("Simple strategy test")
public class ETHStrategyTest {

    @Autowired
    private TickerFluxMock tickerFluxMock;

    /** Dumb strategy. */
    @Autowired
    private ETHStrategy strategy;

    /**
     * Check data reception.
     */
    @Test
    @DisplayName("Check data reception")
    public void receivedData() {
        await().forever().until(() -> tickerFluxMock.isFluxDone());

        // =============================================================================================================
        System.out.println("");
        System.out.println("Gains by position");
        strategy.getPositions()
                .values()
                .forEach(positionDTO -> {
                    if (positionDTO.getStatus().equals(PositionStatusDTO.CLOSED)) {
                        System.out.println("Position " + positionDTO.getPositionId() + " closed with gain: " + positionDTO.getGain());
                    } else {
                        System.out.println("Position " + positionDTO.getPositionId() + " NOT closed with latest gain: " + positionDTO.getLatestCalculatedGain().get());
                    }
                });

        // =============================================================================================================
        System.out.println("");
        System.out.println("Global gains");
        Map<CurrencyDTO, GainDTO> gains = strategy.getGains();
        gains.values().forEach(gainDTO -> System.out.println(gainDTO.getAmount()));
        assertFalse(gains.isEmpty(), "Failure, no gains");
        assertNotNull(gains.get(USDT), "Failure, USDT gains");
        assertTrue(gains.get(USDT).isSuperiorTo(GainDTO.ZERO), "Failure, USDT inferior to zero");
    }

}
```

Voici les principaux points :
- Il s'agit d'un test spring boot classique.
- Ajouter `@Import(TickerFluxMock.class)` fait en sorte que Cassandre charge les données de backtesting et les envois à vos stratégies.
- `private TickerFluxMock tickerFluxMock;` a été ajouté pour que l'on soit capable de savoir quand toutes les données ont été traitées.
- En utilisant `await().forever().until(() -> tickerFluxMock.isFluxDone());`, on fait en sorte que le test attende que tout soit traité par les stratégies.
- On finit par afficher l'état de chaque position puis on test les valeurs des gains pour voir si on en a bien réalisé !


## Lancez vos tests

Pour lancer les tests, il suffit de taper : `mvn test`.

Voici un exemple de résultat :

```sh
Gains by position
Position 1 closed with gain: Gains: -3.170296 USDT (-8.0 %)
Position 2 closed with gain: Gains: -3.170208 USDT (-8.0 %)
Position 3 closed with gain: Gains: -3.168264 USDT (-8.0 %)
Position 4 closed with gain: Gains: -3.165088 USDT (-8.0 %)
Position 5 closed with gain: Gains: -3.164112 USDT (-8.0 %)
Position 6 closed with gain: Gains: -3.162976 USDT (-8.0 %)
Position 7 closed with gain: Gains: -3.15744 USDT (-8.0 %)
Position 8 closed with gain: Gains: -3.1594 USDT (-8.0 %)
Position 9 closed with gain: Gains: -3.15688 USDT (-8.0 %)
Position 10 closed with gain: Gains: -3.150648 USDT (-8.0 %)
Position 11 closed with gain: Gains: -3.13744 USDT (-8.0 %)
Position 12 closed with gain: Gains: -3.11872 USDT (-8.0 %)
...
osition 120 closed with gain: Gains: 1.114808 USDT (4.0 %)
Position 121 closed with gain: Gains: 1.141692 USDT (4.0 %)
Position 122 closed with gain: Gains: 1.133536 USDT (4.0 %)
Position 123 closed with gain: Gains: 1.124956 USDT (4.0 %)
Position 124 closed with gain: Gains: 1.128588 USDT (4.0 %)
Position 125 closed with gain: Gains: 1.17594 USDT (4.0 %)
Position 126 closed with gain: Gains: 1.163032 USDT (4.0 %)
Position 127 closed with gain: Gains: 1.145548 USDT (4.0 %)
Position 128 closed with gain: Gains: 1.143424 USDT (4.0 %)
Position 129 closed with gain: Gains: 1.141428 USDT (4.0 %)
Position 130 closed with gain: Gains: 1.133732 USDT (4.0 %)
Position 131 closed with gain: Gains: 1.14464 USDT (4.0 %)
Position 132 closed with gain: Gains: 1.140584 USDT (4.0 %)
Position 133 closed with gain: Gains: 1.130728 USDT (4.0 %)
Position 134 closed with gain: Gains: 1.156604 USDT (4.0 %)
Position 135 closed with gain: Gains: 1.177412 USDT (4.0 %)
Position 136 closed with gain: Gains: 1.173072 USDT (4.0 %)
Position 137 NOT closed with latest gain: Gains: -0.6901 USDT (-2.2868640422821045 %)
Position 138 NOT closed with latest gain: Gains: -0.956 USDT (-3.1403369903564453 %)
Position 139 NOT closed with latest gain: Gains: -0.8586 USDT (-2.8294429779052734 %)
Position 140 NOT closed with latest gain: Gains: -0.7031 USDT (-2.3289411067962646 %)
Position 141 NOT closed with latest gain: Gains: -1.2125 USDT (-3.9496281147003174 %)
Position 142 NOT closed with latest gain: Gains: -1.1565 USDT (-3.7740960121154785 %)
Position 143 NOT closed with latest gain: Gains: -1.0674 USDT (-3.4934868812561035 %)
Position 144 NOT closed with latest gain: Gains: -0.5626 USDT (-1.8538539409637451 %)
Position 145 NOT closed with latest gain: Gains: -0.5489 USDT (-1.809527039527893 %)
...
Global gains
-191.903428 USDT
[ERROR] Tests run: 1, Failures: 1, Errors: 0, Skipped: 0, Time elapsed: 348.345 s <<< FAILURE! - in com.mycompany.bot.SimpleStrategyTest
[ERROR] receivedData  Time elapsed: 339.607 s  <<< FAILURE!
org.opentest4j.AssertionFailedError: Failure, USDT inferior to zero (eg loss!) ==> expected: <true> but was: <false>
	at com.mycompany.bot.SimpleStrategyTest.receivedData(SimpleStrategyTest.java:67)
```

Comme vous pouvez le constater, ce n'est pas une très bonne stratégie !