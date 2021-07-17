package com.milimili.launchloves;

import android.animation.TypeEvaluator;
import android.graphics.PointF;

public class BezierTypeEvaluator implements TypeEvaluator<PointF> {
    private final PointF p1;
    private final PointF p2;

    public BezierTypeEvaluator(PointF mPoint1, PointF mPoint2) {
        this.p1 = mPoint1;
        this.p2 = mPoint2;
    }

    @Override
    public PointF evaluate(float fraction, PointF p0, PointF p3) {
        //fraction = t
        float t = fraction;
        PointF result = new PointF();

        result.x = p0.x * (1 - t) * (1 - t) * (1 - t) +
                3 * p1.x * t * (1 - t) * (1 - t) +
                3 * p2.x * t * t * (1 - t) +
                    p3.x * t * t * t;
        result.y = p0.y * (1 - t) * (1 - t) * (1 - t) +
                3 * p1.y * t * (1 - t) * (1 - t) +
                3 * p2.y * t * t * (1 - t) +
                    p3.y * t * t * t;
        return result;
    }

}
