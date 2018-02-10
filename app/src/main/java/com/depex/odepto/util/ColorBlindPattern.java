package com.depex.odepto.util;

import com.depex.odepto.R;

/**
 * Created by we on 2/8/2018.
 */

public enum ColorBlindPattern {

        NONE(0),
        DIAMOND(R.drawable.pattern_diamond),
        VERTICAL_LINES(R.drawable.pattern_vertical),
        DIAGONAL_LINES_1(R.drawable.pattern_diagonal),
        DIAGONAL_LINES_2(R.drawable.pattern_diagonal_2);

        int resId;

        private ColorBlindPattern(int resId) {
            this.resId = resId;
        }

        public int resId() {
            return this.resId;
        }
    }
