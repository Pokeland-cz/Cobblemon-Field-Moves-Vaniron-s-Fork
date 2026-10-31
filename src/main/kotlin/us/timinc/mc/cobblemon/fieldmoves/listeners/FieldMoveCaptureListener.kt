package us.timinc.mc.cobblemon.fieldmoves.listeners

import com.cobblemon.mod.common.api.Priority
import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.api.events.pokemon.PokemonCapturedEvent
import us.timinc.mc.cobblemon.fieldmoves.CobblemonFieldMoves.config
import us.timinc.mc.cobblemon.fieldmoves.CobblemonFieldMoves.debug
import us.timinc.mc.cobblemon.fieldmoves.abilities.FieldAbilities
import us.timinc.mc.cobblemon.fieldmoves.config.Trigger

/** Applies field abilities to Pokémon at the moment they are caught (trigger = "capture"). */
object FieldMoveCaptureListener {
    fun register() {
        CobblemonEvents.POKEMON_CAPTURED.subscribe(Priority.NORMAL, ::handleCapture)
    }

    private fun handleCapture(event: PokemonCapturedEvent) {
        if (config.triggerMode != Trigger.CAPTURE) return
        val captured = event.pokemon
        val leader = FieldAbilities.leaderOf(event.player) ?: return

        FieldAbilities.rollSynchronize(leader)?.let { nature ->
            debug { "Synchronize: caught ${captured.species.name} nature -> ${nature.name.path}" }
            captured.nature = nature
        }
        FieldAbilities.rollCuteCharm(leader, captured.species)?.let { gender ->
            debug { "Cute Charm: caught ${captured.species.name} gender -> $gender" }
            captured.gender = gender
        }
    }
}
