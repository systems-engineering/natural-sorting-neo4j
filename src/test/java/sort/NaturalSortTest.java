package sort;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.neo4j.driver.v1.Config;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.neo4j.harness.ServerControls;
import org.neo4j.harness.TestServerBuilders;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NaturalSortTest {

    private static final Config driverConfig = Config.build().withoutEncryption().toConfig();
    private ServerControls embeddedDatabaseServer;

    @BeforeAll
    void initializeNeo4j() {

        this.embeddedDatabaseServer = TestServerBuilders
                .newInProcessBuilder()
                .withFunction(NaturalSort.class)
                .newServer();
    }

    @Test
    public void shouldReturnSortedValues() {
        try( Driver driver = GraphDatabase.driver(embeddedDatabaseServer.boltURI(), driverConfig);
             Session session = driver.session()) {
            String result = session.run( "RETURN sort.natural_list(['=A2', '=A10', '=A1']) AS result").single().get("result").asString();
            assertThat(result).isEqualTo(( "['=A1', '=A2', '=A10']" ));
        }
    }
}
