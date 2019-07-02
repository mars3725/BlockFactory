package com.mattmohandiss.blockfactory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Created by mattm on 2/11/2017.
 */
abstract class Tile extends Actor {
    ObjectMap<Direction, Tile> adjacentTiles = new ObjectMap<>(4);
    Grid grid;
    TileType type;
    Direction anchorDirection;
    ShapeRenderer borderRenderer = new ShapeRenderer();
    Texture texture;
    BitmapFont font = Assets.getFont(0.5f, Color.BLACK);
    private Block blockAtLocation = null;

    Tile(Grid grid, TileType type, int x, int y, Direction anchorDirection, Color color) {
        borderRenderer.setAutoShapeType(true);
        setBounds(x, y, grid.tileSize, grid.tileSize);
        setTouchable(Touchable.enabled);
        this.anchorDirection = anchorDirection;
        this.grid = grid;
        this.type = type;

        if (anchorDirection != null) {
            switch (anchorDirection) {
                case UP:
                    setRotation(90);
                    break;
                case DOWN:
                    setRotation(270);
                    break;
                case LEFT:
                    setRotation(180);
                    break;
                case RIGHT:
                    setRotation(0);
                    break;
            }
        }

        if (color != null) setColor(color);
        else setColor(Color.WHITE);

        switch (type) {
            case normal:
                texture = Assets.tile;
                break;
            case spawner:
                break;
            case receiver:
                texture = Assets.receiver;
        }

        addAction(Actions.alpha(0));
        addAction(Actions.fadeIn(0.5f));
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        return super.hit(x * grid.tileSize, y * grid.tileSize, touchable);
    }

    Block getBlockAtLocation() {
        return blockAtLocation;
    }

    void setBlockAtLocation(Block blockAtLocation) {
        this.blockAtLocation = blockAtLocation;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setColor(getColor().r, getColor().g, getColor().b, getColor().a * parentAlpha);
        batch.draw(texture, grid.tileSize * getX(), grid.tileSize * getY(),
                grid.tileSize / 2, grid.tileSize / 2,
                getWidth(), getHeight(),
                1, 1, getRotation(),
                0, 0, texture.getWidth(), texture.getHeight(), false, false);
        batch.end();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        drawBorder(batch.getProjectionMatrix(), 10, getColor().a * parentAlpha);
        Gdx.gl.glDisable(GL20.GL_BLEND);
        batch.begin();
    }

    private void drawBorder(Matrix4 projectionMatrix, int width, float alpha) {
        Vector2 start = new Vector2(getX(), getY());
        Vector2 end = new Vector2(getX(), getY());
        borderRenderer.setProjectionMatrix(projectionMatrix);
        borderRenderer.setColor(0, 0, 0, alpha);
        borderRenderer.begin();
        borderRenderer.set(ShapeRenderer.ShapeType.Filled);

        for (ObjectMap.Entry<Direction, Tile> adjacentTileEntry :
                adjacentTiles) {
            if (adjacentTileEntry.value == null) {
                start.set(getX(), getY());
                end.set(getX(), getY());
                switch (adjacentTileEntry.key) {
                    case UP:
                        start.add(0, 1);
                        end.add(1, 1);
                        break;
                    case DOWN:
                        start.add(0, 0);
                        end.add(1, 0);
                        break;
                    case LEFT:
                        start.add(0, 0);
                        end.add(0, 1);
                        break;
                    case RIGHT:
                        start.add(1, 0);
                        end.add(1, 1);
                        break;
                }
                start.scl(grid.tileSize);
                end.scl(grid.tileSize);
                borderRenderer.rectLine(start, end, width);
            }
        }
        borderRenderer.end();
    }

    abstract boolean isCartAllowed(Block block);
}
