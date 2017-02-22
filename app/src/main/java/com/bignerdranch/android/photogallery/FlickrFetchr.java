package com.bignerdranch.android.photogallery;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by liubin on 2017/2/22.
 */

public class FlickrFetchr {

    public byte[] getUrlBytes(String urlSpec) throws IOException {

        URL url = new URL(urlSpec);

        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

        try {

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            InputStream in = connection.getInputStream();
        }
    }
}
