<h1 align="center">EIV Canary</h1>

<p align="center">
  <img width="250" height="250" src="https://asphodel.cc/resources/modrinth/eiv/icon.png">
</p>

## Overview

[Extended Item View](https://modrinth.com/mod/eiv) is a mod that provides recipe viewer functionality on modern Minecraft versions, including 1.21.4-1.21.10. However, it has not yet made the jump to 1.21.11 and beyond. Rather than drop EIV support on my mods, I opted to provide this fork (which has been contributed to upstream, should 2Bad4Mods return and wish to use any of it.)

Currently supported functions are:

- recipe viewing
- bookmarking items
- item-transfer (fast-move items in crafting gui)
- hiding/showing overlay
- item highlighting (double-click on searchbar)
- cheatmode

For more details, see the [Extended Item View](https://modrinth.com/mod/eiv).

**NOTE: Since 1.21.2, all recipe viewers must be installed on both the client and server.**

## FAQ

- Will this mod be ported to other versions/loaders?
    - This port will be kept up to date with the latest version of Minecraft. No backports are planned/necessary, please use the original mod.

## Mod Compatibility

Mods with EIV integration can be used with this fork, which should not make unneeded changes to the API. You can find a list of mods with EIV integration in my [Modrinth Collection](https://modrinth.com/collection/4feLcYR6).

Developers wishing to use the mod can make use of EIV's easy to use API. More info on [EIV's GitHub page](https://github.com/liushmn/ExtendedItemView). Unlike the original mod, this fork provides its sources through [Modrinth Maven](https://support.modrinth.com/en/articles/8801191-modrinth-maven#h_233c0ebd50) so that API Javadocs can be easily used.

## License
[![Code license (MIT)](https://img.shields.io/badge/code%20license-MIT-green.svg?style=flat-square)](github.com/cassiancc/bygone-fortress)

EIV Canary is available under the open source MIT License, matching the original mod.

## Credits
This is a port of [Extended Item View](https://modrinth.com/mod/eiv) to Fabric 1.21.11 that I made for personal use. EIV is available under [MIT License](https://www.curseforge.com/minecraft/mc-mods/extended-itemview-eiv#license), but has not been worked on in two months, and due to changes in 1.21.11, previous versions cannot be compiled against. This fork will be retired if/when 2Bad4Mods returns.


# Developer Guide

## Adding the depedency
```gradle
//Taken from https://support.modrinth.com/en/articles/8801191-modrinth-maven
repositories {
    exclusiveContent {
        forRepository {
            maven {
                name = "Modrinth"
                url = "https://api.modrinth.com/maven"
            }
        }
        filter {
            includeGroup "maven.modrinth"
        }
    }
}

dependencies {
	// Fabric and Architectury Loom
	modImplementation "maven.modrinth:eiv-canary:${eiv_version}+${minecraft_version}-fabric"

	// NeoForge and Multiloader Template
	implementation "maven.modrinth:eiv-canary:${eiv_version}+${minecraft_version}-neoforge"
}
```

## Creating your mod's integration

Before you can implement your own recipes you first have to create an eiv-integration for your Mod.
This is done by creating a class implementing `IExtendedItemViewIntegration`:

```java
public class EivIntegration implements IExtendedItemViewIntegration {
    
    @Override
    public void onIntegrationInitialize() {
        
    }
    
}
```

Don't forget to add it as an entrypoint to your mod

### Fabric (fabric.mod.json)
```json
{
...,
	"entrypoints": {
    ...,
		"eiv": [
			"de.you.modid.eiv.EivIntegration"
		]
	},
...
}
```

### NeoForge (neoforge.mods.toml)
```toml
# ...
eiv="de.you.modid.eiv.EivIntegration"
# ...
```

## Adding a new recipe type

Since you want to add a complete new way of crafting, you first need to create your viewtype.
Simply create a class implementing `IEivRecipeViewType` and override the required methods:

```java
public class YourCustomViewType implements IEivRecipeViewType {

    //Create an instance of your viewtype here
    //Relevant for next steps
    protected static final YourCustomViewType INSTANCE = new YourCustomViewType();
    
    
    @Override
    public Component getDisplayName() {
        return Component.literal("YourCustomViewType"); //This is the name of your viewtype displayed later in the recipe-view
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
        return Identifier.fromNamespaceAndPath("yourmodid", "path/to/your/texture"); //Your type's gui texture
    }

    @Override
    public int getSlotCount() {
        return 0; //The amount of slots one of your type's recipes requires (all slots including results)
    }

    @Override
    public void placeSlots(RecipeViewMenu.SlotDefinition slotDefinition) {
        //Tell EIV where your slots are located by calling slotDefinition.addItemSlot();
        //NOTE: Slot position is relative to your gui texture

        slotDefinition.addItemSlot(0, 10, 20);
        slotDefinition.addItemSlot(1, 40, 20);

    }

    @Override
    public Identifier getId() {
        return Identifier.fromNamespaceAndPath("yourmodid", "your_type_id"); //A unique id for your viewtype
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

Now you need to add your recipe's class to tell EIV about things like rendering & items.
Just create a class implementing `IEivViewRecipe` and override the required methods.

```java
public class YourCustomViewRecipe implements IEivViewRecipe {

    private final SlotContent input, output;


    //You can design your constructor to suit your needs
    public YourCustomViewRecipe(ItemStack input, ItemStack output) {

        //Define your inputs and outputs here

        this.input = SlotContent.of(input);
        this.output = SlotContent.of(output);

    }

    @Override
    public IEivRecipeViewType getViewType() {
        return YourCustomViewType.INSTANCE; //Here you need your type's instance you created before
    }

    @Override
    public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {

        //Tell EIV which SlotContent belongs to which of your previously defined slots
        slotFillContext.bindSlot(0, this.input);
        slotFillContext.bindSlot(1, this.output);

        //When you want to add custom information to some of your items simply add a stack modifier to the corresponding slots
        slotFillContext.addAdditionalStackModifier(0, (stack, tooltip) -> {
            tooltip.add(Component.literal("A cool item"));
        });

        //You can also bind a slot as "optional" and provide it with a valid SlotRenderer to ensure a slot is
        //only rendered if there's an item in it
	//The default SlotRenderer is used for rendering minecraft's default slot texture
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

In EIV everything concerning recipe content is handled via a class called `SlotContent`.
It is a representation of all itemstacks a slot holds. The content is constantly ticked while a the player is looking at a recipe to achieve an overview over the possible in- & outputs.
To wrap your ingredients and results (items, itemstacks, list of items, ...) just call `SlotContent.of();`

### Slot dependencies

If there is a slot that should not tick independantly you can bind it as a dependant slot:

```java
    @Override
    public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {

        //...

        slotFillContext.bindDepedantSlot(0, this.input::index, this.input2);

    }
```

The method requires an integer supplier which tells the `SlotContent` at which position it is currently ticking.
In this case we are using the current index of `this.input` as the the index for `this.input2`. So they are always synchronized.

## Server side recipe representation

Since the significant change of Minecraft's recipe system with the 1.21.3 update, recipes are handled serverside and the client is not told which recipes exist.
Unfortunately, we need the recipes clientside, so we have to synchronize the recipes between the server and client ourselves.
For consistency, EIV requires a serverside representation of all recipes regardless whether it's a mod or vanilla recipe.

Creating a serverside representation of your mod recipes is quite easy, simply create a class that implements `IEivServerRecipe` and override the methods:

```java
public class YourServerRecipe implements IEivServerRecipe {


    //Create a server recipe type (the id does not have to match your client side viewtype id)
    public static final ModRecipeType<YourServerRecipe> TYPE = ModRecipeType.register(
            Identifier.fromNamespaceAndPath("yourmodid", "your_recipe_id"),
            () -> new YourServerRecipe()
    );

    @Override
    public void writeToTag(CompoundTag tag) {

    }

    @Override
    public void loadFromTag(CompoundTag tag) {

    }

    @Override
    public ModRecipeType<? extends IEivServerModRecipe> getRecipeType() {
        return TYPE;
    }
}
```
**INFO**: There's a class called `EivTagUtil` that provides a lot of helper functions for en- and decoding different objects. Note that `writeToTag` is called on the server, while `loadFromTag` is called on the client.

## Register your recipes

Registering your recipes requires you to call 2 methods in your `onIntegrationInitialize();` method:

- `ItemView.addRecipeProvider();`
- `ItemView.registerRecipeWrapper();`

```java
public class EivIntegration implements IExtendedItemViewIntegration {

    @Override
    public void onIntegrationInitialize() {

        //For the server 
        ItemView.addRecipeProvider(list -> {
            //Here you can add all your server recipes
        });

        //For the client
        ItemView.registerRecipeWrapper(YourServerRecipe.TYPE, modRecipe -> {
            
            //Here you tell EIV how to process incoming server recipes
            //Requires you to return a list of client-side view-recipes (IEivViewRecipe)
            
            return List.of();
        });

    }

}
```

Recipe providers registered by `ItemView.addRecipeProvider();` are used by the server recipe manager to maintain and update the recipe cache.
Whenever there is an update, the client is informed about the update and the mod recipe wrappers registered by `ItemView.registerRecipeWrapper();` are used to convert incoming server recipes into displayable view-recipes.

### Stack-Sensitives

Stack-Sensitives are "item-variants" that only differ in their itemstacks' components.<br>
Vanilla examples are: _Enchanted Books, Potions, Tipped Arrows..._<br>
<br>
If you want to add your own item stacks to the ItemView-overlay simply call `ItemView.addStackSensitive();` in a reload callback (`ItemView.addReloadCallback();`).<br>
<br>
You can also exclude items from the overlay by calling `ItemView.excludeItem();` This method does not need to be called in a reload callback, since it's only client-side.

## Conclusion

And there you go! Just reproduce these steps for each of your recipe types and you'll be fine.
Note: You can always look at EIV's builtin code, to see how everything works in practice.
If you now want to create item-transfer functionality, read the section below.

## Adding recipe-transfer functionality

To be able to shift items from the players inventory into it's crafting gui you have to override a few more methods of your class that implements `IEivViewRecipe`:

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

The `ItemView` class is the main API class for EIV, so you can always look in there if you wonder whether something can be realized with EIV or not (yet).<br>
This fork is distributed alongside its source code on Modrinth maven, so its JavaDocs are visible in your IDE as well as here on GitHub.<br>
<br>
If you still have questions, you can always contact me via [Discord](https://discord.cassian.cc)<br>
<br>
Have fun modding!