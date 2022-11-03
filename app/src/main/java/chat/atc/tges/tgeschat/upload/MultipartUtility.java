package chat.atc.tges.tgeschat.upload;

import android.content.Context;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import javax.net.ssl.HttpsURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;


import chat.atc.tges.tgeschat.Mensajes.Mensajeria;

import static chat.atc.tges.tgeschat.network.CustomSSLSocketFactory.getSSLSocketFactory;

public class MultipartUtility {
    private HttpURLConnection httpConn;
    private HttpsURLConnection httpsConn; //PRODUCCCION
    //  private HttpsURLConnection httpsConn;
    private DataOutputStream request;
    private final String boundary =  "*****";
    private final String crlf = "\r\n";
    private final String twoHyphens = "--";

    /**
     * This constructor initializes a new HTTP POST request with content type
     * is set to multipart/form-data
     *
     * @param requestURL
     * @throws IOException
     */
    public void MultipartUtilityV2(String requestURL, Context context)
            throws IOException {
        // creates a unique boundary based on time stamp}
        //USAR PARA PRODUCCION
        try {
            URL url = new URL(requestURL);

            httpsConn = (HttpsURLConnection) url.openConnection();
            httpsConn.setSSLSocketFactory(getSSLSocketFactory(context));
            httpsConn.setUseCaches(false);
            httpsConn.setDoOutput(true); // indicates POST method
            httpsConn.setDoInput(true);
            httpsConn.setRequestMethod("POST");
            httpsConn.setRequestProperty("Connection", "Keep-Alive");
            httpsConn.setRequestProperty("Cache-Control", "no-cache");
            httpsConn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + this.boundary);
            request =  new DataOutputStream(httpsConn.getOutputStream());
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

        //DESARROLLO
//        URL url = new URL(requestURL);
//        //httpsConn = (HttpsURLConnection) url.openConnection();
//        httpConn = (HttpURLConnection) url.openConnection();
//        //httpConn.setSSLSocketFactory(getSSLSocketFactory(context));
//        httpConn.setUseCaches(false);
//        httpConn.setDoOutput(true); // indicates POST method
//        httpConn.setDoInput(true);
//        httpConn.setRequestMethod("POST");
//        httpConn.setRequestProperty("Connection", "Keep-Alive");
//        httpConn.setRequestProperty("Cache-Control", "no-cache");
//        httpConn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + this.boundary);
//        request =  new DataOutputStream(httpConn.getOutputStream());
    }

    public void addFormField(String name, String value)throws IOException  {
        request.writeBytes(this.twoHyphens + this.boundary + this.crlf);
        request.writeBytes("Content-Disposition: form-data; name=\"" + name + "\""+ this.crlf);
        request.writeBytes("Content-Type: text/plain; charset=UTF-8" + this.crlf);
        request.writeBytes(this.crlf);
        request.writeBytes(value+ this.crlf);
        request.flush();
    }
    /**
     * Adds a upload file section to the request
     *
     * @param fieldName  name attribute in <input type="file" name="..." />
     * @param uploadFile a File to be uploaded
     * @throws IOException
     */
    public void addFilePart(String fieldName, File uploadFile)
            throws IOException {
        String fileName = uploadFile.getName();
        request.writeBytes(this.twoHyphens + this.boundary + this.crlf);
        request.writeBytes("Content-Disposition: form-data; name=\"" +
                fieldName + "\";filename=\"" +
                fileName + "\"" + this.crlf);
        request.writeBytes("Content-Type: "+getMimeType(uploadFile.toString())+"; charset=UTF-8" + this.crlf);
        request.writeBytes(this.crlf);

//        byte[] bytes = Files.readAllBytes(uploadFile.toPath());
        UtilFile iou = new UtilFile();
        byte[] bytes = iou.readFile(uploadFile.getPath());
        request.write(bytes);
    }

    public void addFilePart(String fieldName, byte[] bytes)
            throws IOException {
        //String fileName = uploadFile.getName();
        String fileName = "archivoSubido.jpg";
        request.writeBytes(this.twoHyphens + this.boundary + this.crlf);
        request.writeBytes("Content-Disposition: form-data; name=\"" +
                fieldName + "\";filename=\"" +
                fileName + "\"" + this.crlf);
        //request.writeBytes("Content-Type: "+getMimeType(uploadFile.toString())+"; charset=UTF-8" + this.crlf);
        request.writeBytes("Content-Type: "+getMimeType(fileName)+"; charset=UTF-8" + this.crlf);
        request.writeBytes(this.crlf);

//        byte[] bytes = Files.readAllBytes(uploadFile.toPath());
        UtilFile iou = new UtilFile();
        //byte[] bytes = iou.readFile(uploadFile.getPath()); // inputStream
        byte[] bytes2 = Mensajeria.bytes; //iou.readFile(bytes);
        request.write(bytes2);
    }

    /**
     * Completes the request and receives response from the server.
     *
     * @return a list of Strings as response in case the server returned
     * status OK, otherwise an exception is thrown.
     * @throws IOException
     */
    public String finish() throws IOException {
        String response ="";
        //request.writeBytes(this.crlf);
        request.writeBytes(this.twoHyphens + this.boundary +
                this.twoHyphens + this.crlf);
        request.flush();
        request.close();
        Log.e("MSG:Sube File", "se cierra la conexion");
        // checks server's status code first

        //DESCOMENTAR EN PRODUCCION
        int status = httpsConn.getResponseCode();
        if (status == HttpsURLConnection.HTTP_OK) {
            InputStream responseStream = new
                    BufferedInputStream(httpsConn.getInputStream());
            BufferedReader responseStreamReader =
                    new BufferedReader(new InputStreamReader(responseStream));
            String line = "";
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = responseStreamReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            responseStreamReader.close();
            response = stringBuilder.toString();
            httpsConn.disconnect();
        }
        else
        {
            throw new IOException("Server returned non-OK status: " + status);
        }
        //USO EN DESARROLLO
//        int status = httpConn.getResponseCode();
//        if (status == HttpURLConnection.HTTP_OK) {
//            InputStream responseStream = new
//                    BufferedInputStream(httpConn.getInputStream());
//            BufferedReader responseStreamReader =
//                    new BufferedReader(new InputStreamReader(responseStream));
//            String line = "";
//            StringBuilder stringBuilder = new StringBuilder();
//            while ((line = responseStreamReader.readLine()) != null) {
//                stringBuilder.append(line).append("\n");
//            }
//            responseStreamReader.close();
//            response = stringBuilder.toString();
//            httpConn.disconnect();
//        } else {
//            throw new IOException("Server returned non-OK status: " + status);
//        }
        return response;
    }

    // url = file path or whatever suitable URL you want.
    public String getMimeType(String url) {
        url=url.replace(" ","");
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

        }
        return type;
    }
}