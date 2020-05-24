import core.Line;
import core.Station;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class RouteCalculatorTest {

    private  final static double DELTA = 0.000001;
    private  final static double ROUTE_TIME = 2.5;
    private  final static double CONNECTION_TIME = 3.5;
    StationIndex index;
    RouteCalculator rt;
    List<Station> threeTransfersRoute;
    List<Station> twoTransfersRoute;
    List<Station> noTransfersRoute;

    Station red;
    Station green;
    Station black;
    Station blue;
    Station purple;
    Station yellow;
    Station grey;
    Station orange;
    Station pink;

    @Before
    public void setUp() throws Exception {
        // [red]   [yellow] <-> [orange]
        //   |        |          |
        // [green] [purple]    [pink]
        //   |       |           |
        // [black]<->[blue]    [grey]

        index = new StationIndex();

        Line line1 = new Line(1, "Первая линия");
        Line line2 = new Line(2, "Вторая линия");
        Line line3 = new Line(3, "Третья линия");

        red = new Station("Красная", line1);
        green = new Station("Зеленая", line1);
        black = new Station("Черная", line1);
        blue = new Station("Синяя", line2);
        purple = new Station("Фиолетовая", line2);
        yellow = new Station("Желтая", line2);
        orange = new Station("Оранжевая", line3);
        pink = new Station("Розовая", line3);
        grey = new Station("Серая", line3);

        Stream.of(line1, line2, line3).forEach(index::addLine);
        Stream.of(red, green, black, blue, purple, yellow, orange, pink, grey).peek(s -> s.getLine().addStation(s)).forEach(index::addStation);
        index.addConnection(Stream.of(black, blue).collect(Collectors.toList()));
        index.addConnection(Stream.of(yellow, orange).collect(Collectors.toList()));
        index.getConnectedStations(black);
        index.getConnectedStations(yellow);

        rt = new RouteCalculator(index);

        noTransfersRoute = Stream.of(red, green, black).collect(Collectors.toList());
        twoTransfersRoute = Stream.of(red, green, black, blue, purple, yellow).collect(Collectors.toList());
        threeTransfersRoute = Stream.of(red, green, black, blue, purple, yellow, orange, pink, grey).collect(Collectors.toList());

    }

    @Test
    public void testGetShortestRouteWithNoTransfer() {

        List<Station> actualWithNoTransfer = rt.getShortestRoute(red, black);
        List<Station> exceptedWithNoTransfer = noTransfersRoute;
        assertEquals(exceptedWithNoTransfer, actualWithNoTransfer);
    }

    @Test
    public void testGetShortestRouteWithTwoTransfer() {
        List<Station> actualWithTwoTransfer = rt.getShortestRoute(red, yellow);
        List<Station> exceptedWithTwoTransfer = twoTransfersRoute;
        assertEquals(exceptedWithTwoTransfer, actualWithTwoTransfer);

    }

    @Test
    public void testGetShortestRouteWithThreeTransfer() {
        List<Station> actualWithThreeTransfer = rt.getShortestRoute(red,grey);
        List<Station> exceptedWithThreeTransfer = threeTransfersRoute;
        assertEquals(exceptedWithThreeTransfer, actualWithThreeTransfer);

    }

    @Test
    public void testCalculateDurationWithTwoConnections() {

        double actual = RouteCalculator.calculateDuration(twoTransfersRoute);
        double excepted = ROUTE_TIME*2 + CONNECTION_TIME + ROUTE_TIME*2;
        assertEquals(excepted, actual, DELTA);
    }

    @Test
    public void testCalculateDurationWithoutConnections() {
        double actual = RouteCalculator.calculateDuration(noTransfersRoute);
        double excepted = ROUTE_TIME*2;
        assertEquals(excepted, actual, DELTA);
    }

    @Test
    public void testCalculateDurationWithMoreConnections() {
        double actual = RouteCalculator.calculateDuration(threeTransfersRoute);
        double excepted = ROUTE_TIME*2 + CONNECTION_TIME + ROUTE_TIME*2 + CONNECTION_TIME + ROUTE_TIME*2;
        assertEquals(excepted, actual, DELTA);
    }
}
