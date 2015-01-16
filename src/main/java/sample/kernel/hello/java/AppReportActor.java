package sample.kernel.hello.java;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

public class AppReportActor extends UntypedActor {
	private final ActorRef resultHandler;
	
	public AppReportActor(ActorRef resultHandler) {
		this.resultHandler = resultHandler;
	}
	
	@Override
	public void onReceive(Object msg) throws Exception {
		if(msg instanceof Schedule) {
			Thread.sleep(1000);
			resultHandler.tell("app report", getSelf());
		} else {
			unhandled(msg);
		}
	}

}
