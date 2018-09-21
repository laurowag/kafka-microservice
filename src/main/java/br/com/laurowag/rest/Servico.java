package br.com.laurowag.rest;

import br.com.laurowag.multitenant.IdentiticacaoTenant;
import br.com.laurowagnitz.model.Cliente;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.transaction.Transactional;
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
@RequestScoped
@Transactional
public class Servico {
	
	@PersistenceUnit
	EntityManagerFactory emf;
	
	@Inject
	IdentiticacaoTenant tenant;

	@PUT
	public Response testePut(Cliente cliente) {
	    //tenant.setTenant("tenant");
	    //EntityManager em = emf.createEntityManager();	    
		//em.createQuery("from Cliente").getResultList();
		return Response.ok(cliente).build();
	}
	
	@POST
	public Response testePost(Cliente cliente) {		
		return Response.ok(cliente).build();
	}
}
