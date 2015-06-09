package com.spotifystreamer.app.adapter;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import kaaes.spotify.webapi.android.models.Image;

public class Utils {

    public static String SPOTIFY_ACCESS_TOKEN = "";

    public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
              int count=is.read(bytes, 0, buffer_size);
              if(count==-1)
                  break;
              os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }

    public static String getImageURL (List<Image> images) {
        String imageURL   = null;

        for(Image image: images) {
            if(image.height != null && image.height < 100) {
                imageURL = image.url;
                break;
            }
        }

        if ((imageURL == null || imageURL.trim().equals("")) && images.size() > 0) {
            imageURL = images.get(0).url;
        }

        return imageURL;
    }
}