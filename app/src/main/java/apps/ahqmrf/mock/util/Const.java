package apps.ahqmrf.mock.util;

/**
 * Created by Lenovo on 4/24/2017.
 */

public final class Const {
    public final class RequestCodes {
        public static final int REQUEST_ACCESS_FINE_LOCATION = 1;
        public static final int REQUEST_BROWSE_GALLERY       = 2;
        public static final int READ_EXTERNAL_STORAGE        = 3;
    }

    public final class Route {
        public static final String USER_REF     = "user";
        public static final String LOCATION_REF = "location";
    }

    public final class Keys {
        public static final String USER            = "user";
        public static final String LATITUDE        = "latitude";
        public static final String LONGITUDE       = "longitude";
        public static final String STATE           = "state";
        public static final String NAME            = "fullName";
        public static final String USERNAME        = "username";
        public static final String EMAIL           = "email";
        public static final String LOGGED_IN       = "isLoggedIn";
        public static final String PROFILE_PIC     = "imageUrl";
    }

    public static final String PREF_NAME = "mock.pref";
}
