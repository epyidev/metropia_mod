package fr.epyi.metropiamod.commands.arguments

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import net.minecraft.command.CommandSource
import net.minecraft.util.text.TranslationTextComponent
import java.util.*

class URLArgument : ArgumentType<String> {
    @Throws(CommandSyntaxException::class)
    override fun parse(reader: StringReader): String {
        val url = StringBuilder()
        while (reader.canRead() && reader.peek() != ' ') {
            url.append(reader.read())
            val compareString = url.toString()
            if (!compareString.startsWith(
                    if (compareString.length < HTTPS_START.length) HTTPS_START.substring(
                        0,
                        compareString.length
                    ) else HTTPS_START
                )
            ) {
                throw HTTPS_EXCEPTION.createWithContext(reader)
            }
        }
        return url.toString()
    }

    override fun getExamples(): Collection<String> {
        return EXAMPLES
    }

    companion object {
        private const val HTTPS_START = "https://"

        private val HTTPS_EXCEPTION = SimpleCommandExceptionType(TranslationTextComponent("argument.nohttps"))

        private val EXAMPLES: Collection<String> = Arrays.asList(HTTPS_START, "https://i.imgur.com/mORJxcm.png")


        fun getURL(ctx: CommandContext<CommandSource>, arg: String?): String {
            return ctx.getArgument(arg, String::class.java)
        }

        fun urlArg(): URLArgument {
            return URLArgument()
        }
    }
}