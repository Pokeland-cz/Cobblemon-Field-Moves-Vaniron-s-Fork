package us.timinc.mc.cobblemon.fieldmoves.commands

import com.mojang.brigadier.CommandDispatcher
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.ChatFormatting
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands.literal
import net.minecraft.network.chat.Component
import us.timinc.mc.cobblemon.fieldmoves.CobblemonFieldMoves
import us.timinc.mc.cobblemon.fieldmoves.config.ConfigBuilder

object FieldMovesCommand {
    fun register() {
        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ -> register(dispatcher) }
    }

    private fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
            literal("fieldmoves")
                .requires { it.hasPermission(2) }
                .then(literal("reload").executes { ctx -> reload(ctx.source) })
        )
    }

    private fun reload(source: CommandSourceStack): Int =
        CobblemonFieldMoves.reload().fold(
            onSuccess = { c ->
                source.sendSuccess({
                    Component.literal(
                        "Field Moves config reloaded: trigger=${c.trigger}, " +
                            "synchronizeChance=${c.synchronizeChance}, cuteCharmChance=${"%.3f".format(c.cuteCharmChance)}, " +
                            "debug=${c.debug}"
                    )
                }, true)
                1
            },
            onFailure = { e ->
                source.sendFailure(
                    Component.literal("Failed to reload Field Moves config (keeping previous settings): ${ConfigBuilder.describe(e)}")
                        .withStyle(ChatFormatting.RED)
                )
                0
            }
        )
}
