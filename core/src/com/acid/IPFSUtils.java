package com.klemstinegroup.sunshineblue.engine.util;

import ar.com.hjg.pngj.ImageInfo;
import ar.com.hjg.pngj.PngWriter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.net.HttpStatus;
import com.badlogic.gdx.utils.*;
import com.klemstinegroup.sunshineblue.SunshineBlue;
import com.klemstinegroup.sunshineblue.engine.Statics;

import java.io.ByteArrayInputStream;

public class IPFSUtils {

    /*public static void pinFile(String cid) {
        String url = "https://api.pinata.cloud/pinning/pinByHash";
        String authorization = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySW5mb3JtYXRpb24iOnsiaWQiOiI5NjMyZTdmMC1lODRiLTRjNzYtYTU2Yy0xZGE2YjgwNGI0YzAiLCJlbWFpbCI6InBhdWxrbGVtc3RpbmVAZ21haWwuY29tIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsInBpbl9wb2xpY3kiOnsicmVnaW9ucyI6W3siaWQiOiJOWUMxIiwiZGVzaXJlZFJlcGxpY2F0aW9uQ291bnQiOjF9XSwidmVyc2lvbiI6MX0sIm1mYV9lbmFibGVkIjpmYWxzZX0sImF1dGhlbnRpY2F0aW9uVHlwZSI6InNjb3BlZEtleSIsInNjb3BlZEtleUtleSI6ImI5MTJhMjE1MTJlMDYzNmZhZjRkIiwic2NvcGVkS2V5U2VjcmV0IjoiNWYwZGYwODIwOTQzM2NiY2ZmNjU0MDg4MzMxMDI3OWZlYjYxYWU0ODk4NzAyMWQ5ZTVhODNiMTU1MWQ5NTQxZiIsImlhdCI6MTYxNzk3NTEyNX0.1Mpg1X9X8XTxoLuiEdvcNW3Z7iMEkkhsSJn7hyexXvM";

        HttpRequestBuilder builder = new HttpRequestBuilder();
        Net.HttpRequest request = builder.newRequest().method(Net.HttpMethods.POST).url(url).timeout(1000000).build();
        request.setHeader("Authorization", "Bearer " + authorization);
        String sss = "{\"hashToPin\": \"" + cid + "\"}";
        request.setHeader("Content-Type", "application/json");
        request.setHeader("Content-Length", sss.length() + "");
        request.setContent(sss);
        Gdx.app.log("hashtopin", sss);
        Net.HttpResponseListener listener = new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                String res = httpResponse.getResultAsString();
                Gdx.app.log("post", request.getContent());
                Gdx.app.log("pin", res);
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.log("response", t.toString());
            }

            @Override
            public void cancelled() {

            }
        };
        Gdx.net.sendHttpRequest(request, listener);
    }*/

    public static void uploadFile(byte[] data, IPFSCIDListener listen) {
        Gdx.app.log("upload", data.length + " bytes uploading");
        String url = "https://ipfs.infura.io:5001/api/v0/add";
        String boundary = "12345678901234567890"; // Just generate some unique random value.
        String CRLF = "\r\n"; // Line separator required by multipart/form-data.

        HttpRequestBuilder builder = new HttpRequestBuilder();
        Net.HttpRequest request = builder.newRequest().method(Net.HttpMethods.POST).url(url).timeout(5000).build();
        request.setHeader("Content-Type", "multipart/form-data; boundary=" + boundary);
        String out1 = "--" + boundary +
                CRLF + "Content-Disposition: form-data; name=\"file\"" +
                CRLF + "Content-Type: " + "application/octet-stream" +
                CRLF + CRLF;
        String out2 = CRLF + "--" + boundary + "--" + CRLF;
        ByteArray batemp = new ByteArray();
        batemp.addAll(out1.getBytes());
        batemp.addAll(data);
        batemp.addAll(out2.getBytes());
//                String datauri = "data:" + mime + ";base64," + new String(Base64Coder.encode(data));
        request.setContent(new ByteArrayInputStream(batemp.toArray()), batemp.size);

        Net.HttpResponseListener listener = new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                String res = httpResponse.getResultAsString();
                if (httpResponse.getStatus().getStatusCode() == HttpStatus.SC_OK) {
                    JsonReader jsonReader = new JsonReader();
                    JsonValue jons = jsonReader.parse(res);
                    String hash = jons.getString("Hash");
                    if (hash != null && listen != null) {
                        Gdx.app.log("upload", "uploaded to " + hash);
                        listen.cid(hash);
                    } else {
                        listen.uploadFailed(new Throwable("upload failed"));
                    }
                }
            }

            @Override
            public void failed(Throwable t) {
                listen.uploadFailed(t);
            }

            @Override
            public void cancelled() {

            }
        };
        Gdx.net.sendHttpRequest(request, listener);
    }

    public static void writePng(Pixmap pixmap, MemoryFileHandle mfh, IPFSCIDListener listener) {
//                MemoryFileHandle mfh = new MemoryFileHandle();
        if (mfh == null) mfh = new MemoryFileHandle();
        ImageInfo imi = new ImageInfo(pixmap.getWidth(), pixmap.getHeight(), 8, true);
        PngWriter pngw = new PngWriter(mfh.write(false), imi);
        int[] temp = new int[pixmap.getWidth() * 4];
        Color col = new Color();
        for (int i = 0; i < pixmap.getHeight(); i++) {
            for (int j = 0; j < pixmap.getWidth(); j++) {
                int c = pixmap.getPixel(j, i);
                col.set(c);
                temp[j * 4 + 0] = (int) (col.r * 255);
                temp[j * 4 + 1] = (int) (col.g * 255);
                temp[j * 4 + 2] = (int) (col.b * 255);
                temp[j * 4 + 3] = (int) (col.a * 255);
            }
            pngw.writeRowInt(temp);
        }
        pngw.end();
        if (listener != null) {
            uploadFile(mfh.readBytes(), listener);
        }
    }


    public static void openIPFSViewer(String cid, boolean gif) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                Gdx.net.openURI(Statics.IPFSGateway + (gif ? Statics.IPFSMediaViewerGIF : Statics.IPFSMediaViewerPNG) + "?url=" + cid);
            }
        });
    }

    public static void downloadFromIPFS(String url, final IPFSFileListener responseListener) {
        Gdx.app.log("ur;", url);
        Net.HttpRequest request = new Net.HttpRequest(Net.HttpMethods.GET);
        request.setUrl(Statics.IPFSGateway + url);
        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                final byte[] result = httpResponse.getResult();
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            responseListener.downloaded(result);
                        } catch (Throwable t) {
                            failed(t);
                        }
                    }
                });
            }

            @Override
            public void failed(Throwable t) {
                responseListener.downloadFailed(t);
            }

            @Override
            public void cancelled() {
                // no way to cancel, will never get called
            }
        });
    }

    public static void uploadPngtoIPFS(Pixmap pixmap, IPFSCIDListener listener) {
        MemoryFileHandle mfh = new MemoryFileHandle();
        IPFSUtils.writePng(pixmap, mfh, null);
        SunshineBlue.nativeNet.uploadIPFS(mfh.readBytes(), new IPFSCIDListener() {
            @Override
            public void cid(String cid) {
                listener.cid(cid);
            }

            @Override
            public void uploadFailed(Throwable t) {

            }
        });
    }

}
