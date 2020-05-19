package com.jiubang.commerce.buychannel.buyChannel.bean;

public class UserTypeInfo {

    public enum FirstUserType {
        apkbuy,
        userbuy,
        withCount,
        organic
    }

    public enum SecondUserType {
        GP_ORGNIC(-1),
        WITHCOUNT_ORGNIC(0),
        GA_USERBUY(1),
        FB_AUTO(2),
        FB_NOTAUTO(3),
        ADWORDS_AUTO(4),
        APK_USERBUY(5),
        ADWORDS_NOTAUTO(6),
        WITHCOUNT_NOT_ORGNIC(7),
        NOT_GP_ORGNIC(8),
        UNKNOWN_USERBUY(9),
        UNKNOWN_OLDUSER(10);
        
        private int mValue;

        private SecondUserType(int value) {
            this.mValue = value;
        }

        public int getValue() {
            return this.mValue;
        }
    }
}
