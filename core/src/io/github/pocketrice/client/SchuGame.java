package io.github.pocketrice.client;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.pocketrice.client.screens.MenuScreen;
import io.github.pocketrice.server.DedicatedServer;
import io.github.pocketrice.server.GameServer;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

// TODO: AWFUL memory leaks abound, pls fix

public class SchuGame extends Game {
	public static final int VIEWPORT_WIDTH = 960, VIEWPORT_HEIGHT = 880;
	public static final Fontbook fontbook = Fontbook.of("koholint.ttf", "dina.ttc", "tf2build.ttf", "tf2segundo.ttf", "delfino.ttf", "kyomadoka.ttf", "tinyislanders.ttf", "benzin.ttf", "99occupy.ttf");
	public static final Audiobox audiobox = Audiobox.of(List.of("hint.ogg", "slide_up.ogg", "slide_down.ogg", "notification_alert.ogg", "hitsound.ogg", "duel_challenge.ogg", "buttonclick.ogg", "buttonclickrelease.ogg", "buttonrollover.ogg", "hitsound.ogg", "vote_started.ogg", "dominate.ogg", "revenge.ogg", "aero-seatbelt.ogg"), List.of());

	private Viewport vp;
	private SchuClient sclient;
	@Getter
	private GameRenderer grdr;
	@Getter
	private GameManager gmgr;
	@Getter @Setter
	boolean isDebug;


	@Override
	public void create() {
		vp = new FillViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
		gmgr = new GameManager(this);
		grdr = new GameRenderer(gmgr);

		try {
			// for testing purposes
			GameServer server = new DedicatedServer(3074);
			server.start();

			sclient = new SchuClient(gmgr);
			sclient.start();
			sclient.connect(5000, "localhost", new int[]{3074});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		gmgr.setClient(sclient);
		gmgr.setGrdr(grdr);

		setScreen(new MenuScreen(this));
	}

	@Override
	public void render() {
		screen.render(0f);
	}
	
	@Override
	public void dispose() {
		grdr.dispose();
		screen.dispose();
		sclient.disconnect();
	}
}
