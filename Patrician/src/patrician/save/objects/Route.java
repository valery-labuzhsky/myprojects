package patrician.save.objects;

import patrician.Paths;
import patrician.save.SaveReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 28/03/15.
 *
 * @author ptasha
 */
public class Route {
    private final List<RoutePoint> points = new ArrayList<>();

    public Route(RoutePoint start, List<RoutePoint> points) {
        this.points.add(start);
        RoutePoint next = start;
        while (!isFirst(next = next(next, points))) {
            this.points.add(next);
        }
    }

    private boolean isFirst(RoutePoint point) {
        return point.isFirst();
    }

    private RoutePoint next(RoutePoint point, List<RoutePoint> points) {
        return points.get(point.getNext());
    }

    @Override
    public String toString() {
        String string = null;
        for (RoutePoint point : points) {
            if (string==null) {
                string = ""+point.getTown();
            } else {
                string += " -> "+point.getTown();
            }
        }
        return string;
    }

    public static void main(String[] args) throws IOException {
        List<Route> routes = new ArrayList<>();
        List<RoutePoint> points = new RoutePoint().find(SaveReader.getData(Paths.getGame("base")));
        for (RoutePoint point : points) {
            if (point.isFirst()) {
                routes.add(new Route(point, points));
            }
        }
        for (Route route : routes) {
            System.out.println(route);
        }
    }
}
