package io.github.polysmee.database;

public final class UploadServiceFactory {

    private UploadServiceFactory() {
    }

    private static boolean isTest = false;

    /**
     * @return an upload service that either runs locally or not,
     * depending on state
     */
    public static UploadService getAdaptedInstance() {
        if (isTest) {
            return new LocalUploadService();
        } else {
            return new FirebaseUploadService();
        }
    }

    public static void setTest(boolean isTest) {
        UploadServiceFactory.isTest = isTest;
    }
}
