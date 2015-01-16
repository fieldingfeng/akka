package sample.kernel.hello.java;

import scala.concurrent.Future;
import akka.dispatch.Futures;

public class AppReportGenerator implements ReportGenerator {

	@Override
	public Future<String> generate(Schedule schedule) {
		return Futures.successful(generate());
	}

	private String generate() {
		System.out.println("begin report generation");
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("completed report generation");
		
		return "app report";
	}

}
