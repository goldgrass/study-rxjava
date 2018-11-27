package net.loggable.study.rxjava;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import org.junit.Test;

public class FilterOperationTest {
	@Test
	public void testFilter() {
		// 특정 조건이 맞는 경우만 발행됨.
		Observable.range(0, 9).filter(new Predicate<Integer>() {
			@Override
			public boolean test(Integer integer) throws Exception {
				return integer % 2 == 0;
			}
		}).subscribe(new Consumer<Integer>() {
			@Override
			public void accept(Integer integer) throws Exception {
				System.out.println("onNext = " + integer);
			}
		});
	}
	
	@Test
	public void testTake() {
		// 순차적인 메시지 중에 앞에 N개만 발행
		Observable.range(0, 9).take(3).subscribe(new Consumer<Integer>() {
			@Override
			public void accept(Integer integer) throws Exception {
				System.out.println("onNext = " + integer);
			}
		});
	}
	
	@Test
	public void testTakeLast() {
		// 순차적인 메시지 중에 마지막 N개만 발행
		Observable.range(0, 9).takeLast(3).subscribe(new Consumer<Integer>() {
			@Override
			public void accept(Integer integer) throws Exception {
				System.out.println("onNext = " + integer);
			}
		});
	}
	
	@Test
	public void testDistinct() {
		// 중복을 제거하여 메시지를 발행
		Observable.fromArray(1, 1, 2, 2, 3, 5).distinct().subscribe(new Consumer<Integer>() {
			@Override
			public void accept(Integer integer) throws Exception {
				System.out.println("onNext = " + integer);
			}
		});
	}
	
	@Test
	public void testDistinctUntilChanged() {
		// 순차적인 변화가 발생했을 때만 발행, 1,2,1,3,5가 발행된다. 온도변화, 실시간 제고 수량 체크 로직에 적합
		// 처음 두번의 1이 발생하고 2로 변화가 발생하였으므로 1은 한번만 출력된다.
		Observable.fromArray(1, 1, 2, 1, 3, 5).distinctUntilChanged().subscribe(new Consumer<Integer>() {
			@Override
			public void accept(Integer integer) throws Exception {
				System.out.println("onNext = " + integer);
			}
		});
	}
	
	@Test
	public void testFirst() {
		// 첫번째 메시지만 발행
		Observable.fromArray(1, 2, 3).firstElement().subscribe(new Consumer<Integer>() {
			@Override
			public void accept(Integer integer) throws Exception {
				System.out.println("onNext = " + integer);
			}
		});
	}
	
	@Test
	public void testLast() {
		// 마지막 메시지만 발행
		Observable.fromArray(1, 2, 3).lastElement().subscribe(new Consumer<Integer>() {
			@Override
			public void accept(Integer integer) throws Exception {
				System.out.println("onNext = " + integer);
			}
		});
	}
}