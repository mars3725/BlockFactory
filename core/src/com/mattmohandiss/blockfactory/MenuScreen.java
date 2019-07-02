package com.mattmohandiss.blockfactory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import static com.badlogic.gdx.graphics.Color.RED;

/**
 * Created by mattm on 2/20/2017.
 */
class MenuScreen implements Screen {
    private Stage stage = new Stage(new ScreenViewport());
    private Actor lever;

    MenuScreen(final GameStarter starter) {
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        final Label titleLabel = new Label("Block Factory", Assets.titleLabelStyle);
        table.add(titleLabel).pad(50).center().row();

        Image switchBackground = new Image(Assets.switchBackground);
        table.add(switchBackground).growY().pad(50).center();
        table.validate();
        lever = new Actor() {
            @Override
            public void draw(Batch batch, float parentAlpha) {
                batch.draw(Assets.lever, getX(), getY(), getWidth(), getHeight());
            }
        };
        lever.setSize(switchBackground.getWidth() * 2, switchBackground.getHeight() / 6);
        final float minLeverPos = switchBackground.getY() + Assets.switchBackground.getBottomHeight() * 0.5f;
        final float maxLeverPos = switchBackground.getY() + switchBackground.getHeight() - Assets.switchBackground.getTopHeight() * 0.5f - lever.getHeight();
        lever.setPosition(stage.getWidth() / 2 - lever.getWidth() / 2, minLeverPos);

        stage.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return (event.getTarget() == lever);
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                lever.setY(MathUtils.clamp(event.getStageY(), minLeverPos, maxLeverPos));
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (lever.getY() == maxLeverPos) {
                    titleLabel.setColor(RED);
                    stage.getRoot().addAction(Actions.sequence(Actions.delay(0.5f), Actions.fadeOut(0.25f), Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            starter.setScreen(new PlayScreen(starter, Math.min(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()) / 4));
                        }
                    })));
                }
            }
        });
        stage.addActor(lever);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getCamera().viewportWidth = width;
        stage.getCamera().viewportHeight = height;

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
