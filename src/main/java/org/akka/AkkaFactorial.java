package org.akka;

import java.io.Serializable;
import java.math.BigInteger;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.routing.FromConfig;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class AkkaFactorial {

	static class Work implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		int from;
		int to;
		
		Work(int from, int to) {
			this.from = from;
			this.to = to;
		}
	}

	public static class Worker extends UntypedActor {
		LoggingAdapter log = Logging.getLogger(getContext().system(), this);
		@Override
		public void onReceive(Object msg) throws Exception {
			if(msg instanceof Work) {
				Work work = (Work) msg;
				log.info("Got work: " + work.from + "-" + work.to);
				BigInteger result = BigInteger.valueOf(work.from);
				for(int i = work.from; i <= work.to; i++) {
					result = result.multiply(BigInteger.valueOf(i));
				}
				
				getSender().tell(result, getSelf());
			} else {
				unhandled(msg);
			}
		}
		
	}
	
	public static class ResultHandler extends UntypedActor {
		LoggingAdapter log = Logging.getLogger(getContext().system(), this);
		ActorRef backend = getContext().actorOf(FromConfig.getInstance().props(),
			      "factorialBackendRouter");

		BigInteger result;
		int slice;
		int sliceCompleted;
		
//		@Override
//		public void preStart() {
//			ExecutionContext ec = getContext().dispatcher();
//			getContext().system().scheduler().schedule(Duration.create(2, "seconds"), Duration.create(5, "seconds"), 
//			backend, new Work(1, 200), ec, getSelf());
//			
//		}
		  
		@Override
		public void onReceive(Object msg) throws Exception {
			if(msg instanceof BigInteger) {
				log.info("got partial result " + msg);
				
				if(result == null) {
					result = (BigInteger) msg;
				} else {
					result.multiply((BigInteger) msg);
				}
				sliceCompleted++;
				
				if(sliceCompleted == slice) {
					log.info("full result : " + result);
					slice = 0;
					sliceCompleted = 0;
				}
//				backend.tell(new Work(1, 200), getSelf());
			} else if (msg instanceof Integer){
				log.info("Got job : " + msg);
				
				slice = 0;
				sliceCompleted = 0;
				int from = 1;
				int to = from + 19;
				boolean done = false;
				
				while(to <= (int)msg) {
					backend.tell(new Work(from, to), getSelf());
					slice++;
					
					from += 20;
					to +=20;
				}
				
				if(to > (int) msg && from < (int) msg) {
					backend.tell(new Work(from, (int) msg), getSelf());
					slice++;
				}

			}
			else {
				unhandled(msg);
			}
		}
		
	}
	
	public static ActorSystem createActorSystem(String [] args) {
	    final String port = args.length > 0 ? args[0] : "0";
	    final Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port).
	      withFallback(ConfigFactory.parseString("akka.cluster.roles = [backend]")).
	      withFallback(ConfigFactory.load("factorial"));

	    ActorSystem system = ActorSystem.create("ClusterSystem", config);

	    system.actorOf(Props.create(Worker.class), "factorialBackend");

	    system.actorOf(Props.create(ResultHandler.class), "resultHandler");
	    
	    return system;

	}
	
	public static void startServer() {
		createActorSystem(new String [] {"2551"});
		createActorSystem(new String [] {"2552"});
		createActorSystem(new String [] {"0"});
	}

	public static void runTest(int num) {
	    final Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port=0").
	      withFallback(ConfigFactory.parseString("akka.cluster.roles = [backend]")).
	      withFallback(ConfigFactory.load("factorial"));

	    ActorSystem system = ActorSystem.create("ClusterSystem", config);
		
		ActorRef backend = system.actorOf(FromConfig.getInstance().props(),
			      "resultHandlerRouter");
		
		backend.tell(200, ActorRef.noSender());
	}
	
	public static void main(String[] args) throws InterruptedException {
//		startServer();
		runTest(200);
	}

}
