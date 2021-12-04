# Uncrafter

---

Uncrafter is a Minecraft Forge mod adding blocks capable of uncrafting items.

Version : 1.16

---

CurseForge : [Project Link](https://www.curseforge.com/minecraft/mc-mods/uncrafter)

---

## For mod developers

You can send IMC messages to this mod to disable items in the uncrafter (meaning this item can't be uncrafted).  

The method the mod is awaiting is `blacklisted-items`, and the supplier should return a list of strings (a string being an item id).

Example :
```java
InterModComms.sendTo("uncrafter", "blacklisted-items", () -> List.of("modid:item"));
```
