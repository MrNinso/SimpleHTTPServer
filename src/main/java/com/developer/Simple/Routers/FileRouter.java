package com.developer.Simple.Routers;

import com.developer.Simple.core.ClientRequest;
import com.developer.Simple.core.Server;
import com.developer.Simple.core.ServerResponse;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;

public class FileRouter implements Server.OnResquest {

    public final File Root;
    public final String IndexFileName;

    public FileRouter(File root, String indexFileName) {
        Root = root;
        IndexFileName = indexFileName;
    }

    @Override
    public ServerResponse request(ClientRequest clientRequest) {
        if (clientRequest.URI.length == 0) {
            return checkFile(new File(Root, IndexFileName));
        }
        File f = Root;

        for (int i = 0; i < clientRequest.URI.length; i++) {
            f = new File(f, clientRequest.URI[i]);

            if (!f.exists())
                return new ServerResponse(404);
        }

        return checkFile(f);
    }

    private ServerResponse checkFile(File f) {
        if (!f.exists())
            return new ServerResponse(404);
        else if (f.isDirectory() || !f.canRead())
            return new ServerResponse(500);
        else {
            try {
                return new ServerResponse(200, FileUtils.readFileToByteArray(f));
            } catch (IOException e) {
                e.printStackTrace();
                return new ServerResponse(500);
            }
        }
    }
}
