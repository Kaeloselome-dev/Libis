
package bac.ac.libis.config;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConfig {

    private static DataSource dataSource;

    public static Connection getConnection() throws SQLException {

        if (dataSource == null) {
            try {
                InitialContext context = new InitialContext();

                dataSource = (DataSource) context.lookup(
                        "java:comp/env/jdbc/libis"
                );

            } catch (NamingException e) {
                throw new SQLException(
                        "Failed to locate the LIBIS database DataSource via JNDI.",
                        e
                );
            }
        }

        return dataSource.getConnection();
    }
}
