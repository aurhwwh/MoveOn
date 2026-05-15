package MoveOn

import com.graphhopper.GraphHopper
import com.graphhopper.config.Profile

object GraphHopperProvider {

    val hopper: GraphHopper by lazy {

        val hopper = GraphHopper()

        val file = javaClass.classLoader.getResource("maps/SanktPetersburg.osm.pbf")
            ?: error("OSM file not found in resources")

        hopper.osmFile = file.file

        hopper.graphHopperLocation =
            "graph-cache"

        hopper.setProfiles(
            Profile("foot")
                .setVehicle("foot")
                .setWeighting("fastest"),

            Profile("bike")
                .setVehicle("bike")
                .setWeighting("fastest")
        )

        hopper.importOrLoad()

        println("GraphHopper loaded")

        hopper
    }
}

fun toGeoJson(response: RouteOptionsResponse): String {

    val features = mutableListOf<String>()

    response.centralPoint?.let { center ->
        features.add("""
        {
          "type": "Feature",
          "geometry": {
            "type": "Point",
            "coordinates": [${center.lon}, ${center.lat}]
          },
          "properties": {
            "type": "center"
          }
        }
        """.trimIndent())
    }

    response.points?.forEachIndexed { index, point ->
        features.add("""
        {
          "type": "Feature",
          "geometry": {
            "type": "Point",
            "coordinates": [${point.lon}, ${point.lat}]
          },
          "properties": {
            "type": "target",
            "index": $index
          }
        }
        """.trimIndent())
    }

    response.routes?.forEachIndexed { index, route ->

        val coords = route.points.map {
            listOf(it.lon, it.lat)
        }

        // сам маршрут
        features.add("""
        {
          "type": "Feature",
          "geometry": {
            "type": "LineString",
            "coordinates": $coords
          },
          "properties": {
            "type": "route",
            "routeIndex": $index,
            "distance": ${route.distance},
            "time": ${route.time}
          }
        }
        """.trimIndent())

        val end = route.points.lastOrNull()
        if (end != null) {
            features.add("""
            {
              "type": "Feature",
              "geometry": {
                "type": "Point",
                "coordinates": [${end.lon}, ${end.lat}]
              },
              "properties": {
                "type": "route_end",
                "routeIndex": $index
              }
            }
            """.trimIndent())
        }
    }

    return """
    {
      "type": "FeatureCollection",
      "features": [${features.joinToString(",")}]
    }
    """.trimIndent()
}