package st.ggviario.house.singleton;

import st.jigahd.support.sql.Configuration;
import st.jigahd.support.sql.postgresql.PostgresSQL;

public class PostgresSQLSingleton {

    private static Configuration configuration =new Configuration( "localhost", 5432, "ggviario", "ggviario", "1234" );

    private static PostgresSQL postgresSQL = new PostgresSQL( configuration );

    public PostgresSQL getPostgresSQL() {
        return postgresSQL;
    }

    public static PostgresSQL getInstance() {
        return postgresSQL;
    }


}
