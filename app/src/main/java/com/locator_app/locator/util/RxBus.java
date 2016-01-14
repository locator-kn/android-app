package com.locator_app.locator.util;



import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

public class RxBus {

    private final Subject<Object, Object> bus = new SerializedSubject<>(PublishSubject.create());

    public void send(Object event) {
        bus.onNext(event);
    }

    public Observable<Object> toObservable() {
        return bus;
    }

    private static RxBus globalBusInstance;
    synchronized public static RxBus globalBus() {
        if (globalBusInstance == null) {
            globalBusInstance = new RxBus();
        }
        return globalBusInstance;
    }
}
