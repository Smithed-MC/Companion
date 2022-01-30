# Itemgroups

## What is an itemgroup and what can it do?

- An itemgroup is a new tab added to the creative inventory, defined by a .json file in a datapack.
- The items within the itemgroup can be taken from loot tables existing in the datapack, or hardcoded to be specific items.


- There are several things itemgroup files can do, and several things they will not do.

  - Itemgroups CAN:
      * Append items to existing itemgroups.
      * Append existing itemgroups to new itemgroups.
          * It is not possible to append a vanilla itemgroup to a custom itemgroup. However, custom itemgroups and other items can be appended to vanilla itemgroups.
      * Create new itemgroups with new items, icons, and textures.
      
  - Itemgroups will NOT:
      * Replace or remove items in existing itemgroups.
      * Change existing itemgroup textures.
      * Change existing itemgroup icons.
      * Remove existing itemgroups.



## Folder Structure

- Itemgroups are located in the your datapack's `data` folder under `<namespace>/item_groups`.
- For example, an itemgroup named `example` under the `foo` namespace would be located at `your_datapack_name/data/foo/item_groups/example.json`
- They can also be located within folders in the `item_groups` folder.
- For example, an itemgroup could be at `your_datapack_name/data/foo/item_groups/example_folder/example.json`



## JSON Structure

- Itemgroups are composed of a few main parameters:
    * The `operation` parameter
        - Specifies what type of operation is going to be used for the itemgroup.
        - `smithed:create` creates a brand new itemgroup.
        - `smithed:append` appends entries to a specified itemgroup. This may be a ([vanilla itemgroup](#vanilla-item-groups)) or an itemgroup specified in another datapack.
    
    * The `id` parameter
        - Specifies the id of the itemgroup to either be created or appended to, based on the `operation` paremeter.
    
    * The `icon` parameter.
        - Specifies the [item](#item) to be used for the icon of the itemgroup tab.
        - Can only be used in `smithed:create` operations.

    * The `texture` parameter.
        - Takes in a path for itemgroup texture.
        - Not yet implemented.
        - Can only be used in `smithed:create` operations.
    
    * The `entries` parameter
        - Specifies the entries to either be created in or appended to an itemgroup, based on the `operation` parameter.
        - Corresponding entry types can be seen within [parameters](#parameter-types).

## Parameter Types
> Note: each parameter comes with an example.

Smithed-companion builds itemgroups using 4 main types all defined by the `type` parameter:
- [Item](#item): takes an `id` and an `nbt` parameter.
- [Loot table](#loot-table): takes an `id` parameter.
- [Loot directory](#loot-directory): takes an `id` parameter.
- [Item group parameter](#item-group-parameter): takes an `id` paramaeter.

### Item
- Grabs a single hardcoded item.
- `type` parameter of `minecraft:item`.
- `id` parameter of desired item id.
- `nbt` parameter of desired NBT for item.

**Example**
```json
{
    "type": "minecraft:item",
    "id": "minecraft:stone",
    "nbt": "{display:{Name:'{\"text\":\"foo\"}'}}"
}
```

### Loot table
> Note: all loot tables use default conditions, loot tables with special conditions will not work!

- Grabs a single loot table file from the specified path.
- `type`: `minecraft:loot_table`.
- `id`: Path to desired loot table.

**Example**
```json
{
    "type": "minecraft:loot_table",
    "id": "foo:bar/bat"
}
```

### Loot Directory
- Grabs an entire folder of loot tables from the specified path.
- `type`: `smithed:loot_directory`.
- `id`: Path to desired loot directory.

**Example**
```json
{
    "type":"smithed:loot_directory",
    "id": "foo:bar"
}
```

### Item Group Parameter
- Grabs an entire item group with the specified id.
- `type`: `smithed:item_group`.
- `id`: Path to desired item group.

**Example**
```json
{
    "type":"smithed:item_group",
    "id": "foo:bat"
}
```


## Vanilla Item Groups
> Note: this is a list of vanilla itemgroup tabs available to be appended to.
- `minecraft:building`
- `minecraft:decoration`
- `minecraft:redstone`
- `minecraft:transportation`
- `minecraft:miscellaneous`
- `minecraft:foodstuffs`
- `minecraft:tools`
- `minecraft:combat`
- `minecraft:brewing`
- `minecraft:search`