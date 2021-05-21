package io.github.polysmee.room;

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