package com.jiubang.commerce.buychannel.buyChannel.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.os.Parcel;
import android.os.RemoteException;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

public final class AdvertisingIdClient {

    public static final class AdInfo {
        private final String advertisingId;
        private final boolean limitAdTrackingEnabled;

        AdInfo(String advertisingId2, boolean limitAdTrackingEnabled2) {
            this.advertisingId = advertisingId2;
            this.limitAdTrackingEnabled = limitAdTrackingEnabled2;
        }

        public String getId() {
            return this.advertisingId;
        }

        public boolean isLimitAdTrackingEnabled() {
            return this.limitAdTrackingEnabled;
        }
    }

    public static AdInfo getAdvertisingIdInfo(Context context) throws Exception {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new IllegalStateException("Cannot be called from the main thread");
        }
        try {
            context.getPackageManager().getPackageInfo("com.android.vending", 0);
            AdvertisingConnection connection = new AdvertisingConnection();
            Intent intent = new Intent("com.google.android.gms.ads.identifier.service.START");
            intent.setPackage("com.google.android.gms");
            if (context.bindService(intent, connection, 1)) {
                try {
                    AdvertisingInterface adInterface = new AdvertisingInterface(connection.getBinder());
                    AdInfo adInfo = new AdInfo(adInterface.getId(), adInterface.isLimitAdTrackingEnabled(true));
                    context.unbindService(connection);
                    return adInfo;
                } catch (Exception exception) {
                    throw exception;
                } catch (Throwable th) {
                    context.unbindService(connection);
                    throw th;
                }
            } else {
                throw new IOException("Google Play connection failed");
            }
        } catch (Exception e) {
            throw e;
        }
    }

    private static final class AdvertisingConnection implements ServiceConnection {
        private final LinkedBlockingQueue<IBinder> queue;
        boolean retrieved;

        private AdvertisingConnection() {
            this.retrieved = false;
            this.queue = new LinkedBlockingQueue<>(1);
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                this.queue.put(service);
            } catch (InterruptedException e) {
            }
        }

        public void onServiceDisconnected(ComponentName name) {
        }

        public IBinder getBinder() throws InterruptedException {
            if (this.retrieved) {
                throw new IllegalStateException();
            }
            this.retrieved = true;
            return this.queue.take();
        }
    }

    private static final class AdvertisingInterface implements IInterface {
        private IBinder binder;

        public AdvertisingInterface(IBinder pBinder) {
            this.binder = pBinder;
        }

        public IBinder asBinder() {
            return this.binder;
        }

        public String getId() throws RemoteException {
            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            try {
                data.writeInterfaceToken("com.google.android.gms.ads.identifier.internal.IAdvertisingIdService");
                this.binder.transact(1, data, reply, 0);
                reply.readException();
                return reply.readString();
            } finally {
                reply.recycle();
                data.recycle();
            }
        }

        public boolean isLimitAdTrackingEnabled(boolean paramBoolean) throws RemoteException {
            int i;
            boolean limitAdTracking = true;
            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            try {
                data.writeInterfaceToken("com.google.android.gms.ads.identifier.internal.IAdvertisingIdService");
                if (paramBoolean) {
                    i = 1;
                } else {
                    i = 0;
                }
                data.writeInt(i);
                this.binder.transact(2, data, reply, 0);
                reply.readException();
                if (reply.readInt() == 0) {
                    limitAdTracking = false;
                }
                return limitAdTracking;
            } finally {
                reply.recycle();
                data.recycle();
            }
        }
    }
}
