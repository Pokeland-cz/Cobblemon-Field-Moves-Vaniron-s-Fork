package us.timinc.mc.cobblemon.fieldmoves.config

import us.timinc.mc.cobblemon.fieldmoves.CobblemonFieldMoves.LOGGER

enum class Trigger { SPAWN, CAPTURE }

class Config {
    /**
     * When Synchronize / Cute Charm apply:
     * - "spawn": to wild Pokémon as they spawn near the player (mainline behaviour;
     *   does not cover fishing, bait or Poké Snack spawns)
     * - "capture": to the Pokémon at the moment it is caught (covers every encounter type)
     */
    var trigger: String = "spawn"
    var cuteCharmChance: Double = 2.0 / 3.0
    var synchronizeChance: Double = 1.0
    var debug: Boolean = false

    @Transient
    var triggerMode: Trigger = Trigger.SPAWN
        private set

    /** Renders the config as JSON with `//` comments (Gson's lenient parser reads them back). */
    fun toCommentedJson(json: (Any) -> String): String = """
        |// Cobblemon Field Moves config. Apply changes with /fieldmoves reload (ops only).
        |// Comments are regenerated on every load; values are kept.
        |{
        |  // When Synchronize / Cute Charm take effect. The ability must be on the FIRST party slot.
        |  //   "spawn"   - wild Pokémon are changed as they spawn near the player (like the main games).
        |  //               Spawns that already fix a nature/gender (datapacks, events) are left alone.
        |  //               Does not affect fishing, bait or Poké Snack spawns.
        |  //   "capture" - the Pokémon is changed at the moment it is caught (covers every encounter type,
        |  //               but players see the original gender/nature until the catch).
        |  "trigger": ${json(trigger)},
        |
        |  // Chance (0.0 - 1.0) that Cute Charm makes a wild Pokémon the opposite gender of the leader.
        |  // Genderless and single-gender species are never affected. Mainline: 0.6667 (2/3).
        |  "cuteCharmChance": ${json(cuteCharmChance)},
        |
        |  // Chance (0.0 - 1.0) that Synchronize gives a wild Pokémon the leader's nature.
        |  // Mainline (Gen 8+): 1.0.
        |  "synchronizeChance": ${json(synchronizeChance)},
        |
        |  // Log every Synchronize / Cute Charm activation to the server console.
        |  "debug": ${json(debug)}
        |}
        |""".trimMargin()

    /** Clamp / normalise values a user may have mistyped. */
    fun sanitize(): Config = apply {
        cuteCharmChance = cuteCharmChance.coerceIn(0.0, 1.0)
        synchronizeChance = synchronizeChance.coerceIn(0.0, 1.0)
        triggerMode = Trigger.entries.firstOrNull { it.name.equals(trigger.trim(), ignoreCase = true) }
            ?: Trigger.SPAWN.also { LOGGER.warn("Unknown trigger \"$trigger\", falling back to \"spawn\"") }
        trigger = triggerMode.name.lowercase()
    }
}
