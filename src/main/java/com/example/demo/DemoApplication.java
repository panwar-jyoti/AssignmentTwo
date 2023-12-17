package com.example.demo;

import com.example.demo.adapter.LocalDateTimeTypeAdapter;
import com.example.demo.model.Transaction;
import com.example.demo.structmap.Car;
import com.example.demo.structmap.CarDto;
import com.example.demo.structmap.CarMapper;
import com.example.demo.structmap.CarType;
import com.google.common.cache.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class DemoApplication {

//	using object mapper
//	public static <ObjectMapper> void getTransactions(){
//		ObjectMapper mapper = new ObjectMapper();
//		mapper.registerModule(new JSR310Module());
//
//		try{
//			Transaction t1 = mapper.readValue(new File("src\\main\\resources\\transaction.json"), Transaction.class);
//			System.out.println(t1);
//		} catch (StreamReadException e) {
//			throw new RuntimeException(e);
//		} catch (DatabindException e) {
//			throw new RuntimeException(e);
//		} catch (IOException e) {
//			throw new RuntimeException(e);
//		}
//	}

	public static void main(String[] args) {

		Transaction[] transactions;
		try {
//			Read file
			ClassLoader classLoader = DemoApplication.class.getClassLoader();
			File file = new File(classLoader.getResource("transaction.json").getFile());
			String content = new String(Files.readAllBytes(file.toPath()));

//			json to pojo
			GsonBuilder builder = new GsonBuilder();
			builder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter());
			builder.setPrettyPrinting();
			Gson gson = builder.create();
			transactions = gson.fromJson(content, Transaction[].class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		LoadingCache<Long, Transaction> transactionCache =
				CacheBuilder.newBuilder()
						.maximumSize(100)                             // maximum 100 records can be cached
						.expireAfterAccess(30, TimeUnit.MINUTES)      // cache will expire after 30 minutes of access
						.build(new CacheLoader<Long, Transaction>() {  // build the cacheLoader

							@Override
							public Transaction load(Long trnId) throws Exception {
								return Arrays.stream(transactions).filter(transaction1 -> transaction1.getTransactionId()==trnId).findFirst().get();
							}
						});

			Arrays.stream(transactions).forEach( transaction ->
					{
						try {
							System.out.println(transactionCache.get(transaction.getTransactionId()));
						} catch (ExecutionException e) {
							throw new RuntimeException(e);
						}
					}
			);

		//given
		Car car = new Car( "Morris", 5, CarType.SEDAN );

		//when
		CarDto carDto = CarMapper.INSTANCE.carToCarDto( car );

		//then
		System.out.println(carDto);
		
        SpringApplication.run(DemoApplication.class, args);
	}
}
