package org.akka;

import akka.actor.ActorSystem;
import akka.actor.Props;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class AkkiPiMain {

	public static void main(String [] args) {
		final String port = args.length > 0 ? args[0] : "0";
		final Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port)
				.withFallback(ConfigFactory.parseString("akka.cluster.roles=[backend]"))
				.withFallback(ConfigFactory.load("pi"));
		
		ActorSystem system = ActorSystem.create("ClusterSystem", config);
		system.actorOf(Props.create(AkkaPi.class), "piBackend");
	}
}
