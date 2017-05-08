package apps.ahqmrf.mock.util;

/**
 * Created by Lenovo on 4/24/2017.
 */

public final class Const {
    public static final java.lang.String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";

    public final class RequestCodes {
        public static final int REQUEST_ACCESS_FINE_LOCATION = 1;
        public static final int REQUEST_BROWSE_GALLERY       = 2;
        public static final int READ_EXTERNAL_STORAGE        = 3;
    }

    public final class Route {
        public static final String USER_REF      = "user";
        public static final String LOCATION_REF  = "location";
        public static final String FRIEND        = "friend";
        public static final String NOTIFICATION  = "notifications";
        public static final String ONLINE_STATUS = "onlineStatus";
        public static final String CHAT          = "chat";
        public static final String LAST_MESSAGE  = "lastMessage";
    }

    public final class Keys {
        public static final String USER               = "user";
        public static final String LATITUDE           = "latitude";
        public static final String LONGITUDE          = "longitude";
        public static final String STATE              = "state";
        public static final String NAME               = "fullName";
        public static final String USERNAME           = "username";
        public static final String EMAIL              = "email";
        public static final String LOGGED_IN          = "isLoggedIn";
        public static final String PROFILE_PIC        = "imageUrl";
        public static final String SERVICE_STARTED    = "locationService";
        public static final String FRIEND             = "friend";
        public static final String STRANGER           = "stranger";
        public static final String REQUESTED          = "requested";
        public static final String WANNABE            = "wannabe";
        public static final String USER_TYPE          = "userType";
        public static final String STATUS             = "status";
        public static final String NOTIFICATION_COUNT = "countNotifications";
        public static final String ACCEPTED           = "accepted";
        public static final String ONLINE             = "online";
        public static final String OFFLINE            = "offline";
        public static final String TYPING_STATUS = "typing";
        public static final String MESSAGES      = "messages";
        public static final String TIME          = "time";
        public static final String DATE          = "day";
        public static final String MESSAGE       = "message";
        public static final String SENDER        = "sender";
        public static final String RECEIVER      = "receiver";
        public static final String TEXT          = "text";
        public static final String SEEN          = "seen";
    }

    public static final String PREF_NAME = "mock.pref";
}
