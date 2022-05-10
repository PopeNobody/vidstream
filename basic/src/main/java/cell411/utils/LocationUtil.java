package cell411.utils;

import android.location.Location;

import androidx.annotation.NonNull;

import com.parse.model.ParseGeoPoint;

import javax.annotation.Nullable;


@SuppressWarnings("unused")
public class LocationUtil {
  public static final String TAG = LocationUtil.class.getSimpleName();

  @Nullable public static ParseGeoPoint getGeoPoint(@Nullable Location location) {
    if (location == null) {
      return null;
    }
    return getGeoPoint(location.getLatitude(), location.getLongitude());
  }

  @NonNull public static ParseGeoPoint getGeoPoint(CharSequence latitude, CharSequence longitude) {
    return getGeoPoint(
      Double.parseDouble(String.valueOf(latitude)),
      Double.parseDouble(String.valueOf(longitude))
    );
  }

  private static ParseGeoPoint getGeoPoint(double latitude, double longitude) {
    return new ParseGeoPoint(latitude, longitude);
  }

  @Nullable public static ParseGeoPoint getGeoPoint(@Nullable ParseGeoPoint location) {
    return location;
  }

  @NonNull public static Location getLocation(double latitude, double longitude) {
    Location location = new Location("built");
    location.setLatitude(latitude);
    location.setLongitude(longitude);
    return location;
  }

  @Nullable public static Location getLocation(@Nullable ParseGeoPoint location) {
    if (location == null) {
      return null;
    }
    return getLocation(location.getLatitude(), location.getLongitude());
  }

  @Nullable public static Location getLocation(@Nullable Location location) {
    return location;
  }

  public static String formatDistance(double distance, String metric) {
    return formatDistance(distance, !metric.startsWith("km"));
  }

  @NonNull public static String formatDistance(double distance, boolean english) {
    String unit = "km";
    if (english) {
      unit = "mile";
      distance /= 1.6f;
    }
    String plural="";
    if (distance == 1) {
      plural = "s";
    }
    return Util.format("%.2f %s%s", distance, unit, plural);
  }

  public static String formatDistance(double distance) {
    return formatDistance(distance, false);
  }

  static public float distanceBetween(ParseGeoPoint oldValue, ParseGeoPoint newValue) {
    return distanceBetween(oldValue.getLatitude(), newValue.getLongitude(), newValue.getLatitude(),
                           newValue.getLongitude());
  }

  static public float distanceBetween(Location oldValue, Location newValue) {
    float[] array = new float[]{0.0f};
    Location.distanceBetween(oldValue.getLatitude(), newValue.getLongitude(), newValue.getLatitude(),
                             newValue.getLongitude(), array);
    return array[0];
  }

  @NonNull public static String formatLocation(Location location) {
    return formatLocation(location.getLatitude(), location.getLongitude());
  }

  private static String formatLocation(double latitude, double longitude) {
    return Util.format("%.08f, %08f", latitude, longitude);
  }

  public static String formatLocation(ParseGeoPoint location) {
    return formatLocation(location.getLatitude(), location.getLongitude());
  }

  private static float distanceBetween(double latitude, double longitude, double latitude1, double longitude1) {
    float[] array = new float[]{0.0f};
    Location.distanceBetween(latitude, longitude, latitude1, longitude1, array);
    return array[0];
  }
  static boolean metric=false;
  public static String getFormattedDistance(ParseGeoPoint origin, ParseGeoPoint destination)
  {
    double distance;
    if (metric) {
      distance = origin.distanceInKilometersTo(destination);
    } else {
      distance = origin.distanceInMilesTo(destination);
    }
    return formatDistance(distance);
  }

  public static ParseGeoPoint getMean(ParseGeoPoint... points) {
    double meanLat = 0;
    double meanLng = 0;
    for (ParseGeoPoint parseGeoPoint : points) {
      meanLat += parseGeoPoint.getLatitude();
      meanLng += parseGeoPoint.getLongitude();
    }
    meanLat /= points.length;
    meanLng /= points.length;
    return new ParseGeoPoint(meanLat, meanLng);
  }
}

