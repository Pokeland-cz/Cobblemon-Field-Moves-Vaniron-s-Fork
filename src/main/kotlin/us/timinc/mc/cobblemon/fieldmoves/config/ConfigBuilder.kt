package us.timinc.mc.cobblemon.fieldmoves.config

import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import net.fabricmc.loader.api.FabricLoader
import us.timinc.mc.cobblemon.fieldmoves.CobblemonFieldMoves.LOGGER
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.reader
import kotlin.io.path.writeText

object ConfigBuilder {
    private val gson = GsonBuilder()
        .disableHtmlEscaping()
        .setPrettyPrinting()
        .create()

    /**
     * Reads `config/<name>.json` (defaults if missing), sanitises it and writes it back so new keys
     * appear. Fails without touching the file if it can't be parsed, so a typo never wipes settings.
     */
    fun tryLoad(name: String): Result<Config> = runCatching {
        val file = FabricLoader.getInstance().configDir.resolve("$name.json")
        val config = if (file.exists()) {
            file.reader().use { gson.fromJson(it, Config::class.java) } ?: Config()
        } else {
            Config()
        }.sanitize()

        try {
            val text = config.toCommentedJson(gson::toJson)
            if (!file.exists() || file.readText() != text) {
                file.parent.createDirectories()
                file.writeText(text)
            }
        } catch (e: Exception) {
            LOGGER.warn("Failed to write $file", e)
        }
        config
    }

    /** Startup variant: falls back to defaults (without overwriting the file) on failure. */
    fun load(name: String): Config = tryLoad(name).getOrElse {
        logFailure("Failed to read config/$name.json, using defaults (file left untouched)", it)
        Config().sanitize()
    }

    /** Short, human-readable reason, e.g. "Expected ':' at line 5 column 4 path $.foo". */
    fun describe(e: Throwable): String = (e.cause?.takeIf { e is JsonParseException } ?: e).message ?: e.javaClass.simpleName

    /** Syntax errors are user typos: log one line. Anything else gets a full stack trace. */
    fun logFailure(msg: String, e: Throwable) {
        if (e is JsonParseException) LOGGER.error("$msg: ${describe(e)}")
        else LOGGER.error(msg, e)
    }
}
