package com.mattmohandiss.blockfactory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import static com.badlogic.gdx.graphics.Color.*;

class PlayScreen implements Screen {
    private Array<Color> allColors = new Array<>(
            new Color[]{RED, MAROON, BLUE, GREEN, BROWN, CYAN,
                    MAGENTA, TEAL, GOLD, ORANGE, ROYAL, PURPLE});
    private Array<Color> colorsInPlay = new Array<>();
    private boolean paused = false;
    private int funds = 1000;
    private Stage stage = new Stage(new ScreenViewport());
    private OrthographicCamera camera = (OrthographicCamera) stage.getCamera();
    private Grid grid;
    private Block selectedBlock;
    private Stage hud = new Stage(new ScreenViewport());
    private Label scoreLabel;
    private GameStarter starter;
    private Button pauseButton;
    private Button editButton;
    private long pauseTime;
    private Label pausedLabel;
    private InputListener resumeTouchListener;
    private InputListener editModeTouchListener;


    PlayScreen(GameStarter starter, int tileSize) {
        this.starter = starter;
        grid = new Grid(this, stage, tileSize);
        createHud();
        createControls();
    }

    private void createHud() {
        Table hudTable = new Table();
        hudTable.setFillParent(true);

        pauseButton = new Button(Assets.pauseButtonDrawable);
        pauseButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (paused) {
                    endGame();
                    return;
                }
                pause();
                pauseButton.setTouchable(Touchable.disabled);
                Timer.instance().scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        pauseButton.setTouchable(Touchable.enabled);
                    }
                }, 1);
            }
        });
        hudTable.add(pauseButton).top().left().pad(50).size(hud.getHeight() / 8);


        scoreLabel = new Label("$" + funds, Assets.headerLabelStyle);
        scoreLabel.setAlignment(Align.center);
        hudTable.add(scoreLabel).expandX().center().padTop(50).top();

        editButton = new Button(new TextureRegionDrawable(Assets.edit));
        editButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (!editButton.isVisible()) return;
                pause();
                hud.removeCaptureListener(resumeTouchListener);
                pausedLabel.setVisible(false);
                editButton.setVisible(false);
                Dialog shop = createShop();
                shop.show(hud);
            }
        });
        hudTable.add(editButton).top().right().pad(50).size(hud.getHeight() / 8).row();

        Label.LabelStyle style = new Label.LabelStyle(Assets.getFont(2, BLACK), BLACK);
        pausedLabel = new Label("Paused", style);
        pausedLabel.setAlignment(Align.center);
        pausedLabel.setVisible(false);
        hudTable.add(pausedLabel).center().expand().colspan(3);

        hud.addActor(hudTable);
    }

    private void createControls() {
        stage.addListener(new InputListener() {
            private Vector3 prevDragPos;
            private int numTouches = 0;

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                selectedBlock = grid.getBlock(MathUtils.floor(event.getStageX() / grid.tileSize), MathUtils.floor(event.getStageY() / grid.tileSize));
                numTouches++;
                return true;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                x = Gdx.input.getX(pointer);
                y = Gdx.input.getY(pointer);
                if (prevDragPos == null) prevDragPos = new Vector3(x, y, 0);
                float deltaX = prevDragPos.x - x;
                float deltaY = y - prevDragPos.y;

                if (selectedBlock == null && numTouches < 2) {
                    camera.position.add(deltaX, deltaY, 0);
                }
                prevDragPos.set(x, y, 0);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                prevDragPos = null;
                numTouches--;
            }
        });

        stage.addListener(new ActorGestureListener() {
            @Override
            public void touchDown(InputEvent event, float x, float y, int pointer, int button) {
                super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void fling(InputEvent event, float velocityX, float velocityY, int button) {
                if (selectedBlock != null && !selectedBlock.hasActions()) {
                    if (Math.abs(velocityX) > Math.abs(velocityY)) {
                        if (velocityX > 0) {
                            selectedBlock.move(Direction.RIGHT);
                        } else {
                            selectedBlock.move(Direction.LEFT);
                        }
                    } else {
                        if (velocityY > 0) {
                            selectedBlock.move(Direction.UP);
                        } else {
                            selectedBlock.move(Direction.DOWN);
                        }
                    }
                }
            }

            @Override
            public void zoom(InputEvent event, float initialDistance, float distance) {
                float ratio = MathUtils.clamp(initialDistance / distance, 0.75f, 1.25f);
                camera.zoom *= ratio;
                camera.zoom = MathUtils.clamp(camera.zoom, 0.75f, 3);
            }
        });

        switch (starter.deviceType) {
            case iOS:
                break;
            case android:
                break;
            case desktop:
                stage.addListener(new InputListener() {
                    @Override
                    public boolean keyTyped(InputEvent event, char character) {
                        switch (character) {
                            case 'w':
                                camera.position.add(0, 5, 0);
                                break;
                            case 'a':
                                camera.position.add(-5, 0, 0);
                                break;
                            case 's':
                                camera.position.add(0, -5, 0);
                                break;
                            case 'd':
                                camera.position.add(5, 0, 0);
                                break;
                            case 'c':
                                Color randomColor = allColors.random();
                                colorsInPlay.add(randomColor);
                                grid.addTileRandomly(TileType.spawner, randomColor);
                                grid.addTileRandomly(TileType.receiver, randomColor);
                                break;
                            case 'g':
                                grid.addTileRandomly(TileType.normal, null);
                                break;
                            case 'p':
                                if (paused) {
                                    resume();
                                } else {
                                    pause();
                                }
                                break;
                            case 'b':
                                grid.spawnComponent(colorsInPlay.random());
                                break;
                            case 'k':
                                break;
                            case '-':
                                camera.zoom += 0.25;
                                camera.zoom = MathUtils.clamp(camera.zoom, 0.75f, 3);
                                break;
                            case '=': //+
                                camera.zoom -= 0.25;
                                camera.zoom = MathUtils.clamp(camera.zoom, 0.75f, 3);
                                break;
                            case 'q':
                                endGame();
                        }
                        return true;
                    }
                });
                break;
        }
    }

    @Override
    public void show() {
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(hud);
        multiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(multiplexer);
        SequenceAction gridSpawn = loadGridSpawnAction(MathUtils.random(3, 5), MathUtils.random(1, 2));
        gridSpawn.addAction(Actions.run(new Runnable() {
            @Override
            public void run() {
                startGame();
            }
        }));
        stage.addAction(gridSpawn);
    }

    private Dialog createShop() {
        Dialog shopWindow = new Dialog("", Assets.windowStyle) {
            InputListener exitListener = new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    Vector2 vec = hud.screenToStageCoordinates(new Vector2(x, y));
                    Rectangle rectangle = new Rectangle(getX(), getY(), getWidth(), getHeight());
                    boolean touchedShop = rectangle.contains(vec.x, vec.y);
                    if (!touchedShop) {
                        hide();
                        hud.removeCaptureListener(this);
                        editButton.setVisible(true);
                        resume();
                        return true;
                    }
                    return false;
                }
            };

            @Override
            protected void result(Object object) {
                shopItem item = ((shopItem) object);
                changeFunds(-item.price);
                enterEditMode(item);
            }

            @Override
            public Dialog show(Stage stage) {
                hud.addCaptureListener(exitListener);
                return super.show(stage);
            }

            @Override
            public void hide() {
                super.hide();
                hud.removeCaptureListener(exitListener);
            }
        };

        Array<shopItem> shopItems = new Array<>(new shopItem[]{
                new shopItem(100, "Expand Floor"),
                new shopItem(150, "Rush Block"),
                new shopItem(150, "Fast Block"),
                new shopItem(250, "Production Skip"),
                new shopItem(500, "Rush Components"),
                new shopItem(1000, "Production Control")});

        Label title = new Label("Factory Shop", Assets.headerLabelStyle);
        title.setAlignment(Align.center);
        shopWindow.getContentTable().add(title).expandX();
        for (shopItem item :
                shopItems) {
            TextButton button = new TextButton(item.name + "   $" + item.price, Assets.textButtonStyle);
            if (item.price > funds) {
                button.setTouchable(Touchable.disabled);
                button.getLabel().setColor(Color.FIREBRICK);
            }
            shopWindow.button(button, item);
            shopWindow.getButtonTable().row();
        }

        return shopWindow;
    }

    void changeFunds(int amount) {
        funds += amount;
        scoreLabel.setText("$" + funds);
    }

    private void startGame() {
        stage.addAction(Actions.forever(Actions.delay(MathUtils.random(10, 20), Actions.run(new Runnable() {
            @Override
            public void run() {
                if (MathUtils.random(funds / 50) == 0) {
                    Color randomColor = allColors.pop();
                    colorsInPlay.add(randomColor);
                    grid.addTileRandomly(TileType.spawner, randomColor);
                    grid.addTileRandomly(TileType.receiver, randomColor);
                } else {
                    grid.addTileRandomly(TileType.normal, null);
                }
            }
        }))));

        stage.addAction(Actions.forever(Actions.delay(MathUtils.random(2, 5), Actions.run(new Runnable() {
            @Override
            public void run() {
                grid.spawnComponent(colorsInPlay.random());
            }
        }))));
    }

    void endGame() {
        SequenceAction fade = Actions.sequence(Actions.fadeOut(0.75f), Actions.run(new Runnable() {
            @Override
            public void run() {
                starter.setScreen(new MenuScreen(starter));
            }
        }));
        hud.getRoot().addAction((Actions.fadeOut(0.75f)));
        stage.getRoot().addAction(fade);
    }

    private SequenceAction loadGridSpawnAction(int tileCount, int spawnerCount) {
        grid.addTile(0, 0);
        grid.center();
        SequenceAction spawnSequence = new SequenceAction();

        for (int i = 0; i < tileCount - 1; i++) {
            spawnSequence.addAction(new SequenceAction(Actions.delay(0.25f), Actions.run(new Runnable() {
                @Override
                public void run() {
                    grid.addTileRandomly(TileType.normal, null);
                }
            })));
        }

        for (int i = 0; i < spawnerCount; i++) {
            final Color color = allColors.pop();
            colorsInPlay.add(color);
            spawnSequence.addAction(new SequenceAction(Actions.delay(0.25f), Actions.run(new Runnable() {
                @Override
                public void run() {
                    grid.addTileRandomly(TileType.spawner, color);
                }
            })));
            spawnSequence.addAction(new SequenceAction(Actions.delay(0.25f), Actions.run(new Runnable() {
                @Override
                public void run() {
                    grid.addTileRandomly(TileType.receiver, color);
                }
            })));
        }

        spawnSequence.getActions().shuffle();
        return spawnSequence;
    }

    @Override
    public void render(float delta) {
        stage.draw();
        hud.act();
        hud.draw();
        if (!paused) stage.act(delta);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
    }

    @Override
    public void pause() {
        paused = true;
        pauseTime = TimeUtils.nanosToMillis(TimeUtils.nanoTime());
        Assets.pauseButtonDrawable.setRegion(Assets.exit);
        pausedLabel.setVisible(true);
        Timer.instance().stop();
        resumeTouchListener = new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (hud.hit(event.getStageX(), event.getStageY(), false) == pauseButton) {
                    Dialog exitDialouge = new Dialog("", Assets.windowStyle) {
                        @Override
                        protected void result(Object object) {
                            resume();
                            if (object == "Y") endGame();
                        }
                    };
                    Label title = new Label("Quit?", Assets.headerLabelStyle);
                    title.setAlignment(Align.center);
                    exitDialouge.getContentTable().add(title).expandX();
                    exitDialouge.button(new TextButton("Yes", Assets.textButtonStyle), "Y");
                    exitDialouge.getButtonTable().getCells().peek().space(100);
                    exitDialouge.button(new TextButton("No", Assets.textButtonStyle), "N");
                    hud.removeCaptureListener(resumeTouchListener);
                    exitDialouge.show(hud);
                } else {
                    resume();
                }
                return true;
            }
        };
        hud.addCaptureListener(resumeTouchListener);
    }

    @Override
    public void resume() {
        paused = false;
        Timer.instance().delay(TimeUtils.nanosToMillis(TimeUtils.nanoTime()) - pauseTime);
        pausedLabel.setVisible(false);
        Assets.pauseButtonDrawable.setRegion(Assets.pause);
        Timer.instance().start();
        hud.removeCaptureListener(resumeTouchListener);
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    private void enterEditMode(final shopItem item) {
        switch (item.name) {
            case "Expand Floor":
                grid.addTileRandomly(TileType.normal, null);
                exitEditMode();
                return;
            case "Rush Block":
                for (Actor actor :
                        stage.getActors()) {
                    if (!(actor instanceof Spawner)) actor.getColor().a = 0.25f;
                }
                break;
            case "Fast Block":
                for (Actor actor :
                        stage.getActors()) {
                    if (!(actor instanceof Block)) {
                        actor.getColor().a *= 0.25f;
                    }
                }
                break;
            case "Production Skip":
                for (Actor actor :
                        stage.getActors()) {
                    if (!(actor instanceof Spawner)) actor.getColor().a = 0.25f;
                }
                break;
            case "Rush Components":
                for (int i = 0; i < MathUtils.random(5, 15); i++) grid.spawnComponent(colorsInPlay.random());
                exitEditMode();
                return;
            case "Production Control":
                for (Actor actor :
                        stage.getActors()) {
                    if (!(actor instanceof Spawner)) actor.getColor().a = 0.25f;
                }
                break;
        }

        editModeTouchListener = new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Tile touchedTile = grid.getTile(MathUtils.floor(event.getStageX() / grid.tileSize), MathUtils.floor(event.getStageY() / grid.tileSize));
                try {
                    switch (item.name) {
                        case "Rush Block":
                            ((Spawner) touchedTile).spawnCart();
                            exitEditMode();
                            break;
                        case "Fast Block":
                            touchedTile.getBlockAtLocation().moveDuration = 0.25f;
                            exitEditMode();
                            break;
                        case "Production Skip":
                            touchedTile.getActions().first().restart();
                            exitEditMode();
                            break;
                        case "Production Control":
                            final Label intervalDisplay = new Label(String.valueOf(((Spawner) touchedTile).spawnInterval), Assets.textLabelStyle);
                            final Spawner spawner = ((Spawner) touchedTile);
                            Dialog controlWindow = new Dialog("", Assets.windowStyle) {
                                @Override
                                protected void result(Object object) {
                                    if (object == "OK") {
                                        spawner.getActions().pop();
                                        spawner.loadAction();
                                        exitEditMode();
                                    }
                                }
                            };
                            Label title = new Label("Production Interval", Assets.headerLabelStyle);
                            title.setAlignment(Align.center);
                            controlWindow.getContentTable().add(title).expandX();
                            final float lowerBound = spawner.spawnInterval - 5;
                            final float upperBound = spawner.spawnInterval + 5;
                            TextButton minus = new TextButton("-", Assets.textButtonStyle);
                            minus.addListener(new ChangeListener() {
                                @Override
                                public void changed(ChangeEvent event, Actor actor) {
                                    spawner.spawnInterval = MathUtils.clamp(spawner.spawnInterval - 1, lowerBound, upperBound);
                                    if (spawner.spawnInterval < 0) spawner.spawnInterval = 0;
                                    intervalDisplay.setText(String.valueOf(spawner.spawnInterval));
                                    event.cancel();
                                }
                            });
                            controlWindow.button(minus);
                            controlWindow.getButtonTable().add(intervalDisplay).expandX();
                            TextButton plus = new TextButton("+", Assets.textButtonStyle);
                            plus.addListener(new ChangeListener() {
                                @Override
                                public void changed(ChangeEvent event, Actor actor) {
                                    spawner.spawnInterval = MathUtils.clamp(spawner.spawnInterval + 1, lowerBound, upperBound);
                                    intervalDisplay.setText(String.valueOf(spawner.spawnInterval));
                                    event.cancel();
                                }
                            });
                            controlWindow.button(plus);
                            controlWindow.getButtonTable().row();
                            controlWindow.button(new TextButton("OK", Assets.textButtonStyle), "OK");
                            controlWindow.getButtonTable().getCells().peek().colspan(3);
                            controlWindow.show(hud);
                            break;
                    }
                } catch (Exception ex) { //Refund
                    changeFunds(item.price);
                    exitEditMode();
                }
                return true;
            }
        };
        stage.addCaptureListener(editModeTouchListener);
    }

    private void exitEditMode() {
        for (Actor actor :
                stage.getActors()) {
            actor.getColor().a = 1;
        }
        if (editModeTouchListener != null) stage.removeCaptureListener(editModeTouchListener);
        editButton.setVisible(true);
        resume();
    }
}

class shopItem {
    int price;
    String name;

    shopItem(int price, String name) {
        this.price = price;
        this.name = name;
    }
}