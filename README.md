# TFC Trotting Wagons

Private compatibility addon. It lets TerraFirmaCraft livestock pull wagons from Trotting Wagons Extended.

Minecraft 1.21.1, NeoForge 21.1.248. The mod requires:

- Trotting Wagons Extended, a build that reads the entity tag `trotting_wagons:draft_animals`. That mod in turn requires GeckoLib 4.8.3 and BoneHitboxLib 0.3.0, so those jars stay in the mods folder.
- TerraFirmaCraft 4.2.6 or newer on the 1.21.1 line, because that is when the two camels were added
- TFC More Items (`tfc_items`) 1.2.1 or newer, for its long stick and the other parts
- TFC Metal Tools (`tfc_metal_tools`) 1.0.5 or newer

ArborFirmaCraft (`afc` 2.0.0 or newer) is optional. The game still loads without it.

## What it does

This jar adds these ids to `trotting_wagons:draft_animals`:

- `tfc:horse`
- `tfc:donkey`
- `tfc:mule`
- `tfc:dromedary_camel`
- `tfc:bactrian_camel`

A TerraFirmaCraft animal can be hitched only when its familiarity heart has the white or red outline. That is familiarity at or above the decay limit, 30% unless the TFC config changes it. A vanilla horse still has to be tamed. Babies are refused. Hitch width and camel size are not handled yet.

## Develop

Java 21. `gradlew.bat compileJava` compiles the mod. Put the built jar in `mods` next to Trotting Wagons Extended and TerraFirmaCraft.
