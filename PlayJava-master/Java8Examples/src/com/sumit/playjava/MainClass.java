package com.sumit.playjava;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.sumit.playjava.defaultMethod.ImplementInterfaceWithDefaultMethod;
import com.sumit.playjava.staticMethod.InterfaceWithStaticMethod;

public class MainClass {

	public static void main(String[] args) {
		
		/**
		 * Static and default method in interface:
		 */
		
		
		//1. Default method: below class implement the default method from one of the interface(s) 
		ImplementInterfaceWithDefaultMethod defaultClass=new ImplementInterfaceWithDefaultMethod();
		defaultClass.getDefault();
		System.out.println("=================================================");
		
		//2.Static method of interface can be directly called with the interface
		//No need to create object of class implementing it.
		InterfaceWithStaticMethod.getStaticData();
		System.out.println("=================================================");
	
		
		
		
		
		
		/**
		 * Functional Interfaces.
		 * Function, Consumer, Supplier and Predicate
		 */
		
		//1.Predicate: return boolean value
		Predicate<String> pred=new Predicate<String>() {
			@Override
			public boolean test(String str) {
				return str.length()>10;
			}
		};
		
		String s="PlayJava !!!";
		System.out.println("Boolean value from predicate: "+pred.test(s));
		System.out.println("=================================================");
		
		//2.Consumer: this accepts a single argument and returns no result.
		Consumer<Integer> consumer=new Consumer<Integer>() {
			@Override
			public void accept(Integer i) {
				System.out.println("Nothing is returned in case of consumer");
			}
		};
		consumer.accept(10);
		System.out.println("=================================================");
		
		//3.Function: this interface accepts one argument and return one result.
		Function<Integer,String> function=new Function<Integer,String>() {
			@Override
			public String apply(Integer t) {
				return "The data is: "+t;
			}
		};
		System.out.println(function.apply(100));
		System.out.println("=================================================");
		
		//4.Supplier: this accepts nothing but return a value.
		Supplier<String> supplier=new Supplier<String>() {
			@Override
			public String get() {
				return "Data from supplier functional interface: "+Math.random();
			}
		};
		System.out.println(supplier.get());
		System.out.println("=================================================");
	
		
		
		
		
		
		/**
		 * Intermediate operations:
		 * filter, map, flatMap, distinct & sorted.
		 */
		
		//1.filter: filters the data
		Arrays.asList("one","two","three","one","four","one","two","seven")
			.stream().filter(c->c.startsWith("t"))
							.forEach(System.out::println);
		System.out.println("=================================================");
		
		//2.map: modifies the data to new stream without modifing the existing stream
		Arrays.asList("one","two","three","one","four","one","two","seven")
			.stream().map(c->c+" "+Math.random())
					.forEach(System.out::println);
		System.out.println("=================================================");
		
		//3.flatMap: modifies the existing data only.
		Arrays.asList("one","two","three","one","four","one","two","seven")
			.stream().flatMap(c->Stream.of(Arrays.asList("one")))
				.forEach(System.out::println);
		System.out.println("=================================================");
		
		//4.distinct: gives distinct data of stream
		Arrays.asList("one","two","three","one","four","one","two","seven")
			.stream().distinct()
						.forEach(System.out::println);
		System.out.println("=================================================");
		
		//5.sorted: gives sorted data
		Arrays.asList("one","two","three","one","four","one","two","seven")
		.stream().sorted()
				.forEach(System.out::println);
		System.out.println("=================================================");
		
		
		
		
		
		/***
		 * terminal operationd:
		 * collect, count, reduce, forEach & max.
		 */
		
		//1.collect: it collects the data of stream into container such as collection.
		HashSet<String> hs=Arrays.asList("one","two","three","one","four","one","two","seven")
			.stream().map(p -> p+" "+Math.random())
                .collect(Collectors.toCollection(HashSet::new));
		
		//2.count: it counts the data in stream
		long dataCount=Arrays.asList("one","two","three","one","four","one","two","seven")
				.stream().count();
		System.out.println(dataCount);
		System.out.println("=================================================");
		
		//3.reduce: it takes two arguments and performs operation on it
		String[] arr={"one","two","seven"};
		Optional<String> str=Arrays.stream(arr)
					.reduce((str1,str2) -> str1+"-"+str2);
		System.out.println(str.get());
		System.out.println("=================================================");
		
		//3.forEach: performs operation on each data
		Arrays.asList("one","two","three","one","four","one","two","seven")
			.stream().forEach(System.out::println);
		System.out.println("=================================================");
		
		//4.max: finds the maximum data
		Optional<Integer> maxdata=Arrays.asList(3,5,7,2,1,6)
					.stream().max(Integer::compare);
		System.out.println("Maximum data is: "+maxdata.get());
		System.out.println("=================================================");
		
	}

}
