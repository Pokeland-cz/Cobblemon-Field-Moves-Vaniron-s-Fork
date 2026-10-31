package us.timinc.mc.cobblemon.fieldmoves.listeners

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.api.spawning.detail.PokemonSpawnAction
import com.cobblemon.mod.common.api.spawning.detail.SpawnAction
import com.cobblemon.mod.common.api.spawning.influence.SpawningInfluence
import com.cobblemon.mod.common.pokemon.Species
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import us.timinc.mc.cobblemon.fieldmoves.CobblemonFieldMoves.config
import us.timinc.mc.cobblemon.fieldmoves.CobblemonFieldMoves.debug
import us.timinc.mc.cobblemon.fieldmoves.abilities.FieldAbilities
import us.timinc.mc.cobblemon.fieldmoves.config.Trigger

/** Applies field abilities to wild Pokémon before they are created (trigger = "spawn"). */
class FieldMoveSpawnInfluence(private val player: ServerPlayer) : SpawningInfluence {
    override fun affectAction(action: SpawnAction<*>) {
        if (config.triggerMode != Trigger.SPAWN) return
        if (action !is PokemonSpawnAction) return
        val props = action.props
        val leader = FieldAbilities.leaderOf(player) ?: return

        // Respect natures/genders fixed by the spawn definition (datapacks, events, ...).
        if (props.nature == null) {
            FieldAbilities.rollSynchronize(leader)?.let { nature ->
                debug { "Synchronize: wild ${props.species} nature -> ${nature.name.path}" }
                props.nature = nature.name.path
            }
        }

        if (props.gender == null) {
            val species = props.species?.let(::speciesOf) ?: return
            FieldAbilities.rollCuteCharm(leader, species)?.let { gender ->
                debug { "Cute Charm: wild ${species.name} gender -> $gender" }
                props.gender = gender
            }
        }
    }

    private fun speciesOf(name: String): Species? =
        if (':' in name) ResourceLocation.tryParse(name)?.let(PokemonSpecies::getByIdentifier)
        else PokemonSpecies.getByName(name)
}
