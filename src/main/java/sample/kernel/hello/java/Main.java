package sample.kernel.hello.java;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class Main {

	public static void main(String [] argv) {
	    ActorSystem system = ActorSystem.create("report");

	    ActorRef resultActor = system.actorOf(Props.create(ReportResultActor.class), "resultHandler");

	    ActorRef appReportGenerator = system.actorOf(Props.create(AppReportActor.class, resultActor), "reportGenerator");

	    for(int i = 0; i < 1000; i++) {
	    	System.out.println("Submit report");
	    	appReportGenerator.tell(new Schedule(), ActorRef.noSender());
	    }
	    
//		ReportGenerator reportGenerator = TypedActor.get(system).typedActorOf(
//				new TypedProps<AppReportGenerator>(ReportGenerator.class, AppReportGenerator.class));
//		
//		reportGenerator.generate(new Schedule());
//		
//		System.out.println("Do Something");
//		
//		TypedActor.get(system).stop(reportGenerator);
	}
}
