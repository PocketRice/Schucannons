package io.github.pocketrice;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;
import io.github.pocketrice.Prysm.Rigidbody;
import lombok.Getter;
import net.mgsx.gltf.loaders.gltf.GLTFLoader;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.DoubleStream;

import static io.github.pocketrice.AnsiCode.*;

@Getter
public class BotPlayer extends Player {
    int difficulty;
    @Getter
    boolean isDummy;

    public BotPlayer() {
        this(0, false);
    }

    public BotPlayer(int d, boolean dummy) {
        difficulty = d;
        isDummy = dummy;
        Model model = new GLTFLoader().load(Gdx.files.internal("models/schucannon.gltf")).scene.model;
        health = 3;
        rb = new Rigidbody(model);
        playerId = UUID.randomUUID();
        playerName = weightedRandom(new String[]{"Notbot", "Anna", "Heavy Weapons Guy", "Scott", "Jianyao", "Wil", "Mundy", "Lando", "Vinny", "Shogo", "Wario", "Lyra", "Ado", "Hal", "Mark", "Bird", "Korb", "Minton", "Lorry", "Heathcliff", "Gilbert", "The Legend", "Plonko", "Plinko", "Hubert", "Pauling"}, new double[0], true);

    }



    public Color generateRandomColor() {
        return new Color((float)(Math.random() * 255), (float)(Math.random() * 255), (float)(Math.random() * 255), 1f);
    }



    @Override
    public void deductHealth() {
        health--;
    }

    @Override
    public void requestProjVector() throws InterruptedException {
        // FIXME please :)
        fancyDelay(500, "Thinking...", "Got it!", 4);
        projVector = new Vector3(proximityRandom(5f, 3f,4f), proximityRandom(8f, 4f,2f), proximityRandom(5f, 3f,4f));
    }

    @Override
    public String toString() {
        return "BOT " + (playerName.isEmpty() ? playerId : playerName);
    }

    public static <T> T weightedRandom(T[] choices, double[] weights, boolean autoEqualize)
    {
        double rng = Math.random();

        if (autoEqualize) {
            weights = new double[choices.length];
            Arrays.fill(weights, 1.0 / choices.length);
        }

        assert (DoubleStream.of(weights).sum() != 1) : "WeightedRandom weights do not add up to 1 (= " + DoubleStream.of(weights).sum() + ")!";
        assert (choices.length == weights.length) : "WeightedRandom choice (" + choices.length + ") and weights (" + weights.length + ") arrays are not same length!";

        for (int i = 0; i < weights.length; i++) {
            if (rng < weights[i])
                return choices[i];
            else
                rng -= weights[i];
        }

        return choices[choices.length-1];
    }

    public static float proximityRandom(float base, float lowerOffset, float upperOffset) { // <+> APM
        return (float) (Math.random() * (lowerOffset + upperOffset) + base - lowerOffset);
    }

    public static void fancyDelay(long delay, String loadMessage, String completionMessage, int iterations) throws InterruptedException { // Yoinked from SchudawgCannoneer
        int recursionCount = 0;
        System.out.print(loadMessage + " /");

        while (recursionCount < iterations) {
            TimeUnit.MILLISECONDS.sleep(delay);
            System.out.print("\b—");
            TimeUnit.MILLISECONDS.sleep(delay);
            System.out.print("\b\\");
            TimeUnit.MILLISECONDS.sleep(delay);
            System.out.print("\b|");
            TimeUnit.MILLISECONDS.sleep(delay);
            System.out.print("\b/");
            recursionCount++;
        }
        if (!completionMessage.isBlank()) System.out.print("\b" + completionMessage + "\n" + ANSI_RESET);
        else System.out.println();
    }
}
