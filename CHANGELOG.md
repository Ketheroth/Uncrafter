# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## 1.17.1-0.4.0

## Added
- Allow shift-clicking items in input slot and inventory slots.
Shift-clicking in output/enchantment slots will be enabled when I find a proper way of doing it.

### Fixed
- Unable to get enchanted books after removing every ingredient.
- Enchanted book duplication.
- Ingredients of a shaped recipe have a wrong slot position in the Uncrafter.

## 1.17.1-0.3.0

### Added
- Advanced Uncrafter. Allow three ingredients to be retrieved. Allow three enchantments to be retrieved (as enchanted books), but you can't retrieve a curse.
- When extracting an ingredient from the output slots, the input slot is locked until you have taken all possible ingredients.
A little cross appears on the input slot when it is locked.

### Changed
- Changed the texture for the uncrafter again (it's better now).

### Fixed
- Uncrafter having end stone as bottom texture instead of crying obsidian

## 1.17.1-0.2.0

### Added
- A server configuration to blacklist recipes that shouldn't be inverted.
The default blacklist has some recipes to avoid item duplication.
If you find more recipe that lead to duplication, please tell me, so I can add them to the default blacklist.
- A server configuration to change the max amount of item that can be retrieved while uncrafting an item.
- Inter Mod Communication. A mod can add a list of blacklisted recipes to the uncrafter.

### Changed
- The recipe is now easier
- The texture of the uncrafter has been changed to math the crafting recipe

## 1.17.1-0.1.0

Initial beta release
