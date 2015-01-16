package sample.kernel.hello.java;

import akka.actor.UntypedActor;

public class ReportResultActor extends UntypedActor {

	@Override
	public void onReceive(Object msg) throws Exception {
		if(msg instanceof String) {
			System.out.println(msg);
		}
	}

}
