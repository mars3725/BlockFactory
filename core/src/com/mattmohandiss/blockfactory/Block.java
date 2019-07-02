package com.mattmohandiss.blockfactory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by mattm on 2/8/2017.
 */
public class Block extends Group {
    float moveDuration = 0.5f;
    ArrayMap<ComponentType, Component> collectedComponents = new ArrayMap<>(4);
    Array<Vector2> positions = new Array<>(4);
    Vector2 gridLocation;
    private Grid grid;

    Block(final Grid grid, int x, int y, final Color color) {
        this.grid = grid;
        this.gridLocation = new Vector2(x, y);
        setColor(color);
        setBounds(x * grid.tileSize, y * grid.tileSize, grid.tileSize, grid.tileSize);
        final float componentSize = grid.tileSize / 4;

        positions.add(new Vector2(getWidth() * .75f - componentSize / 2, getHeight() * .75f - componentSize / 2));
        positions.add(new Vector2(getWidth() * .25f - componentSize / 2, getHeight() * .75f - componentSize / 2));
        positions.add(new Vector2(getWidth() * .25f - componentSize / 2, getHeight() * .25f - componentSize / 2));
        positions.add(new Vector2(getWidth() * .75f - componentSize / 2, getHeight() * .25f - componentSize / 2));

        addActor(new Actor() {
            @Override
            public void draw(Batch batch, float parentAlpha) {
                batch.setColor(color.r, color.g, color.b, getColor().a * parentAlpha);
//                batch.setColor(getColor().r, getColor().g, getColor().b, getColor().a*parentAlpha);
                batch.draw(Assets.block, 0, 0, grid.tileSize, grid.tileSize);
            }
        });
        for (final Vector2 position :
                positions) {
            final ComponentType type = ComponentType.random(true);
            collectedComponents.insert(positions.indexOf(position, true), type, null);
            addActor(new Actor() {
                @Override
                public void draw(Batch batch, float parentAlpha) {
                    batch.setColor(Color.LIGHT_GRAY.r, Color.LIGHT_GRAY.g, Color.LIGHT_GRAY.b, getColor().a * parentAlpha);
                    batch.draw(type.getAsset(), position.x, position.y, componentSize, componentSize);
                }
            });
        }
    }

    boolean move(final Direction direction) {
        final Tile currentTile = grid.getTile(((int) gridLocation.x), ((int) gridLocation.y));
        final Tile newTile = currentTile.adjacentTiles.get(direction);

        if (!hasActions() && newTile != null && newTile.isCartAllowed(this)
                && (newTile.getBlockAtLocation() == null || newTile.getBlockAtLocation().move(direction))) {
            newTile.setBlockAtLocation(this);
            addAction(sequence(moveTo(newTile.getX() * grid.tileSize, newTile.getY() * grid.tileSize, moveDuration), run(new Runnable() {
                @Override
                public void run() {
                    if (currentTile.getBlockAtLocation() == Block.this) currentTile.setBlockAtLocation(null);
                    gridLocation.add(direction.absoluteX(), direction.absoluteY());
                    if (newTile.type == TileType.receiver) {
                        int value = 25;
                        for (int i = 0; i < collectedComponents.size; i++) {
                            if (collectedComponents.getValueAt(i) != null) value *= 2;
                        }
                        grid.screen.changeFunds(value);
                        remove();
                    }
                }
            })));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean remove() {
        grid.getTile(((int) gridLocation.x), ((int) gridLocation.y)).setBlockAtLocation(null);
        grid.blocks.removeValue(Block.this, true);
        grid.components.removeAll(collectedComponents.values().toArray(), true);
        return super.remove();
    }
}
