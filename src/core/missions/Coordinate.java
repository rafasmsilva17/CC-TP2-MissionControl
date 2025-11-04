package core.missions;

public class Coordinate {
    private final double latitude;
    private final double longitude;

    public Coordinate(double lat, double lon) {
        if (lat < -90 || lat > 90 || lon < -180 || lon > 180) {
            throw new IllegalArgumentException("Invalid latitude or longitude value.");
        }
        this.latitude = lat;
        this.longitude = lon;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double distanceTo(Coordinate other, double planetRadiusKM) {

        double latDistance = Math.toRadians(other.latitude - this.latitude);
        double lonDistance = Math.toRadians(other.longitude - this.longitude);

        double lat1 = Math.toRadians(this.latitude);
        double lat2 = Math.toRadians(other.latitude);

        // Apply the Haversine formula
        // Haversine formula: a = sin²(Δlat/2) + cos(lat1) * cos(lat2) * sin²(Δlon/2)
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                 + Math.cos(lat1) * Math.cos(lat2)
                 * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        // c = 2 * atan2(√a, √(1−a))
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return planetRadiusKM * c;
    }
}
