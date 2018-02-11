package org.team2851.util;

public class LatchedBoolean
{
    private boolean isTrue = false;
    public LatchedBoolean() {}

    public boolean getValue(boolean in)
    {
        if (!isTrue && in) {
            isTrue = true;
            return true;
        } else if (isTrue && !in) {
            isTrue = false;
        }
        return false;
    }
}
