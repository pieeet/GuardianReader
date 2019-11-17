# Guardian Reader
This Android app was initially built for the UDACITY Android Basics Nanodegree.
After spending a lot of time building it I decided to make it available
in the Google Play store. My purpose was mainly to make a demo app for my students
Software Development. The app was approximately 15,000 times downloaded and received
an average raing of 4.5.
Unfortunately then The Guardian staff decided to invalidate my API key, without further
explanation.

To deploy the app for yourself you should add a java class called "Secret" to the utils
package with the following code:

    class Secret {

        private static final String API_KEY = "[YOUR_API_KEY]";

        private Secret() {}

        static String getApiKey() {
            return API_KEY;
        }
    }

There's a demo apk version of the app in the root folder. You should be able to install it on your
Android device.

Good luck!