package us.timinc.mc.cobblemon.fieldmoves.abilities

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.pokemon.Gender
import com.cobblemon.mod.common.pokemon.Nature
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.pokemon.Species
import net.minecraft.server.level.ServerPlayer
import us.timinc.mc.cobblemon.fieldmoves.CobblemonFieldMoves.config
import kotlin.random.Random

/** Ability rules shared by the spawn and capture triggers. */
object FieldAbilities {
    private const val SYNCHRONIZE = "synchronize"
    private const val CUTE_CHARM = "cutecharm"

    fun leaderOf(player: ServerPlayer): Pokemon? = Cobblemon.storage.getParty(player).firstOrNull()

    /** The nature to force, or null if the leader lacks Synchronize or the roll failed. */
    fun rollSynchronize(leader: Pokemon): Nature? {
        if (leader.ability.name != SYNCHRONIZE) return null
        if (Random.nextDouble() >= config.synchronizeChance) return null
        return leader.nature
    }

    /** The gender to force on [species], or null if Cute Charm doesn't apply or the roll failed. */
    fun rollCuteCharm(leader: Pokemon, species: Species): Gender? {
        if (leader.ability.name != CUTE_CHARM) return null
        val target = when (leader.gender) {
            Gender.MALE -> Gender.FEMALE
            Gender.FEMALE -> Gender.MALE
            else -> return null
        }
        // Genderless / single-gender species can't be flipped.
        if (target !in species.possibleGenders) return null
        if (Random.nextDouble() >= config.cuteCharmChance) return null
        return target
    }
}
