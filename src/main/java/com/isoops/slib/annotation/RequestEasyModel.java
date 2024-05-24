package com.isoops.slib.annotation;

import com.isoops.slib.pojo.AbstractObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;


@EqualsAndHashCode(callSuper = true)
@Data
public class RequestEasyModel extends AbstractObject {

    private RequestMethod method;
    private String ip;
    private String url;
    private String uri;
    private HttpServletRequest request;
    private String headersString;

    public RequestEasyModel(HttpServletRequest request) {
        this.request = request;
        this.method = ContractFacory.stringToMethod(request.getMethod());
        this.ip = ContractFacory.getIpAddr(request);
        this.url = request.getRequestURL().toString();
        this.uri = request.getRequestURI();
        this.headersString = getHeadersString();
    }
}
