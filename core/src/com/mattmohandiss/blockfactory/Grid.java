package com.mattmohandiss.blockfactory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import static com.mattmohandiss.blockfactory.Direction.*;

/**
 * Created by mattm on 2/9/2017.
 */
class Grid {
    Stage stage;
    Array<Block> blocks = new Array<>();
    Array<Component> components = new Array<>();
    int tileSize;
    PlayScreen screen;
    private Array<Tile> tiles = new Array<>();

    Grid(PlayScreen screen, Stage stage, int tileSize) {
        this.screen = screen;
        this.stage = stage;
        this.tileSize = tileSize;
    }

    private void addTile(TileType type, int x, int y, Direction direction, Color color) {
        Tile tile;

        switch (type) {
            case spawner:
                tile = new Spawner(this, x, y, direction, color, MathUtils.random(4, 12));
                break;

            case receiver:
                tile = new Tile(this, type, x, y, direction, color) {
                    @Override
                    boolean isCartAllowed(Block block) {
                        return block.getColor().equals(this.getColor());
                    }
                };
                break;

            default:
                tile = new Tile(this, type, x, y, direction, color) {
                    @Override
                    boolean isCartAllowed(Block block) {
                        return true;
                    }
                };
                break;
        }

        tiles.add(tile);
        stage.addActor(tile);
        fixZPositions();

        for (Direction possibleDirection :
                Direction.values()) {
            Tile adjacentTile = getTile(x + possibleDirection.absoluteX(), y + possibleDirection.absoluteY());
            tile.adjacentTiles.put(possibleDirection, adjacentTile);
            if (adjacentTile != null) adjacentTile.adjacentTiles.put(possibleDirection.opposite(), tile);
        }
    }

    void addTile(int x, int y) {
        addTile(TileType.normal, x, y, null, null);
    }

    boolean addTileRandomly(TileType type, Color color) {
        tiles.shuffle();
        for (Tile tile :
                tiles) {
            if (tile.type == TileType.normal) {
                for (ObjectMap.Entry<Direction, Tile> adjacentTileEntry :
                        tile.adjacentTiles) {
                    if (adjacentTileEntry.value == null) {
                        addTile(type, ((int) tile.getX() + adjacentTileEntry.key.absoluteX()),
                                ((int) tile.getY() + adjacentTileEntry.key.absoluteY()),
                                adjacentTileEntry.key.opposite(), color);
                        fixZPositions();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    Tile getTile(int x, int y) {
        for (Tile tile :
                tiles) {
            if (tile.getX() == x && tile.getY() == y) {
                return tile;
            }
        }
        return null;
    }

    Block getBlock(int x, int y) {
        for (Block block :
                blocks) {
            if (block.gridLocation.x == x && block.gridLocation.y == y) {
                return block;
            }
        }
        return null;
    }

    void center() {
        int midX = MathUtils.ceil((getBound(LEFT) + getBound(RIGHT)) / 2);
        int midY = MathUtils.ceil((getBound(UP) + getBound(DOWN)) / 2);
        stage.getCamera().position.set(midX * tileSize, midY * tileSize, 0);
    }

    float getBound(Direction direction) {
        float bound = 0;
        for (Tile tile :
                tiles) {
            switch (direction) {
                case UP:
                    bound = Math.max(bound, tile.getY());
                    break;
                case DOWN:
                    bound = Math.min(bound, tile.getY());
                    break;
                case LEFT:
                    bound = Math.min(bound, tile.getX());
                    break;
                case RIGHT:
                    bound = Math.max(bound, tile.getX());
                    break;
            }
        }
        return bound;
    }

    void spawnComponent(Color color) {
        Direction direction = Direction.values()[MathUtils.random(Direction.values().length - 1)];
        Component component;
        if (MathUtils.random(2) == 1) component = new Bomb(this, direction);
        else component = new Component(this, color, ComponentType.random(true), direction);
        components.add(component);
        System.out.println(component.type);
        stage.addActor(component);
        fixZPositions();
    }

    void fixZPositions() {
        for (Block block :
                blocks) {
            block.toBack();
        }

        for (Tile tile :
                tiles) {
            if (tile.type == TileType.spawner || tile.type == TileType.receiver) {
                tile.toFront();
            } else {
                tile.toBack();
            }
        }

        for (Component component :
                components) {
            component.toFront();
        }
    }
}
