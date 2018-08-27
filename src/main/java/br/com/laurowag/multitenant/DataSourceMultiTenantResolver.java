package br.com.laurowag.multitenant;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.jboss.logging.Logger;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;

public class DataSourceMultiTenantResolver implements CurrentTenantIdentifierResolver {

    private static Logger log = Logger.getLogger(DataSourceMultiTenantResolver.class);

    @SuppressWarnings("unchecked")
    @Override
    public String resolveCurrentTenantIdentifier() {
        String multiTenancy = "SCHEMA";//System.getProperty("agrotis.hibernate.multitenancy");
        if ((multiTenancy == null) || (!(multiTenancy.equals("DATABASE") || multiTenancy.equals("SCHEMA")))) {
            return null;
        }
        
        try {
            InitialContext initialContext = new InitialContext();
            BeanManager bm = (BeanManager) initialContext.lookup("java:comp/BeanManager");
            Bean<IdentiticacaoTenant> bean = (Bean<IdentiticacaoTenant>) bm.getBeans(IdentiticacaoTenant.class).iterator().next();
            CreationalContext<IdentiticacaoTenant> ctx = bm.createCreationalContext(bean);
            IdentiticacaoTenant tenant = (IdentiticacaoTenant) bm.getReference(bean, IdentiticacaoTenant.class, ctx);
            log.debug("TENANT: " + tenant.getTenant());
            if ((tenant.getTenant() == null) || (tenant.getTenant().equals(""))) {
                return "Default";
            } else {
                return tenant.getTenant();   
            }
        } catch (Exception erro) {
            log.error("Erro ao carregar o tenant. Usando tenant default.", erro);
            return "Default";
        }

    }

    // Should we validate that the tenant identifier on "current sessions" that already exist when
    // CurrentSessionContext.currentSession() is called matches the value returned here from
    // resolveCurrentTenantIdentifier()?
    @Override
    public boolean validateExistingCurrentSessions() {
        // true indicates that the extra validation will be performed; false indicates it will not
        return false;
    }

}
