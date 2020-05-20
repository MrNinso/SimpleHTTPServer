package com.developer.Simple.Routers;

import com.developer.Simple.core.ClientRequest;
import com.developer.Simple.core.HTTPCodes;
import com.developer.Simple.core.Server;
import com.developer.Simple.core.ServerResponse;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class FileRouter implements Server.OnResquest {

    public final File Root;
    public final String IndexFileName;

    public ArrayList<String> IgnoredFiles;

    public FileRouter(File root, String indexFileName) {
        Root = root;
        IndexFileName = indexFileName;
        IgnoredFiles = new ArrayList<>();
    }

    public FileRouter(File root, String indexFileName, ArrayList<String> ignoredFiles) {
        Root = root;
        IndexFileName = indexFileName;
        IgnoredFiles = ignoredFiles;
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
                return new ServerResponse(HTTPCodes.NOT_FOUND);
        }

        return checkFile(f);
    }

    private ServerResponse checkFile(File f) {
        if (!f.exists())
            return new ServerResponse(HTTPCodes.NOT_FOUND);
        else if (f.isDirectory() || !f.canRead())
            return new ServerResponse(HTTPCodes.INTERNAL_SERVER_ERROR);
        else if (IgnoredFiles.contains(f.getName()))
            return new ServerResponse(HTTPCodes.NOT_FOUND);
        else {
            try {
                return new ServerResponse(HTTPCodes.OK, FileUtils.readFileToByteArray(f));
            } catch (IOException e) {
                e.printStackTrace();
                return new ServerResponse(HTTPCodes.INTERNAL_SERVER_ERROR);
            }
        }
    }
}
