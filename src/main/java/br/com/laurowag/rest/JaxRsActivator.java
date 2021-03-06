package br.com.laurowag.rest;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("api")
public class JaxRsActivator extends Application {

	@Override
	public Set<Class<?>> getClasses() {
		final Set<Class<?>> classes = new HashSet<>();
		classes.add(Servico.class);		
		classes.add(KafkaResponseFilter.class);
		return classes;
	}
	
	

}
