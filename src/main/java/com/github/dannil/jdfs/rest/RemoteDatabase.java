package com.github.dannil.jdfs.rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.github.dannil.jdfs.model.FileModel;

public class RemoteDatabase {

    private Collection<String> hosts;

    public RemoteDatabase() {
        this.hosts = new ArrayList<>();
        this.hosts.add("localhost:8080");
    }

    public RemoteDatabase(Collection<String> hosts) {
        this.hosts = hosts;
    }

    public Collection<FileModel> getFiles() {
        Map<String, FileModel> remoteFiles = new HashMap<>();

        for (String host : hosts) {
            Client client = ClientBuilder.newClient();

            WebTarget webTarget = client.target("http://" + host + "/files/get");
            Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

            try {
                Response response = invocationBuilder.get();
                List<FileModel> files = response.readEntity(new GenericType<List<FileModel>>() {
                });

                // List<File> files = invocationBuilder.get(new GenericType<List<File>>()
                // {
                // });

                for (FileModel f : files) {
                    if (!remoteFiles.containsKey(f.getPath())) {
                        remoteFiles.put(f.getPath(), f);
                    } else {
                        FileModel storedFile = remoteFiles.get(f.getPath());
                        if (f.getLastChanged().isAfter(storedFile.getLastChanged())) {
                            remoteFiles.put(f.getPath(), f);
                        }
                    }
                }
            } catch (Exception e) {
                continue;
            }
        }

        return remoteFiles.values();
    }

    public void addFile(FileModel file) {
        for (String host : hosts) {
            Client client = ClientBuilder.newClient();
        }
    }

}
