package com.guerra.russel.letmebee.Collection;

public class Signatures {

    private String name;
    private String url;

    public Signatures() {
    }

    public Signatures(String name, String url) {
        if (name.trim().equals("")) {
            name = "No name";
        }

        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}
