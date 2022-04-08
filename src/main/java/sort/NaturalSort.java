package sort;

import org.neo4j.procedure.Name;
import org.neo4j.graphdb.Node;
import org.neo4j.procedure.UserFunction;
import java.util.*;

public class NaturalSort
{
    public static final char ASCENDING_ORDER_CHAR = '^';

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
        Boolean reverseOrder = reverseOrder(props);
        String pureProp = cleanProperty(props);
        comparator.setProps(pureProp);
        Collections.sort(sorted, comparator);

        if(reverseOrder == true){
            Collections.reverse(sorted);
        }

        return sorted;
    }

    @UserFunction
    // @Description("sort.natural_map(collection, props) sort on collection(map) of
    // objects and specific props")
    public List<Map> natural_map(
            @Name("collection") List<Map> collection,
            @Name("props") String props) {
        if (props == null)
            return collection;
        if (collection == null || collection.isEmpty())
            return Collections.emptyList();
        List<Map> sorted = new ArrayList<>(collection);
        NaturalOrderObjComparator comparator = new NaturalOrderObjComparator();
        Boolean reverseOrder = reverseOrder(props);
        String pureProp = cleanProperty(props);
        comparator.setProps(pureProp);
        Collections.sort(sorted, comparator);

        if (reverseOrder == true) {
            Collections.reverse(sorted);
        }

        return sorted;
    }

    public Boolean reverseOrder(String props) {
        return props.charAt(0) == ASCENDING_ORDER_CHAR ? true : false;
    }

    public String cleanProperty(String props) {
        return props.charAt(0) == ASCENDING_ORDER_CHAR ? props.substring(1) : props;
    }
}
