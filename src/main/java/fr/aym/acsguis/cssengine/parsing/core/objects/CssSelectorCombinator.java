package fr.aym.acsguis.cssengine.parsing.core.objects;

public enum CssSelectorCombinator {
    ENUM (','),
    PLUS ('+'),
    GREATER ('>'),
    TILDE ('~'),
    BLANK (' ');

    private final char letter;

    CssSelectorCombinator(char letter) {
        this.letter = letter;
    }

    public char getLetter() {
        return letter;
    }
}
