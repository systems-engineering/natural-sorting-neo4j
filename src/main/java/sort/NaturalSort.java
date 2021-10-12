package sort;

import org.neo4j.procedure.Name;
import org.neo4j.graphdb.Node;
import org.neo4j.procedure.Procedure;
import org.neo4j.procedure.UserFunction;
import sort.*;
import java.util.*;

public class NaturalSort
{
    @UserFunction
    // @Description("sort.natural_list(coll) sort on primitive collection")
    public List<Object> natural_list(
        @Name("coll") List<Object> coll
        ) {
	    if (coll == null || coll.isEmpty()) return Collections.emptyList();
        List<Object> sorted = new ArrayList<>(coll);
        Collections.sort(sorted, new NaturalOrderComparator());
        return sorted;
    }

    @UserFunction
    // @Description("sort.natural_obj(coll, props) sort on collection of nodes and specific props")
    public List<Node> natural_obj(
        @Name("coll") List<Node> coll,
        @Name("props") String props
        ) {
	    if (props == null) return coll;
	    if (coll == null || coll.isEmpty()) return Collections.emptyList();
        List<Node> sorted = new ArrayList<>(coll);
        NaturalOrderNodeComparator comparator = new NaturalOrderNodeComparator();
        comparator.setProps(props); // This does not work yet, we should set this props somehow
        Collections.sort(sorted, comparator);
        return sorted;
    }
}
