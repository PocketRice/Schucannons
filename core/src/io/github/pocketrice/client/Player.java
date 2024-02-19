package io.github.pocketrice.client;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;


@Getter
public abstract class Player {
    @Setter
    int health = 0;
    @Setter
    Color playerColor;
    @Setter
    Vector3 projVector, pos;
    UUID playerId;
    @Setter
    String playerName;
    @Setter
    boolean isRefused, isReady;


    public abstract void deductHealth();
    public abstract String getIdentifier();
    public abstract void requestProjVector() throws InterruptedException; // the proj vector is CALCULATED (angle is chosen. magnitude of force is clamped 1-3 or chosen, direction is auto-set or chosen. In other words, just angle and 1-3 mag)
    public abstract String toString();
}
