package chat.atc.tges.tgeschat.Mensajes;

/**
 * Created by vamsi on 24-Feb-16.
 */

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.io.File;

public class FilePath {

    /**
     * Method to return file path of selected file
     *
     * @param context
     * @param uri
     * @return path of the selected  file from directory
     */
    public static String getPath(final Context context, final Uri uri) {

        // check here to KITKAT or new version
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        String sdcardpath="";

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {

            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    //String hola= Environment.getExternalStorageDirectory() + "/" + split[1];
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }

/*
                String[] projection = {MediaStore.MediaColumns.DATA};
                String path="";
                ContentResolver cr = context.getContentResolver();
                Cursor metaCursor = cr.query(uri, projection, null, null, null);
                if (metaCursor != null) {
                    try {
                        if (metaCursor.moveToFirst()) {
                            path = metaCursor.getString(0);
                        }
                    } finally {
                        metaCursor.close();
                    }
                }



                // Handle SD cards
                File file = getFileIfExists("/storage/extSdCard", split[1]);
                if(file != null){
                    return file.getAbsolutePath();
                }
                file = getFileIfExists("/storage/sdcard1", split[1]);
                if(file != null){
                    return file.getAbsolutePath();
                }
                file = getFileIfExists("/storage/usbcard1", split[1]);
                if(file != null){
                    return file.getAbsolutePath();
                }
                file = getFileIfExists("/storage/sdcard0", split[1]);
                if(file != null){
                    return file.getAbsolutePath();
                }

                // SD Cards
                if (new File("/data/sdext4/").exists() && new File("/data/sdext4/").canRead()){
                    sdcardpath = "/data/sdext4/";
                }
                if (new File("/data/sdext3/").exists() && new File("/data/sdext3/").canRead()){
                    sdcardpath = "/data/sdext3/";
                }
                if (new File("/data/sdext2/").exists() && new File("/data/sdext2/").canRead()){
                    sdcardpath = "/data/sdext2/";
                }
                if (new File("/data/sdext1/").exists() && new File("/data/sdext1/").canRead()){
                    sdcardpath = "/data/sdext1/";
                }
                if (new File("/data/sdext/").exists() && new File("/data/sdext/").canRead()){
                    sdcardpath = "/data/sdext/";
                }

                //MNTS

                if (new File("mnt/sdcard/external_sd/").exists() && new File("mnt/sdcard/external_sd/").canRead()){
                    sdcardpath = "mnt/sdcard/external_sd/";
                }
                if (new File("mnt/extsdcard/").exists() && new File("mnt/extsdcard/").canRead()){
                    sdcardpath = "mnt/extsdcard/";
                }
                if (new File("mnt/external_sd/").exists() && new File("mnt/external_sd/").canRead()){
                    sdcardpath = "mnt/external_sd/";
                }
                if (new File("mnt/emmc/").exists() && new File("mnt/emmc/").canRead()){
                    sdcardpath = "mnt/emmc/";
                }
                if (new File("mnt/sdcard0/").exists() && new File("mnt/sdcard0/").canRead()){
                    sdcardpath = "mnt/sdcard0/";
                }
                if (new File("mnt/sdcard1/").exists() && new File("mnt/sdcard1/").canRead()){
                    sdcardpath = "mnt/sdcard1/";
                }
                if (new File("mnt/sdcard/").exists() && new File("mnt/sdcard/").canRead()){
                    sdcardpath = "mnt/sdcard/teclado windows en mac.jpg";
                    return sdcardpath;
                }

                //Storages
                if (new File("/storage/removable/sdcard1/").exists() && new File("/storage/removable/sdcard1/").canRead()){
                    sdcardpath = "/storage/removable/sdcard1/";
                }
                if (new File("/storage/external_SD/").exists() && new File("/storage/external_SD/").canRead()){
                    sdcardpath = "/storage/external_SD/";
                }
                if (new File("/storage/ext_sd/").exists() && new File("/storage/ext_sd/").canRead()){
                    sdcardpath = "/storage/ext_sd/";
                }
                if (new File("/storage/sdcard1/").exists() && new File("/storage/sdcard1/").canRead()){
                    sdcardpath = "/storage/sdcard1/";
                }
                if (new File("/storage/sdcard0/").exists() && new File("/storage/sdcard0/").canRead()){
                    sdcardpath = "/storage/sdcard0/";
                }
                if (new File("/storage/sdcard/").exists() && new File("/storage/sdcard/").canRead()){
                    sdcardpath = "/storage/sdcard/";
                }
                if (sdcardpath.contentEquals("")){
                    sdcardpath = Environment.getExternalStorageDirectory().getAbsolutePath();
                    return sdcardpath;
                }


                //FIN SD Cards
                /*else{
                    String hola= Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                            + split[1];
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }

            */
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] { split[1] };

                return getDataColumn(context, contentUri, selection,
                        selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    private static File getFileIfExists(String basePath, String path){
        File result = null;
        File file = new File(basePath);
        if(file.exists())
        {
            file = new File(file, path);
            if(file.exists()){
                result = file;
            }
        }
        return result;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context
     *            The context.
     * @param uri
     *            The Uri to query.
     * @param selection
     *            (Optional) Filter used in the query.
     * @param selectionArgs
     *            (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { column };

        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri
                .getAuthority());
    }
}

