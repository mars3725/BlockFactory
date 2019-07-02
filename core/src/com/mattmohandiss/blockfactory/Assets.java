package com.mattmohandiss.blockfactory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import static com.badlogic.gdx.graphics.Color.*;

/**
 * Created by mattm on 2/11/2017.
 */
class Assets {
    static Texture tile = new Texture("tile.png");
    static Texture spawner = new Texture("spawner.png");
    static Texture blocked = new Texture("blocked.png");
    static Texture receiver = new Texture("receiver.png");
    static Texture block = new Texture("block.png");

    static Texture bomb1 = new Texture("bomb1.png");
    static Texture bomb2 = new Texture("bomb2.png");
    static Texture bomb3 = new Texture("bomb3.png");
    static Texture bomb4 = new Texture("bomb4.png");
    static Texture bomb5 = new Texture("bomb5.png");
    static Texture bomb6 = new Texture("bomb6.png");
    static Texture bomb7 = new Texture("bomb7.png");

    static Texture circle = new Texture("circle.png");
    static Texture triangle = new Texture("triangle.png");
    static Texture star = new Texture("star.png");
    static Texture square = new Texture("square.png");

    static TextureRegion pause = new TextureRegion(new Texture("pause.png"));
    static TextureRegion exit = new TextureRegion(new Texture("exit.png"));
    static TextureRegion edit = new TextureRegion(new Texture("edit.png"));

    static NinePatch switchBackground = new NinePatch(new Texture("switchBackground.png"), 60, 60, 60, 60);
    static Texture lever = new Texture("lever.png");
    static TextureRegionDrawable pauseButtonDrawable = new TextureRegionDrawable(pause);
    private static Texture characters = new Texture("font.png");
    static Label.LabelStyle textLabelStyle = new Label.LabelStyle(Assets.getFont(1.25f, WHITE), WHITE);
    static Label.LabelStyle headerLabelStyle = new Label.LabelStyle(Assets.getFont(1, BLACK), BLACK);
    static Label.LabelStyle titleLabelStyle = new Label.LabelStyle(Assets.getFont(1.75f, GRAY), GRAY);
    private static NinePatch patch = new NinePatch(new Texture("button.png"), 14, 14, 14, 14);
    static TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle(new NinePatchDrawable(Assets.patch), new NinePatchDrawable(Assets.patch).tint(Color.DARK_GRAY), new NinePatchDrawable(Assets.patch).tint(Color.DARK_GRAY), Assets.getFont(0.75f, Color.WHITE));
    static Window.WindowStyle windowStyle = new Window.WindowStyle(Assets.getFont(1, BLACK), BLACK, new NinePatchDrawable(Assets.patch));

    static BitmapFont getFont(float scale, Color color) {
        BitmapFont font = new BitmapFont(Gdx.files.internal("font.fnt"), new TextureRegion(characters));
        font.getData().setScale(scale);
        font.setColor(color);
        return font;
    }
}
