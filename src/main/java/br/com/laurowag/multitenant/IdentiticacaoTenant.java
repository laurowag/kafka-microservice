package br.com.laurowag.multitenant;

import javax.enterprise.context.RequestScoped;

@RequestScoped
public class IdentiticacaoTenant {
    
    private String tenant;

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

}
