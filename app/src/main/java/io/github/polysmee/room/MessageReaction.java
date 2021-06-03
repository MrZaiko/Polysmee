package io.github.polysmee.room;

import android.content.Context;
import android.util.Log;

import io.github.polysmee.R;

public enum MessageReaction {
    DEFAULT(0, 0),
    JOY(1, R.string.emoji_joy),
    SUNGLASSES(2, R.string.emoji_sunglasses),
    HEART_EYES(3, R.string.emoji_heart_eyes),
    EXPRESSION_LESS(4, R.string.emoji_expression_less),
    SAD(5, R.string.emoji_sad);

    private final int reactionId, emoji;

    MessageReaction(int reactionId, int emoji) {
        this.reactionId = reactionId;
        this.emoji = emoji;
    }

    public int getEmoji() {
        return emoji;
    }

    public int getReactionId() {
        return reactionId;
    }

    public static MessageReaction getReaction(Context context, String s) {

        if (s.equals(context.getString(JOY.emoji))) {
            return JOY;
        } else if (s.equals(context.getString(SUNGLASSES.emoji))) {
            return SUNGLASSES;
        } else if (s.equals(context.getString(HEART_EYES.emoji))) {
            return HEART_EYES;
        } else if (s.equals(context.getString(EXPRESSION_LESS.emoji))) {
            return EXPRESSION_LESS;
        } else if (s.equals(context.getString(SAD.emoji))) {
            return SAD;
        } else {
            return DEFAULT;
        }
    }

    public static MessageReaction getReaction(int id) {
        switch (id) {
            case 1:
                return JOY;
            case 2:
                return SUNGLASSES;
            case 3:
                return HEART_EYES;
            case 4:
                return EXPRESSION_LESS;
            case 5:
                return SAD;
            default:
                return DEFAULT;
        }
    }
}