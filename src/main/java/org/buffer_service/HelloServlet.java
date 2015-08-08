// SEE: http://www.eclipse.org/jetty/documentation/9.2.6.v20141205/maven-and-jetty.html???

package org.buffer_service;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Geometry;
import org.geotools.geojson.geom.GeometryJSON;

public class HelloServlet extends HttpServlet
{
    protected int[] parseQueryString(String qs){
        // Given a string representing a query string, return an array
        // representing {x-cord, y-cord, buffer-distance}
        int[] qs_parsed = new int[3];

        // Get param=val pairs
        String[] pv_pairs = qs.split("&");

        for (String pair: pv_pairs) {
            String[] key_value = pair.split("=");
            int i = Integer.parseInt(key_value[1]);
            switch (key_value[0].toLowerCase()) {
                case "x":
                    qs_parsed[0] = i;
                    break;
                case "y":
                    qs_parsed[1] = i;
                    break;
                case "dist":
                    qs_parsed[2] = i;
                    break;
            }
        }
        return qs_parsed;
    }

    protected Point makePoint(int coords[]) {
        // Given an array of an x-cord and a y-cord
        GeometryFactory fact = new GeometryFactory();
        Point p = fact.createPoint(new Coordinate(coords[0], coords[1]));
        return p;
    }

    protected String geom2GeoJSON(Geometry in_geom) {
        // Convert a Geometry to GeoJSON
        GeometryJSON gj = new GeometryJSON();
        String gj_str = gj.toString(in_geom);
        // Hack to wrap String in GeoJSON FeatureCollection form.
        String gj_feature_str = "{\"type\": \"FeatureCollection\",\"features\":" +
                "[{\"type\": \"Feature\",\"geometry\": " + gj_str + ",\"properties\": {}" + "}]}";
        return gj_feature_str;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);

        // Get Query String
        String qs = request.getQueryString();
        int[] qs_parsed = parseQueryString(qs);

        // Create a Point, Buffer it, Convert to GeoJSON
        Point pt_geom = makePoint(qs_parsed);
        Geometry buff_geom = pt_geom.buffer(qs_parsed[2]);
        String gj_feature_str = geom2GeoJSON(buff_geom);

        response.getWriter().println(gj_feature_str);
    }
}