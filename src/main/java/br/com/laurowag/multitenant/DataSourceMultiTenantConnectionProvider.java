package br.com.laurowag.multitenant;

import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.jboss.logging.Logger;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.sql.DataSource;

public class DataSourceMultiTenantConnectionProvider implements MultiTenantConnectionProvider  {

    private static final long serialVersionUID = 1L;
    private String baseJndiName;

    private static Logger log = Logger.getLogger(MultiTenantConnectionProvider.class);

    public DataSourceMultiTenantConnectionProvider() {
        this.baseJndiName = "java:jboss/datasources/ConexaoDS";
    }
    
    public DataSourceMultiTenantConnectionProvider(String baseJndiName) {
        this.baseJndiName = baseJndiName;
    }

    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
        log.info("*** Multitenant - releaseAnyConnection");
        if ((connection != null) && (!connection.isClosed())) {
            connection.close();
        }
    }

    @Override
    public Connection getConnection(String tenantIdentifier) throws SQLException {
        try {
            log.info("*** Multitenant - getConnection TENANT: " + tenantIdentifier);
            InitialContext context = new InitialContext();
            DataSource tenantDataSource = null;
            String tipoTenant = "SCHEMA";//System.getProperty("agrotis.hibernate.multitenancy");
            if (tipoTenant.equals("DATABASE")) {
                if (tenantIdentifier.equals("") || tenantIdentifier.equalsIgnoreCase("Default")) {
                    tenantDataSource = (DataSource) context.lookup(baseJndiName);
                } else {
                    tenantDataSource = (DataSource) context.lookup(baseJndiName + "/" + tenantIdentifier);
                }
                return tenantDataSource.getConnection();
            } else {
                tenantDataSource = (DataSource) context.lookup(baseJndiName);
                Connection connection = tenantDataSource.getConnection();
                try {
                    if ((tenantIdentifier.equals("")) || (tenantIdentifier.equalsIgnoreCase("Default"))) {
                        if (!isSqlServer(connection) && !isMySql(connection)) {
                            connection.createStatement().execute("SET SEARCH_PATH TO public;");
                        }
                    } else {
                        if (isSqlServer(connection)) {
                            connection.createStatement().execute("REVERT;");
                            connection.createStatement().execute("EXECUTE AS USER = '" + tenantIdentifier + "';");
                        } else if (isMySql(connection)) {
                            connection.createStatement().execute("USE " + tenantIdentifier + ";");
                        } else {
                            connection.createStatement().execute("SET SEARCH_PATH TO " + tenantIdentifier + ";");
                        }
                    }
                    return connection;
                } catch (Exception e) {
                    if ((connection != null) && (!connection.isClosed())) {
                        connection.close();
                    }
                    throw e;
                }
            }
        } catch (Exception e) {
            throw new SQLException("Erro ao acessar data source: " + baseJndiName + "/" 
                            + tenantIdentifier + ": " + e.getMessage(), e);
        }
    }

    @Override
    public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
        log.info("*** Multitenant - releaseConnection");
        if ((connection != null) && (!connection.isClosed())) {
            connection.close();
        }
    }

    private boolean isSqlServer(Connection connection) throws SQLException {
        return connection.getMetaData().getClass().getName().contains("SQLServer");
    }
    
    private boolean isMySql(Connection connection) throws SQLException {
        return connection.getMetaData().getClass().getName().toLowerCase().contains("mysql");
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }

    @Override
    public Connection getAnyConnection() throws SQLException {
        log.info("*** Multitenant - getAnyConnection");
        return getConnection("Default");
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean isUnwrappableAs(Class arg0) {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> arg0) {
        return null;
    }

}
