package org.radrso.workflow.provider.requests.entity;

import lombok.Data;
import lombok.ToString;
import org.apache.http.HttpResponse;
import org.radrso.workflow.provider.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

/**
 * Created by raomengnan on 16-12-9.
 */
@Data
@ToString
public class Response implements Serializable{
    private String content;
    private int statusCode;
    private String errorMsg;
    private boolean success;
    private String contentType;

    public Response(HttpResponse response) throws IOException {
        this.content = getContent(response);
        this.statusCode = response.getStatusLine().getStatusCode();
        this.success = (statusCode / 100 == 2);
        if(response.getEntity().getContentType() != null)
            this.contentType = response.getEntity().getContentType().getValue();
    }

    private String getContent(HttpResponse response) throws IOException {
        InputStream stream = response.getEntity().getContent();
        return StreamUtils.readFromStream(stream);
    }
}
