# Project ENVLP

Basic tools to help enable e-commerce integrations with cardano.

## ENVLP Wallet

We're building a wallet for cardano that allows online stores to process multiple
payments at the same time. It operates differently from regular Daedalus/Yoroi 
wallets to display associated online order information with the transactions in the 
blockchain.

Online stores will be able to see who purchased what, at what price, directly
from their wallet, and from which online stores - many businesses sell from 
Amazon, Ebay, their own website, etc. There are many things we can add here
and we will introduce plug-in support so more functionalities can be created by the 
community.

## Payment processing at scale

The BIP44 standard defines a gap of unused payment addresses - 20 by default.
This causes issues for regular wallet users. If an online store processes too 
many orders at the same time, and 20 payment addresses are left without any 
transactions, payments made to address 21 onward won't be visible in regular 
wallets such as Daedalus/Yoroi.

Our wallet is being built to handle this nicely and work around this limitation,
so online sellers can be able to view all their sales all the time. This doesn't
affect regular users who don't transact often, but will certainly affect larger
retailers. 

## Our Catalyst project

We've applied for funding with the Fund2 catalyst project as we believe this wallet will enable more developers and businesses to come and join the ecosystem by building their own extensions for the wallet.

For more details please visit:

https://cardano.ideascale.com/a/dtd/ENVLP-a-wallet-with-plug-ins/322314-48088

* Registration is required to view the proposal.

## Delegate to the **SHOP** pool to support our project!

<a href="https://adapools.org/pool/76ce5348012e971dff93887b4870dd8aeb0a7ffc02a25ccdf337fc1c" target="_blank"><img src="https://static.adapools.org/summary/adapools.org!ext!static.php!v=epoch_summary&pool=76ce5348012e971dff93887b4870dd8aeb0a7ffc02a25ccdf33.png" alt=""></a>

We want to offer everything for free to help with the adoption of Cardano as 
viable payment gateway for everyone.
 
Pool fees will help to support the development and maintenance of this wallet and our e-commerce
integrations being.

## Videos
[![ENVLP wallet](https://img.youtube.com/vi/cUyfRMnhrAo/0.jpg)](https://www.youtube.com/watch?v=cUyfRMnhrAo)


## ROADMAP

 * Allow generation of offline, 24-word seed phrases that can work to create
  wallets from an air-gapped system.
  
 * Upload public root keys to shopify and other online stores that use the 
 cardation e-commerce integration, for generation of payment addresses.

 * Implement a visualization-only wallet, which doesn't keep any 
 private keys but can receive updates regarding payments to your wallets.
 
 * Provide full wallet functionality using the `cardano-wallet` backend to 
 actually allow transferring funds out and delegating to your stake pool of choice
 
 * Pull order details from the remote servers that process cardano-based 
 purchases, then display these with each transaction in the wallet
 
 * Implement plug-in support allowing other developers to add their own 
 functionalities to the wallet (such as reporting, etc)
  
