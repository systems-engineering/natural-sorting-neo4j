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
    // @Description("sort.natural_list(collection) sort on primitive collection")
    public List<Object> natural_list(
        @Name("collection") List<Object> collection
        ) {
	    if (collection == null || collection.isEmpty()) return Collections.emptyList();
        List<Object> sorted = new ArrayList<>(collection);
        Collections.sort(sorted, new NaturalOrderComparator());
        return sorted;
    }

    @UserFunction
    // @Description("sort.natural_obj(collection, props) sort on collection of nodes and specific props")
    public List<Node> natural_obj(
        @Name("collection") List<Node> collection,
        @Name("props") String props
        ) {
	    if (props == null) return collection;
	    if (collection == null || collection.isEmpty()) return Collections.emptyList();
        List<Node> sorted = new ArrayList<>(collection);
        NaturalOrderNodeComparator comparator = new NaturalOrderNodeComparator();
        comparator.setProps(props); // This does not work yet, we should set this props somehow
        Collections.sort(sorted, comparator);
        return sorted;
    }
}
