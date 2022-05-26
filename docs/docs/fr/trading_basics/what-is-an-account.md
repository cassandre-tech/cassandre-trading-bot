---
lang: fr-FR
title: Qu'est-ce qu'un compte sur un exchange ?
description: Trading - Qu'est-ce qu'un compte sur un exchange ?
---

# Qu'est-ce qu'un compte ?

Habituellement, sur un exchange, vous pouvez avoir plusieurs comptes avec des usages différents (Classique, trading,
trading sur marge, future...), et, pour chacun d'entre eux, vous avez un solde pour chaque devise.

Un solde n'est pas simplement le montant que vous avez; il peut être plus compliqué se décompose donc de la façon
suivante :

| Champs      | Description                             |
|:------------|:----------------------------------------|
| currency    | Devise                                  |
| total       | Renvoie le montant total pour la devise |
| available   | Montant disponible pour le trading      |
| frozen      | Montant gelé                            |
| loaned      | Montant prêté                           |
| borrowed    | Returns emprunté                        |
| withdrawing | Montant verrouillé qui va être retiré   |
| depositing  | Montant verrouillé qui va être déposé   |