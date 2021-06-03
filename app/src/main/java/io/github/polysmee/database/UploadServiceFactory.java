package io.github.polysmee.database;

public final class UploadServiceFactory {

    private UploadServiceFactory() {
    }

    private static boolean runLocally = false;

    /**
     * @return an upload service that either runs locally or not,
     * depending on state
     */
    public static UploadService getAdaptedInstance() {
        if (runLocally) {
            return new LocalUploadService();
        } else {
            return new FirebaseUploadService();
        }
    }

    public static void setLocal(boolean isLocal) {
        UploadServiceFactory.runLocally = isLocal;
    }
}
