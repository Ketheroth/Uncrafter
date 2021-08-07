# Uncrafter

---

Uncrafter is a Minecraft Forge mod adding a block capable of uncrafting items.

Version : 1.16

---

CurseForge : [Project Link](https://www.curseforge.com/minecraft/mc-mods/uncrafter)

---

## For mod developers

You can send IMC messages to this mod to disable recipes in the uncrafter (meaning the recipe can't be inverted).  

The method the mod is awaiting is `blacklistedRecipes`, and the supplier should return a list of strings (a string being a recipe id).

Example :
```java
InterModComms.sendTo("uncrafter", "blacklistedRecipes", () -> List.of("modid:recipe_for_something"));
```
