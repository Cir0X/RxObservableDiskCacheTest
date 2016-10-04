package de.cir0x.rxobservablediskcachetest;

public class HttpBinResponse {

    private String origin;

    private String url;

    public HttpBinResponse() {
    }

    public HttpBinResponse(String origin, String url) {
        this.origin = origin;
        this.url = url;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "HttpBinResponse{" +
                "origin='" + origin + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
