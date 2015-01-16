package sample.kernel.hello.java;

import scala.concurrent.Future;

public interface ReportGenerator {
	public Future<String> generate(Schedule schedule);
}
