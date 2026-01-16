package com.zottik.hytale.command;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.ParseResult;
import com.hypixel.hytale.server.core.command.system.arguments.types.SingleArgumentType;

/**
 * Argument type for parsing show-player flag (true/false).
 */
public class ShowPlayerArgumentType extends SingleArgumentType<Boolean> {

    public ShowPlayerArgumentType() {
        super("show-player", "Show player model in freecam (true/false)");
    }

    @Override
    public Boolean parse(String input, ParseResult result) {
        String lower = input.toLowerCase();
        if (lower.equals("true") || lower.equals("yes") || lower.equals("1") || lower.equals("on")) {
            return true;
        } else if (lower.equals("false") || lower.equals("no") || lower.equals("0") || lower.equals("off")) {
            return false;
        } else {
            result.fail(Message.raw("Show-player must be true or false."));
            return null;
        }
    }
}
