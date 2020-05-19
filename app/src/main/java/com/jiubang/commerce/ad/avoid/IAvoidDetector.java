package com.jiubang.commerce.ad.avoid;

public interface IAvoidDetector {
    void detect(Object... objArr);

    boolean isNoad();

    boolean shouldAvoid();
}
