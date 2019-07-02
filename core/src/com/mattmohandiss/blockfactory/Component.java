package com.mattmohandiss.blockfactory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Timer;


/**
 * Created by mattm on 2/17/2017.
 */
class Component extends Actor {
    ComponentType type;
    Grid grid;
    Texture texture;
    Rectangle cartBounds = new Rectangle();
    Rectangle ballBounds = new Rectangle();
    private boolean collected = false;

    Component(Grid grid, Color color, ComponentType type, Direction travelDirection) {
        this.grid = grid;
        this.type = type;
        setColor(color);
        texture = type.getAsset();
        setTouchable(Touchable.disabled);
        setSize(grid.tileSize / 4, grid.tileSize / 4);

        float spawnCoordinate = grid.getBound(travelDirection.opposite());

        if (travelDirection == Direction.DOWN || travelDirection == Direction.LEFT) {
            spawnCoordinate++;
        } else {
            spawnCoordinate--;
        }
        spawnCoordinate *= grid.tileSize;
        OrthographicCamera camera = (OrthographicCamera) grid.stage.getCamera();
        float minX = grid.getBound(Direction.LEFT) * grid.tileSize;
        float minY = grid.getBound(Direction.DOWN) * grid.tileSize;
        float maxX = grid.getBound(Direction.RIGHT) * grid.tileSize;
        float maxY = grid.getBound(Direction.UP) * grid.tileSize;

        int speed = MathUtils.random(100, 150);
        switch (travelDirection) {
            case UP:
                setPosition(MathUtils.random(minX, maxX - getWidth()), Math.min(spawnCoordinate, camera.position.y - camera.viewportHeight * camera.zoom / 2 - getHeight()));
                addAction(Actions.forever(Actions.moveBy(0, speed, 1)));
                break;
            case DOWN:
                setPosition(MathUtils.random(minX, maxX - getWidth()), Math.max(spawnCoordinate, camera.position.y + camera.viewportHeight * camera.zoom / 2));
                addAction(Actions.forever(Actions.moveBy(0, -speed, 1)));
                break;
            case LEFT:
                setPosition(Math.max(spawnCoordinate, camera.position.x + camera.viewportWidth * camera.zoom / 2), MathUtils.random(minY, maxY - getHeight()));
                addAction(Actions.forever(Actions.moveBy(-speed, 0, 1)));
                break;
            case RIGHT:
                setPosition(Math.min(spawnCoordinate, camera.position.x - camera.viewportWidth * camera.zoom / 2 - getWidth()), MathUtils.random(minY, maxY - getHeight()));
                addAction(Actions.forever(Actions.moveBy(speed, 0, 1)));
                break;
        }

        Timer.instance().scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                if (!collected) {
                    attemptCollection();
                } else {
                    cancel();
                }
            }
        }, 0, .25f);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setColor(getColor().r, getColor().g, getColor().b, getColor().a * parentAlpha);
        batch.draw(texture, getX(), getY(), getWidth(), getHeight());
    }

    void attemptCollection() {
        ballBounds.set(getX(), getY(), getWidth(), getHeight());
        for (Block block :
                grid.blocks) {
            if (block.getColor().equals(getColor())) {
                cartBounds.set(block.getX(), block.getY(), block.getWidth(), block.getHeight());
                if (cartBounds.overlaps(ballBounds)) {
                    for (int i = 0; i < block.collectedComponents.size; i++) {
                        if (block.collectedComponents.getKeyAt(i) == type && block.collectedComponents.getValueAt(i) == null) {
                            block.collectedComponents.setValue(i, this);
                            collected = true;
                            clearActions();
                            Vector2 translatedPos = block.parentToLocalCoordinates(new Vector2(getX(), getY()));
                            block.addActor(this);
                            setPosition(translatedPos.x, translatedPos.y);
                            Vector2 targetPos = block.positions.get(i);
                            float duration = Vector2.dst(getX(), getY(), targetPos.x, targetPos.y) / 300;
                            addAction(Actions.moveTo(targetPos.x, targetPos.y, duration));
                            break;
                        }
                    }
                }
            }
        }
    }
}
