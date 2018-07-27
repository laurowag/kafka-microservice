package br.com.laurowag;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("servico")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class Servico {

	@PUT
	public Response testePut(Cliente cliente) {		
		return Response.ok(cliente).build();
	}
	
	@POST
	public Response testePost(Cliente cliente) {		
		return Response.ok(cliente).build();
	}
}
