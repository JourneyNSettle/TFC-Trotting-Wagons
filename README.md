# TFC Trotting Wagons

Private compatibility addon. It lets TerraFirmaCraft livestock pull wagons from Trotting Wagons Extended.

Minecraft 1.21.1, NeoForge 21.1.248. The mod requires both:

- Trotting Wagons Extended, a build that reads the entity tag `trotting_wagons:draft_animals`
- TerraFirmaCraft 4.2.6 or newer on the 1.21.1 line, because that is when the two camels were added

## What it does

This jar adds these ids to `trotting_wagons:draft_animals`:

- `tfc:horse`
- `tfc:donkey`
- `tfc:mule`
- `tfc:dromedary_camel`
- `tfc:bactrian_camel`

Babies are still refused by the wagon mod. Hitch width, camel size, and other TFC behaviour are not handled yet.

## Develop

Java 21. `gradlew.bat compileJava` compiles the mod. Put the built jar in `mods` next to Trotting Wagons Extended and TerraFirmaCraft.
