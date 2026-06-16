package com.rifledluffy.chairs.messages;

import org.intellij.lang.annotations.Subst;

public enum PlaceHolder {
    ARGUMENT("argument"),
    PLAYER("player"),
    SEATED("seated");

    private final String placeholder;

    PlaceHolder(String placeholder) {
        this.placeholder = placeholder;
    }

    /**
     * Since this will be used in Mini-messages placeholder only the pattern "[!?#]?[a-z0-9_-]*" is valid.
     * if used inside an unparsed text you have to add surrounding <> yourself.
     */
    @Subst("name") // substitution; will be inserted if the IDE/compiler tests if input is valid.
    public String getPlaceholder() {
        return placeholder;
    }
}