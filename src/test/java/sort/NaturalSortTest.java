package example;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Value;
import org.neo4j.harness.Neo4j;
import org.neo4j.harness.Neo4jBuilders;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GetRelationshipTypesTests {

    private Driver driver;
    private Neo4j embeddedDatabaseServer;

    @BeforeAll
    void initializeNeo4j() {
        this.embeddedDatabaseServer = Neo4jBuilders.newInProcessBuilder()
                .withDisabledServer()
                .withProcedure(GetRelationshipTypes.class)
                .build();

        this.driver = GraphDatabase.driver(embeddedDatabaseServer.boltURI());
    }

    @AfterAll
    void closeDriver() {
        this.driver.close();
        this.embeddedDatabaseServer.close();
    }

    @AfterEach
    void cleanDb() {
        try (Session session = driver.session()) {
            session.run("MATCH (n) DETACH DELETE n");
        }
    }

    /**
     * We should be getting the correct values when there is only one type in each
     * direction
     */
    @Test
    public void shouldReturnTheTypesWhenThereIsOneEachWay() {
        final String expectedIncoming = "INCOMING";
        final String expectedOutgoing = "OUTGOING";

        // In a try-block, to make sure we close the session after the test
        try (Session session = driver.session()) {

            // Create our data in the database.
            session.run(String.format("CREATE (:Person)-[:%s]->(:Movie {id:1})-[:%s]->(:Person)", expectedIncoming,
                    expectedOutgoing));

            // Execute our procedure against it.
            Record record = session.run(
                    "MATCH (u:Movie {id:1}) CALL example.getRelationshipTypes(u) YIELD outgoing, incoming RETURN outgoing, incoming")
                    .single();

            // Get the incoming / outgoing relationships from the result
            assertThat(record.get("incoming").asList(Value::asString)).containsOnly(expectedIncoming);
            assertThat(record.get("outgoing").asList(Value::asString)).containsOnly(expectedOutgoing);
        }
    }

    /**
     * We should only get distinct values from our procedure
     */
    @Test
    public void shouldReturnDistinctTypes() {
        final String expectedIncoming = "INCOMING";
        final String expectedOutgoing = "OUTGOING";

        try (Session session = driver.session()) {
            session.run(String.format("CREATE (:Person)-[:%s]->(:Movie {id:1})<-[:%s]-(:Person)", expectedIncoming,
                    expectedIncoming));
            session.run(String.format("MATCH (m:Movie {id:1}) CREATE (:Person)<-[:%s]-(m)-[:%s]->(:Person)",
                    expectedOutgoing, expectedOutgoing));

            Record record = session.run(
                    "MATCH (u:Movie {id:1}) CALL example.getRelationshipTypes(u) YIELD outgoing, incoming RETURN outgoing, incoming")
                    .single();

            assertThat(record.get("incoming").asList(Value::asString)).containsOnly(expectedIncoming);
            assertThat(record.get("outgoing").asList(Value::asString)).containsOnly(expectedOutgoing);
        }
    }

    /**
     * We should check that if there are no outgoing relationships, the procedure
     * still works.
     */
    @Test
    public void shouldReturnIncomingIfThereAreNoOutgoingTypes() {
        final String expectedIncoming = "INCOMING";

        // In a try-block, to make sure we close the session after the test
        try (Session session = driver.session()) {

            // Create our data in the database.
            session.run(String.format("CREATE (:Person)-[:%s]->(:Movie {id:1})", expectedIncoming));

            // Execute our procedure against it.
            Record record = session.run(
                    "MATCH (u:Movie {id:1}) CALL example.getRelationshipTypes(u) YIELD outgoing, incoming RETURN outgoing, incoming")
                    .single();

            // Get the incoming / outgoing relationships from the result
            assertThat(record.get("incoming").asList(Value::asString)).containsOnly(expectedIncoming);
            assertThat(record.get("outgoing").asList(Value::asString)).isEmpty();
        }
    }

    /**
     * We should check that if there are no relationships, the procedure still
     * works.
     */
    @Test
    public void shouldReturnIncomingIfThereAreNoRelationships() {
        // In a try-block, to make sure we close the session after the test
        try (Session session = driver.session()) {

            // Create our data in the database.
            session.run("CREATE (:Movie {id:1})");

            // Execute our procedure against it.
            Record record = session.run(
                    "MATCH (u:Movie {id:1}) CALL example.getRelationshipTypes(u) YIELD outgoing, incoming RETURN outgoing, incoming")
                    .single();

            // Get the incoming / outgoing relationships from the result
            assertThat(record.get("incoming").asList(Value::asString)).isEmpty();
            assertThat(record.get("outgoing").asList(Value::asString)).isEmpty();
        }
    }

    /**
     * We should check that if there are no incoming relationships, the procedure
     * still works.
     */
    @Test
    public void shouldReturnOutgoingIfThereAreNoIncoming() {
        final String expectedOutgoing = "OUTGOING";

        // In a try-block, to make sure we close the session after the test
        try (Session session = driver.session()) {

            // Create our data in the database.
            session.run(String.format("CREATE (:Movie {id:1})-[:%s]->(:Person)", expectedOutgoing));

            // Execute our procedure against it.
            Record record = session.run(
                    "MATCH (u:Movie {id:1}) CALL example.getRelationshipTypes(u) YIELD outgoing, incoming RETURN outgoing, incoming")
                    .single();

            // Get the incoming / outgoing relationships from the result
            assertThat(record.get("incoming").asList(Value::asString)).isEmpty();
            assertThat(record.get("outgoing").asList(Value::asString)).containsOnly(expectedOutgoing);
        }
    }

    /**
     * We should check that if the node doesn't exist, that we just return an empty
     * stream.
     */
    @Test
    public void shouldReturnEmptyStreamIfInputNodeIsNull() {
        try (Session session = driver.session()) {
            Result result = session.run(
                    "MATCH (u:Movie {id:1}) CALL example.getRelationshipTypes(u) YIELD outgoing, incoming RETURN outgoing, incoming");
            assertThat(result.hasNext()).isFalse();
        }
    }

    /**
     * We should check that if there are multiple relationships types, all are
     * returned.
     */
    @Test
    public void shouldReturnAllTypes() {
        final String expectedIncoming_1 = "INCOMING_1";
        final String expectedIncoming_2 = "INCOMING_2";
        final String expectedOutgoing_1 = "OUTGOING_1";
        final String expectedOutgoing_2 = "OUTGOING_2";

        // In a try-block, to make sure we close the session after the test
        try (Session session = driver.session()) {

            // Create our data in the database.
            session.run(String.format("CREATE (:Person)-[:%s]->(:Movie {id:1})<-[:%s]-(:Person)", expectedIncoming_1,
                    expectedIncoming_2));
            session.run(String.format("MATCH (m:Movie {id:1}) CREATE (:Person)<-[:%s]-(m)-[:%s]->(:Person)",
                    expectedOutgoing_1, expectedOutgoing_2));

            // Execute our procedure against it.
            Record record = session.run(
                    "MATCH (u:Movie {id:1}) CALL example.getRelationshipTypes(u) YIELD outgoing, incoming RETURN outgoing, incoming")
                    .single();

            assertThat(record.get("incoming").asList(Value::asString)).containsOnly(expectedIncoming_1,
                    expectedIncoming_2);
            assertThat(record.get("outgoing").asList(Value::asString)).containsOnly(expectedOutgoing_1,
                    expectedOutgoing_2);
        }
    }
}
