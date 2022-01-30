# Smithed Companion
> Note: there will eventually be a wiki page on the github that goes more in-depth for all of this


## Quick Links
- [Setup](#setup)
- [Usage](#usage)
    * [Server](#server)
    * [Client](#client)
- [WIP features](#wip-features)
- [Planned features](#planned-features)

## Setup

> Note: If you are using the smithed client you can ignore this.

1. Install [fabric](https://fabricmc.net/)
2. Drag and drop into mods folder
3. Open minecraft client/boot server


## Usage

> Note: not all of these are clientside / serverside, please pay attention to which features you're using and in what context

### Server

- Items Tagged with `{smithed:{noInteract:1b}}` will not attract mobs usually tempted by them



### Client

- Global datapacks, stored in the default `.minecraft` folder under `datapacks`

- Colored durability bars, purely cosmetic for developers to make their custom items POP, done in `{smithed:{colors:[]}}` which is an array of RGB ints

- No vanilla durability text for items tagged with the `{smithed:{durability:{}}}` tag, solution? use smithed durability :)

- Custom item groups, created within the folder of your datapacks and distrubuted to clients with the companion mod upon server join, and updated upon server reload. See [this](https://fabricmc.net/) for more info on itemgroups

## WIP Features

- Colored durability bars, right now its a singular solid color, however i want to add support for multicolor lerping

- Spawners will always be placed with proper rotation

- Installed smithed datapacks will be viewable from main menu(and hopefully on servers as well)

- Custom Items will appear within the searchbar without being added to an itemgroup


## Planned Features
> Note: how is this different from [`WIP Features?`](#wip-features), I haven't started working on these yet

- Shortcuts, datapack devs can specify shortcut commands within their datapacks, converting `/function foo:bar/bam/bop` -> `/bop`

- TODO: yav please fill this out  so i can use it as a TODO list :)

