package com.mattmohandiss.blockfactory;

/**
 * Created by mattm on 2/11/2017.
 */
public enum Direction {
    LEFT, RIGHT, UP, DOWN;

    Direction opposite() {
        switch (this) {
            case UP:
                return DOWN;
            case DOWN:
                return UP;
            case LEFT:
                return RIGHT;
            case RIGHT:
                return LEFT;
        }
        return null;
    }

    int absoluteX() {
        switch (this) {
            case LEFT:
                return -1;
            case RIGHT:
                return 1;
        }
        return 0;
    }

    int absoluteY() {
        switch (this) {
            case DOWN:
                return -1;
            case UP:
                return 1;
        }
        return 0;
    }
}
