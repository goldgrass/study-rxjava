package net.loggable.study.rxjava;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.junit.Test;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;

public class ObservableCreationTest {
	@Test
	public void observalbe은_subscribe할때_동작한다() {
		// Observable은 subscribe하는 시점에 메시지가 발행되는 cold 방식
		// ObservableOnSubscribe 콜백은 observer가 등록될 때 실행되는 callback임.
		Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
			@Override
			public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
				// 메시지를 발행할때는 emitter의 onNext를 실행한다.
				// 결국에는 subscribe에 등록된 subscirber를 래핑하는 객체임.
				IntStream.range(0, 9).forEach(emitter::onNext);
				emitter.onComplete();
			}
		});

		//observer 등록. 이 시점에 메시지가 발행된다.
		observable.subscribe(new Observer<Integer>() {
			@Override
			public void onSubscribe(Disposable d) {
				System.out.println("subscribe....");
			}
			
			@Override
			public void onNext(Integer integer) {
				System.out.println("onNext = " + integer);
			}
			
			@Override
			public void onError(Throwable e) {
			
			}
			
			@Override
			public void onComplete() {
			
			}
		});
	}
	
	@Test
	public void 람다를_사용하면_편하다() {
		Observable<Integer> observable = Observable.create(emitter -> {
			IntStream.range(0, 9).forEach(emitter::onNext);
			emitter.onComplete();
		});
		// consumer를 onNext 콜백으로 등록, onError, onComplete를 consumer로 등록 가능하다.
		observable.subscribe(integer -> System.out.println("onNext = " + integer));
	}
	
	@Test
	public void defer를_쓰면_observable_생성을_늦출_수_있다() {
		// observable을 생성하는 callable을 제공하여 바로 observable 객체를 생성하는 것이 아니라
		// observer가 등록되는 시점에 observable을 생성한다.
		Observable<Integer> observable = Observable.defer(new Callable<ObservableSource<Integer>>() {
			@Override
			public ObservableSource<Integer> call() throws Exception {
				return new Observable<Integer>() {
					@Override
					protected void subscribeActual(Observer<? super Integer> observer) {
						IntStream.range(0, 9).forEach(observer::onNext);
						observer.onComplete();
					}
				};
			}
		});
		observable.subscribe(new Consumer<Integer>() {
			@Override
			public void accept(Integer integer) throws Exception {
				System.out.println(integer);
			}
		});
	}
	
	@Test
	public void 배열로_생성할_수_있다() {
		Observable<Integer> observable = Observable.fromArray(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
		observable.subscribe(integer -> System.out.println("onNext = " + integer));
	}
	
	@Test
	public void 하나짜리도_생성할_수_있다() {
		Observable<Integer> just = Observable.just(1);
		just.subscribe(integer -> System.out.println("onNext = " + integer));
	}
	
	@Test
	public void callable은_just와_비슷한방식이다() {
		Observable.fromCallable(new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				return 1;
			}
		}).subscribe(integer -> System.out.println(integer));
	}
	
	@Test
	public void 하나짜리는_Single에서도_만들_수_있다() {
		Single.just(1).subscribe(System.out::println);
	}

	@Test
	public void 특정_period단위로_연속되는_숫자를_발행한다() throws InterruptedException {
		Observable.interval(1000, TimeUnit.MILLISECONDS).subscribe(new Consumer<Long>() {
			@Override
			public void accept(Long aLong) throws Exception {
				System.out.println(aLong);
			}
		});
		Thread.sleep(10000);
	}
	
	@Test
	public void range_구간에_증분_숫자를_발행한다() {
		// stream의 range와 다르게 start, count이다.
		Observable.range(10, 10).subscribe(System.out::println);
	}
	
	@Test
	public void 특정시간_이후에_0을_발생시킨다() throws InterruptedException {
		CountDownLatch countDownLatch = new CountDownLatch(1);
		Observable.timer(1000, TimeUnit.MILLISECONDS).subscribe(aLong -> {
			System.out.println(aLong);
			countDownLatch.countDown();
		});
		countDownLatch.await();
	}
	
	@Test
	public void dispose_시킬_수_있다() {
		// observable에 subscribe하고 나면 LambdaObserver로 래핑되고 리턴되는데 이녀석이 disposable을 구현하고 있다.
		// disposable을 CompositeDisposable add하고 clear한다.
		Disposable disposable = Observable.range(10, 10).subscribe(System.out::println);

		CompositeDisposable compositeDisposable = new CompositeDisposable();
		compositeDisposable.add(disposable);
		compositeDisposable.clear();

		// 처음부터 disposable observer를 subscribWith로 등록하고 연이어 CompositeDisposable에 add하고 clear한다.
		Observable<Integer> observable = Observable.range(10, 10);
		DisposableObserver<Integer> disposableObserver = new DisposableObserver<Integer>() {
			@Override
			public void onNext(Integer integer) {
				System.out.println(integer);
			}
			
			@Override
			public void onError(Throwable e) {
			
			}
			
			@Override
			public void onComplete() {
			
			}
		};

		compositeDisposable.add(observable.subscribeWith(disposableObserver));
		compositeDisposable.clear();
	}
}
