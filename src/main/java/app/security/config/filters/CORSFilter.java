package app.security.config.filters;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

@Component
public class CORSFilter extends GenericFilterBean {

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		final HttpServletResponse response = (HttpServletResponse) res;
		final HttpServletRequest request = (HttpServletRequest) req;
		
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
		response.addHeader("Access-Control-Allow-Headers", "X-Requested-With,Origin,Content-Type, Accept");
		
		if (request.getMethod().equals("OPTIONS")) {
			response.setStatus(HttpStatus.SC_ACCEPTED); // if OPTIONS method, respond allowed
		} else {
			chain.doFilter(req, response); // other http methods continue chain
		}
	}
}
