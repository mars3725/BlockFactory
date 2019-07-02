package com.mattmohandiss.blockfactory;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;

public enum ComponentType {
    square, circle, triangle, star, bomb;

    public static ComponentType random(boolean excludeBomb) {
        if (excludeBomb) return values()[MathUtils.random(values().length - 2)];
        else return values()[MathUtils.random(values().length - 1)];
    }

    public Texture getAsset() {
        switch (this) {
            case square:
                return Assets.square;
            case circle:
                return Assets.circle;
            case triangle:
                return Assets.triangle;
            case star:
                return Assets.star;
            default:
                return null;
        }
    }
}
