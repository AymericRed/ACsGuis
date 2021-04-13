package fr.aym.acsguis.sqript;

public class NoSqriptSupport implements SqriptSupport
{
    @Override
    public boolean isSqriptLoaded() {
        return false;
    }

    @Override
    public void onCssInit() {}
}
