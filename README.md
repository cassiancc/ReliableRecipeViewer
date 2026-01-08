<h1 align="center">RRV</h1>

<p align="center">
  <img width="250" height="250" src="https://asphodel.cc/resources/modrinth/rrv/icon2.png">
</p>

## Overview

[Reliable Recipe Viewer](https://modrinth.com/mod/rrv) is a mod that provides recipe viewer functionality on modern Minecraft versions, from 1.21.11 to 26.1. It's based on [Extended Item View](https://modrinth.com/mod/eiv), which supported 1.21.4-1.21.10. Since EIV never made the jump to 1.21.11 and beyond and had a few issues I opted to provide this fork.

Currently supported functions are:

- recipe viewing
- bookmarking items
- item-transfer (fast-move items in crafting gui)
- hiding/showing overlay
- item highlighting (double-click on searchbar)
- cheatmode

For more details, see [Extended Item View](https://modrinth.com/mod/eiv) on Modrinth.

**NOTE: Since 1.21.2, all recipe viewers must be installed on both the client and server.**

# Developer Guide

These instructions are for my maven, but Modrinth Maven should also be useable once this mod is available on Modrinth!

## Adding the dependency
```gradle
repositories {
    exclusiveContent {
        forRepository {
            maven {
              name = "Cassian's Maven"
              url = uri("https://maven.cassian.cc")
            }
        }
        filter {
            includeGroupAndSubgroups("cc.cassian")
        }
    }
}

dependencies {
    // Fabric 1.21.11 and below
    modImplementation("cc.cassian.rrv:reliable-recipe-viewer-fabric:${rrv_version}+${minecraft_version}")
    
    // Fabric 26.1 and above
    implementation("cc.cassian.rrv:reliable-recipe-viewer-fabric:${rrv_version}+${minecraft_version}")
}
```

## Creating your mod's integration

Before you can implement your own recipes, you first have to create a RRV plugin for your mod.
This is done by creating a class implementing `ReliableRecipeViewerPlugin`:

```java
public class ExampleModRecipeViewerIntegration implements ReliableRecipeViewerPlugin {
    
    @Override
    public void onIntegrationInitialize() {
        
    }
    
}
```

Don't forget to add it as an entrypoint to your mod.

### Fabric (fabric.mod.json)
```json
{
...,
	"entrypoints": {
    ...,
		"rrv": [
			"com.example.mod.rrv.ExampleModRecipeViewerIntegration"
		]
	},
...
}
```

- Users of split sources (and RRV 6.1.0 and above) can also make use of `ReliableRecipeViewerClientPlugin` and the `rrv_client` entrypoint. This is currently functionally identical to the existing `ReliableRecipeViewerPlugin` and `rrv` entrypoint, but may change in the future as the mod is updated to better handle split sources.

```java
public class ExampleModRecipeViewerClientIntegration implements ReliableRecipeViewerClientPlugin {
    
    @Override
    public void onIntegrationInitialize() {
        
    }
	
}
```

```json
{
...,
	"entrypoints": {
    ...,
		"rrv_client": [
			"com.example.mod.rrv.ExampleModRecipeViewerClientIntegration"
		]
	},
...
}
```

## Adding a new recipe type

Since you want to add a complete new way of crafting, you first need to create your client recipe type.
Simply create a class implementing `ReliableClientRecipeType` and override the required methods:

```java
public class ExampleModClientRecipeType implements ReliableClientRecipeType {

    //Create an instance of your viewtype here
    //Relevant for next steps
    protected static final ReliableClientRecipeType INSTANCE = new ReliableClientRecipeType();
    
    
    @Override
    public Component getDisplayName() {
        return Component.literal("Example Crafting"); //This is the name of your recipe type, displayed later in the recipe view
    }

    @Override
    public int getDisplayWidth() {
        return 0; //The width of your type's gui texture
    }

    @Override
    public int getDisplayHeight() {
        return 0; //The height of your type's gui texture
    }

    @Override
    public Identifier getGuiTexture() {
        return Identifier.fromNamespaceAndPath("example-mod", "path/to/your/texture"); //Your type's gui texture.
    }

    @Override
    public int getSlotCount() {
        return 0; //The amount of slots one of your type's recipes requires (all slots including results)
    }

    @Override
    public void placeSlots(RecipeViewMenu.SlotDefinition slotDefinition) {
        //Tell RRV where your slots are located by calling slotDefinition.addItemSlot();
        //NOTE: Slot position is relative to your gui texture

        slotDefinition.addItemSlot(0, 10, 20);
        slotDefinition.addItemSlot(1, 40, 20);

    }

    @Override
    public Identifier getId() {
        return Identifier.fromNamespaceAndPath("example-mod", "your_type_id"); //A unique id for your viewtype
    }

    @Override
    public ItemStack getIcon() {
        return null; //The icon displayed in the recipe-view
    }

    @Override
    public List<ItemStack> getCraftReferences() {
        return List.of(); //Return a list of blocks/items that can be used to process your recipes (e.g. for Smelting it would be the furnace)
    }
}
```

## Adding your recipe blueprint

Now you need to add your recipe's class to tell RRV about things like rendering & items.
Just create a class implementing `ReliableClientRecipe` and override the required methods.

```java
public class ExampleModClientRecipe implements ReliableClientRecipe {

    private final SlotContent input, output;


    //You can design your constructor to suit your needs
    public YourCustomViewRecipe(ItemStack input, ItemStack output) {

        //Define your inputs and outputs here

        this.input = SlotContent.of(input);
        this.output = SlotContent.of(output);

    }

    @Override
    public IRrvRecipeViewType getViewType() {
        return YourCustomViewType.INSTANCE; //Here you need your type's instance you created before
    }

    @Override
    public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {

        //Tell RRV which SlotContent belongs to which of your previously defined slots
        slotFillContext.bindSlot(0, this.input);
        slotFillContext.bindSlot(1, this.output);

        // When you want to add custom information to some of your items simply add a stack modifier to the corresponding slots
        slotFillContext.addAdditionalStackModifier(0, (stack, tooltip) -> {
            tooltip.add(Component.literal("A cool item"));
        });

        // You can also bind a slot as "optional" and provide it with a valid SlotRenderer to ensure a slot 
        // is only rendered if there's an item in it
	    // The default SlotRenderer is used for rendering Minecraft's default slot texture
        slotFillContext.bindOptionalSlot(0, this.result, RecipeViewMenu.OptionalSlotRenderer.DEFAULT);

    }

    @Override
    public List<SlotContent> getIngredients() {
        return List.of(this.input); //Return all of your inputs here
    }

    @Override
    public List<SlotContent> getResults() {
        return List.of(this.output); //Return all of your outputs here
    }
}
```
### The SlotContent

In RRV, everything concerning recipe content is handled via a class called `SlotContent`.
It is a representation of all item stacks a slot holds. The content is constantly ticked while a the player is looking at a recipe to achieve an overview over the possible in- & outputs.
To wrap your ingredients and results (items, item stacks, fluid stacks, lists of items, ...) just call `SlotContent.of();`

### Slot dependencies

If there is a slot that should not tick independently you can bind it as a dependent slot:

```java
    @Override
    public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {

        //...

        slotFillContext.bindDependentSlot(0, this.input::index, this.input2);

    }
```

The method requires an integer supplier which tells the `SlotContent` at which position it is currently ticking.
In this case, we are using the current index of `this.input` as the index for `this.input2`. So they are always synchronized.

## Server side recipe representation

Minecraft's recipe system was changed in 1.21.2 so that all recipes only exist on the server, meaning the client is not told which recipes exist.
Since this mod requires recipes to exist clientside, we have to synchronize the recipes between the server and client ourselves.
For consistency, RRV requires a serverside representation of all recipes regardless whether it's a mod or vanilla recipe.

Creating a serverside representation of your mod recipes is quite easy, simply create a class that implements `ReliableServerRecipe` and override the methods:

```java
public class ExampleServerRecipe implements ReliableServerRecipe {


    //Create a server recipe type (the id does not have to match your client side viewtype id)
    public static final ModRecipeType<YourServerRecipe> TYPE = ModRecipeType.register(
            Identifier.fromNamespaceAndPath("example-mod", "your_recipe_id"),
            () -> new YourServerRecipe()
    );

    @Override
    public void writeToTag(CompoundTag tag) {

    }

    @Override
    public void loadFromTag(CompoundTag tag) {

    }

    @Override
    public ModRecipeType<? extends IRrvServerModRecipe> getRecipeType() {
        return TYPE;
    }
}
```
**INFO**: The API includes a `TagUtil` class that provides a lot of helper functions for encoding and decoding different objects. Note that `writeToTag` is called on the server, while `loadFromTag` is called on the client.

## Register your recipes

Registering your recipes requires you to call 2 methods in your `onIntegrationInitialize();` method:

- `ItemView.addRecipeProvider();`
- `ItemView.registerRecipeWrapper();`

```java
public class ExampleModIntegration implements ReliableRecipeViewerPlugin {

    @Override
    public void onIntegrationInitialize() {

        //For the server 
        ItemView.addServerRecipeProvider(list -> {
            //Here you can add all your server recipes
        });

        //For the client
        ItemView.registerClientRecipeWrapper(YourServerRecipe.TYPE, modRecipe -> {
            
            //Here you tell RRV how to process incoming server recipes
            //Requires you to return a list of client recipes (ReliableClientRecipe)
            
            return List.of();
        });

    }

}
```

Recipe providers registered by `ItemView.addServerRecipeProvider();` are used by the server recipe manager to maintain and update the recipe cache.
Whenever there is an update, the client is informed about the update and the mod recipe wrappers registered by `ItemView.registeClientrRecipeWrapper();` are used to convert incoming server recipes into displayable client recipes.

### Stack-Sensitives

Stack-Sensitives are "item-variants" that only differ in their itemstacks' components.<br>
Vanilla examples are: _Enchanted Books, Potions, Tipped Arrows..._<br>
<br>
If you want to add your own item stacks to the ItemView-overlay simply call `ItemView.addStackSensitive();` in a reload callback (`ItemView.addReloadCallback();`).<br>
<br>
You can also exclude items from the overlay by calling `ItemView.excludeItem();` This method does not need to be called in a reload callback, since it's only client-side.

## Conclusion

And there you go! Just reproduce these steps for each of your recipe types and you'll be fine.
Note: You can always look at RRV's builtin code, to see how everything works in practice.
If you now want to create item-transfer functionality, read the section below.

## Adding recipe-transfer functionality

To be able to shift items from the players inventory into it's crafting gui you have to override a few more methods of your class that implements `ReliableClientRecipe`:

```java

    @Override
    public boolean supportsItemTransfer() {
        return true; //Enable item transfer
    }

    @Override
    public Class<? extends AbstractContainerScreen<?>> getTransferClass() {
        return CraftingScreen.class; //Tell which screen is the corresponding crafting gui
    }

    @Override
    public void mapRecipeItems(RecipeTransferMap map) {

	//Link your recipe slots to the corresponding slots in the destination inventory (the crafting inventory)

        map.linkSlots(0, 1);
        map.linkSlots(1, 2);
        map.linkSlots(2, 3);
        map.linkSlots(3, 4);
        map.linkSlots(4, 5);
        map.linkSlots(5, 6);
        map.linkSlots(6, 7);
        map.linkSlots(7, 8);
        map.linkSlots(8, 9);

    }
```

## General hints

The `ItemView` class is the main API class for RRV, so you can always look in there if you wonder whether something can be realized with RRV or not (yet).<br>

This fork is distributed alongside its source code on Modrinth Maven, so its Javadocs are visible in your IDE as well as here on GitHub.<br>
<br>
If you still have questions, you can always contact me via [Discord](https://discord.cassian.cc)<br>
<br>
Have fun modding!


## FAQ

- Will this mod be ported to other versions/loaders?
  - This port will be kept up to date with the latest version of Minecraft. No backports are planned/necessary, please use the original mod.

## Mod Compatibility

Developers wishing to use the mod can make use of RRV's easy to use API. More info on [RRV's GitHub page](https://github.com/liushmn/ExtendedItemView). Unlike the original mod, this fork provides its sources through [Modrinth Maven](https://support.modrinth.com/en/articles/8801191-modrinth-maven#h_233c0ebd50) so that API Javadocs can be easily used.

## License
[![Code license (MIT)](https://img.shields.io/badge/code%20license-MIT-green.svg?style=flat-square)](github.com/cassiancc/bygone-fortress)

RRV is available under the open source MIT License, matching the original mod.

## Credits
This started as a port of [Extended Item View](https://modrinth.com/mod/rrv) to Fabric 1.21.11 that I made for personal use. EIV is available under [MIT License](https://www.curseforge.com/minecraft/mc-mods/extended-itemview-eiv#license), but has not been worked on in two months, and due to changes in 1.21.11, previous versions cannot be compiled against. I have opted to redesign some elements of the mod with the goal to make it a more reliable recipe viewer to use.
