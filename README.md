# TFC Trotting Wagons

Compatibility addon for Trotting Wagons Extended on TerraFirmaCraft. Version 1.0.0.

Minecraft 1.21.1, NeoForge 21.1.248.

## Required

- Trotting Wagons Extended 2.0.0 or newer. That jar brings GeckoLib 4.8.3 and BoneHitboxLib 0.3.0, so those two stay in the mods folder.
- TerraFirmaCraft 4.2.6 or newer on the 1.21.1 line. The two camels were added in that version.

## Optional

- TFC More Items (`tfc_items`) 1.2.1 or newer. When it is installed, its long stick, nails, screws, and the other parts are used. When it is not, this jar registers those same ids and lists them on a TFC Parts creative tab so JEI can show them.
- FirmaLife. Stainless steel nails and screws then repair wheels the same way black steel does.
- ArborFirmaCraft (`afc`) 2.0.0 or newer. Its lumber, axles, and gear boxes count where the TFC wood parts do.

The game still loads without the optional mods.

## What it does

TFC horses, donkeys, mules, and both camels can pull a wagon. A TFC animal hitches only when its familiarity heart has the white or red outline. That is the familiarity decay limit, 30% unless the TFC config changes it. A vanilla horse still has to be tamed. Babies are refused. The horse, mule, and both camels take a large passenger seat. The donkey stays a small passenger.

Recipes and the in-world build use TFC materials: lumber, planks, wrought iron, steel, bronze (including bismuth bronze and black bronze), brass mechanisms, gear boxes, rope, cloth, and glue. A finished TFC lamp fits a lantern slot. Wheels are crafted at zero durability and can be repaired. A wheel at zero durability stays fitted and stops the wagon.

A barrel placed on the barrel wagon does not open item slots. That screen is one row of twelve barrel slots. The log wagon holds 1760 items. It accepts TFC logs, wood, stripped wood, stripped logs, lumber, planks, sticks, and wooden supports.

## Develop

Java 21. `gradlew.bat jar` builds the mod. Put the jar in `mods` next to Trotting Wagons Extended 2.0.0 and TerraFirmaCraft. Compiling needs BoneHitboxLib on the classpath, because the wagon classes implement one of its types. That library is not a runtime dependency of this jar.
