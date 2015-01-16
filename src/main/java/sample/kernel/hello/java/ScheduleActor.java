package sample.kernel.hello.java;

import java.util.ArrayList;
import java.util.List;

import akka.actor.UntypedActor;

public class ScheduleActor extends UntypedActor {

	@Override
	public void onReceive(Object msg) throws Exception {
		if(msg instanceof String) {
			System.out.println(msg);
		} else if (msg instanceof Schedule) {
			
		}
	}

}
