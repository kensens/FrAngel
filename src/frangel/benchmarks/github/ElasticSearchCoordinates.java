package frangel.benchmarks.github;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.spatial4j.exception.InvalidShapeException;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.BenchmarkUtils;
import frangel.benchmarks.TaskCreator;

public enum ElasticSearchCoordinates implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.GITHUB.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("elasticsearch_coordinates")
                .setInputTypes(Edge.class, Coordinate[].class)
                .setInputNames("component", "coordinates")
                .setOutputType(Coordinate[].class)
                .excludeMethods(
                        // these can destroy Edge.MAX_COORDINATE (which should be a constant)
                        BenchmarkUtils.getMethod(Coordinate.class, "setCoordinate", Coordinate.class),
                        BenchmarkUtils.getMethod(Coordinate.class, "setOrdinate", int.class, double.class))
                .addTags(Tag.FOR);

        // no unit tests

        // added examples
        task.addExample(new Example()
                .setInputs(() -> {
                    Edge head = new Edge(new Coordinate(11, 22), null);
                    Edge rest = new Edge(new Coordinate(34, 56), new Edge(new Coordinate(7, 8), head));
                    head.setNext(rest);
                    return new Object[] { head, new Coordinate[5] };
                })
                .setOutput(new Coordinate[] {
                        new Coordinate(34, 56), new Coordinate(7, 8), new Coordinate(11, 22),
                        new Coordinate(34, 56), new Coordinate(7, 8)
                }));

        task.addExample(new Example()
                .setInputs(() -> new Object[] {
                        new Edge(new Coordinate(11, 22), new Edge(new Coordinate(34, 56), new Edge(new Coordinate(7, 8), null))),
                        new Coordinate[2]
                })
                .setOutput(new Coordinate[] {new Coordinate(34, 56), new Coordinate(7, 8)}));

        task.addExample(new Example()
                .setInputs(() -> new Object[] {
                        new Edge(new Coordinate(11, 22), new Edge(new Coordinate(34, 56), new Edge(new Coordinate(7, 8), null))),
                        new Coordinate[1]
                })
                .setOutput(new Coordinate[] {new Coordinate(34, 56)}));

        return task;
    }

    // from elasticsearch-master/server/src/main/java/org/elasticsearch/common/geo/builders/PolygonBuilder.java
    static Coordinate[] solution(Edge component, Coordinate[] coordinates) {
        for (int i = 0; i < coordinates.length; i++) {
            coordinates[i] = (component = component.next).coordinate;
        }
        return coordinates;
    }

    // from elasticsearch-master/server/src/main/java/org/elasticsearch/common/geo/builders/ShapeBuilder.java
    // changed all access modifiers to "public" (so FrAngel can use)
    public static final class Edge {
        public Coordinate coordinate; // coordinate of the start point
        public Edge next; // next segment
        public Coordinate intersect; // potential intersection with dateline
        public int component = -1; // id of the component this edge belongs to
        public static final Coordinate MAX_COORDINATE = new Coordinate(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);

        public Edge(Coordinate coordinate, Edge next, Coordinate intersection) {
            this.coordinate = coordinate;
            // use setter to catch duplicate point cases
            this.setNext(next);
            this.intersect = intersection;
            if (next != null) {
                this.component = next.component;
            }
        }

        public Edge(Coordinate coordinate, Edge next) {
            this(coordinate, next, Edge.MAX_COORDINATE);
        }

        public void setNext(Edge next) {
            // don't bother setting next if its null
            if (next != null) {
                // self-loop throws an invalid shape
                if (this.coordinate.equals(next.coordinate)) {
                    throw new InvalidShapeException("Provided shape has duplicate consecutive coordinates at: " + this.coordinate);
                }
                this.next = next;
            }
        }

        /**
         * Set the intersection of this line segment to the given position
         *
         * @param position
         *            position of the intersection [0..1]
         * @return the {@link Coordinate} of the intersection
         */
        public Coordinate intersection(double position) {
            return intersect = position(coordinate, next.coordinate, position);
        }

        public static Coordinate position(Coordinate p1, Coordinate p2, double position) {
            if (position == 0) {
                return p1;
            } else if (position == 1) {
                return p2;
            } else {
                final double x = p1.x + position * (p2.x - p1.x);
                final double y = p1.y + position * (p2.y - p1.y);
                return new Coordinate(x, y);
            }
        }

        @Override
        public String toString() {
            return "Edge[Component=" + component + "; start=" + coordinate + " " + "; intersection=" + intersect + "]";
        }
    }
}
