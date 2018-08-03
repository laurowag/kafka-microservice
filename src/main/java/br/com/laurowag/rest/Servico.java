package br.com.laurowag.rest;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import br.com.laurowagnitz.model.Cliente;

@Path("servico")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
@Transactional
public class Servico {
	
	@PersistenceContext
	EntityManager em;

	@PUT
	public Response testePut(Cliente cliente) {
		em.createQuery("from cliente").getResultList();
		return Response.ok(cliente).build();
	}
	
	@POST
	public Response testePost(Cliente cliente) {		
		return Response.ok(cliente).build();
	}
}
