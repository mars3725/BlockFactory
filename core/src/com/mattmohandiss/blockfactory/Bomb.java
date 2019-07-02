package com.mattmohandiss.blockfactory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class Bomb extends Component {
    private final long spawnTime;
    private Animation<Texture> animation;

    Bomb(Grid grid, Direction travelDirection) {
        super(grid, Color.WHITE, ComponentType.bomb, travelDirection);
//        Array<Texture> textures =
////                new Array<>(new Texture[]{Assets.bomb1, Assets.bomb2, Assets.bomb3, Assets.bomb4, Assets.bomb5});
        Array<Texture> textures =
                new Array<>(new Texture[]{Assets.bomb6, Assets.bomb7});
        animation = new Animation<>(0.15f, textures, Animation.PlayMode.LOOP);
        spawnTime = TimeUtils.millis();
    }

    @Override
    void attemptCollection() {
        ballBounds.set(getX(), getY(), getWidth(), getHeight());
        for (Block block :
                grid.blocks) {
            if (block.getColor().equals(getColor())) {
                cartBounds.set(block.getX(), block.getY(), block.getWidth(), block.getHeight());
                if (cartBounds.overlaps(ballBounds)) {
                    block.remove();
                    grid.components.removeValue(this, true);
                    remove();
                }
            }
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        long elapsedTime = TimeUtils.timeSinceMillis(spawnTime) / 100;
        texture = animation.getKeyFrame(elapsedTime);
        super.draw(batch, parentAlpha);
    }
}
