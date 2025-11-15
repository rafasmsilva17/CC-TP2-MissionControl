package core.missions.common;

public class Coordinate {
    private float latitude;
    private float longitude;

    public Coordinate(float lat, float lon) {
        if (lat < -90 || lat > 90 || lon < -180 || lon > 180) {
            throw new IllegalArgumentException("Invalid latitude or longitude value.");
        }
        this.latitude = lat;
        this.longitude = lon;
    }

    public void setLatitude(float newL) { this.latitude = newL; }

    public void setLongitude(float newL) { this.longitude= newL; }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public float distanceTo(Coordinate other, float planetRadiusKM) {

        double latDistance = Math.toRadians((double)(other.latitude - this.latitude));
        double lonDistance = Math.toRadians((double)(other.longitude - this.longitude));

        double lat1 = Math.toRadians((double)this.latitude);
        double lat2 = Math.toRadians((double)other.latitude);

        // Apply the Haversine formula
        // Haversine formula: a = sin²(Δlat/2) + cos(lat1) * cos(lat2) * sin²(Δlon/2)
        float a = (float)(Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                 + Math.cos(lat1) * Math.cos(lat2)
                 * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2));

        // c = 2 * atan2(√a, √(1−a))
        float c =(float)( 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)));

        return planetRadiusKM * c;
    }
}
