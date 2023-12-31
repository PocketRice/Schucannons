package io.github.pocketrice;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import io.github.pocketrice.Prysm.Rigidbody;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;


@Getter
public abstract class Player {

    @Setter
    int health = 0;

    @Setter
    Color playerColor = Color.GRAY;
    @Setter
    Rigidbody rb;

    Vector3 projVector = Vector3.Zero;

    UUID playerId;
    @Setter
    String playerName;
    boolean isRefused;

    public abstract void deductHealth();
    public abstract void requestProjVector() throws InterruptedException; // the proj vector is CALCULATED (angle is chosen. magnitude of force is clamped 1-3 or chosen, direction is auto-set or chosen. In other words, just angle and 1-3 mag)
    public abstract String toString();
}
