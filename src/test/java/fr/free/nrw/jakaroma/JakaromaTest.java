package fr.free.nrw.jakaroma;

import org.junit.Assert;
import org.junit.Test;

public class JakaromaTest {

    @Test
    public void translate() {
        String[] testInput = {"もらった", "ホッ", "すごっ", "ピッザ"};

        Assert.assertEquals("Moratta ", Jakaroma.convert(testInput[0]));
        Assert.assertEquals("Ho! ", Jakaroma.convert(testInput[1]));
        Assert.assertEquals("Sugo! ", Jakaroma.convert(testInput[2]));
        Assert.assertEquals("Pizza ", Jakaroma.convert(testInput[3]));
    }

    @Test
    public void notTranslateForEnglish() {
        String[] testInput = {"cat", "dog", "Hello"};

        Assert.assertEquals("Cat ", Jakaroma.convert(testInput[0]));
        Assert.assertEquals("Dog ", Jakaroma.convert(testInput[1]));
        Assert.assertEquals("Hello ", Jakaroma.convert(testInput[2]));
    }
}
