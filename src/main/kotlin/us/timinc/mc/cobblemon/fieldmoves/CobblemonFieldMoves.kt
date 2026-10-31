package us.timinc.mc.cobblemon.fieldmoves

import com.cobblemon.mod.common.api.spawning.spawner.PlayerSpawnerFactory
import net.fabricmc.api.ModInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import us.timinc.mc.cobblemon.fieldmoves.commands.FieldMovesCommand
import us.timinc.mc.cobblemon.fieldmoves.config.Config
import us.timinc.mc.cobblemon.fieldmoves.config.ConfigBuilder
import us.timinc.mc.cobblemon.fieldmoves.listeners.FieldMoveCaptureListener
import us.timinc.mc.cobblemon.fieldmoves.listeners.FieldMoveSpawnInfluence

object CobblemonFieldMoves : ModInitializer {
    const val MOD_ID = "field_moves"
    val LOGGER: Logger = LoggerFactory.getLogger(MOD_ID)

    @Volatile
    lateinit var config: Config
        private set

    override fun onInitialize() {
        config = ConfigBuilder.load(MOD_ID)

        // Both hooks are always registered; each one only acts when config.triggerMode matches,
        // so the trigger can be switched with /fieldmoves reload.
        PlayerSpawnerFactory.influenceBuilders.add(::FieldMoveSpawnInfluence)
        FieldMoveCaptureListener.register()
        FieldMovesCommand.register()

        LOGGER.info("Field abilities apply on: ${config.trigger}")
    }

    /** Re-reads the config file; keeps the current config on failure. */
    fun reload(): Result<Config> = ConfigBuilder.tryLoad(MOD_ID)
        .onSuccess {
            config = it
            LOGGER.info("Config reloaded, field abilities apply on: ${it.trigger}")
        }
        .onFailure { ConfigBuilder.logFailure("Failed to reload config/$MOD_ID.json", it) }

    fun debug(msg: () -> String) {
        if (config.debug) LOGGER.info(msg())
    }
}
