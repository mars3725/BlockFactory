package com.mattmohandiss.blockfactory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.utils.Array;

import static com.mattmohandiss.blockfactory.Direction.*;

class Spawner extends Tile {
    float spawnInterval;
    private boolean blocked = false;
    private Actor cover;

    Spawner(final Grid grid, int x, int y, Direction anchorDirection, Color color, float spawnInterval) {
        super(grid, TileType.spawner, x, y, anchorDirection, color);
        this.spawnInterval = spawnInterval;
        texture = Assets.spawner;
        cover = new Actor() {
            @Override
            public void draw(Batch batch, float parentAlpha) {
                super.draw(batch, parentAlpha);
                batch.end();
                Gdx.gl.glEnable(GL20.GL_BLEND);
                Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
                borderRenderer.setProjectionMatrix(batch.getProjectionMatrix());
                borderRenderer.begin(ShapeRenderer.ShapeType.Filled);
                borderRenderer.setColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, .4f);
                borderRenderer.rect(getX() * grid.tileSize, getY() * grid.tileSize, getWidth(), getHeight());
                borderRenderer.end();
                Gdx.gl.glDisable(GL20.GL_BLEND);
                batch.begin();
            }
        };
        loadAction();
    }

    void loadAction() {
        Action setSize = null;
        Action expand = null;
        switch (anchorDirection) {
            case UP:
                cover.setBounds(getX(), getY(), grid.tileSize, 0);
                setSize = Actions.sizeTo(grid.tileSize, 0);
                expand = Actions.sizeTo(grid.tileSize, grid.tileSize, this.spawnInterval);
                break;
            case DOWN:
                cover.setBounds(getX(), getY() + 1, grid.tileSize, 0);
                setSize = Actions.sizeTo(grid.tileSize, 0);
                expand = Actions.sizeTo(grid.tileSize, -grid.tileSize, this.spawnInterval);
                break;
            case LEFT:
                cover.setBounds(getX() + 1, getY(), 0, grid.tileSize);
                setSize = Actions.sizeTo(0, grid.tileSize);
                expand = Actions.sizeTo(-grid.tileSize, grid.tileSize, this.spawnInterval);
                break;
            case RIGHT:
                cover.setBounds(getX(), getY(), 0, grid.tileSize);
                setSize = Actions.sizeTo(0, grid.tileSize);
                expand = Actions.sizeTo(grid.tileSize, grid.tileSize, this.spawnInterval);
                break;
        }
        grid.stage.addActor(cover);
        cover.toFront();
        SequenceAction loadAction = new SequenceAction(setSize, expand, Actions.run(new Runnable() {
            @Override
            public void run() {
                spawnCart();
            }
        }));
        cover.addAction(Actions.forever(loadAction));
    }

    @Override
    public Array<Action> getActions() {
        return cover.getActions();
    }

    @Override
    public boolean setZIndex(int index) {
        cover.setZIndex(index - 1);
        return super.setZIndex(index);
    }

    void spawnCart() {
        Block block = new Block(grid, ((int) getX()), ((int) getY()), getColor());
        grid.stage.addActor(block);

        boolean moved = false;

        switch (anchorDirection) {
            case UP:
                moved = block.move(UP);
                break;
            case DOWN:
                moved = block.move(DOWN);
                break;
            case LEFT:
                moved = block.move(LEFT);
                break;
            case RIGHT:
                moved = block.move(RIGHT);
                break;
        }

        if (!moved) {
            block.remove();
            if (blocked) grid.screen.endGame();
            else blocked = true;
        } else {
            grid.blocks.add(block);
            if (blocked) blocked = false;
        }
        grid.fixZPositions();
    }

    @Override
    boolean isCartAllowed(Block block) {
        return false;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (blocked)
            batch.draw(Assets.blocked, getX() * grid.tileSize, getY() * grid.tileSize, grid.tileSize, grid.tileSize);
    }
}
