package br.com.agenda.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import br.com.agenda.model.Usuario;

@WebFilter("/*")
public class LoginFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest servletRequest = (HttpServletRequest) request;
		if (servletRequest.getRequestURI().contains("javax.faces.resource")) {
			chain.doFilter(servletRequest, response);
		} else {
			HttpSession sessao = servletRequest.getSession(true);
			Usuario usuario = (Usuario) sessao.getAttribute("usuario");
			String url = servletRequest.getRequestURL().toString();
			if (usuario != null) {
				if (url.endsWith("/agenda")) {
					String contextPath = ((HttpServletRequest) request).getContextPath();
					((HttpServletResponse) response).sendRedirect(contextPath + "/contatos.xhtml");
					return;
				} else {
					chain.doFilter(request, response);
				}
			} else {
				if (url.contains("login") || url.contains("cadastrar")) {
					chain.doFilter(request, response);
				} else {
					String contextPath = ((HttpServletRequest) request).getContextPath();
					String path = ((HttpServletRequest)request).getRequestURL().toString();
					String pagina = path.substring(path.lastIndexOf("/") + 1);
					if(pagina.contains("login"))
						((HttpServletResponse) response).sendRedirect(contextPath + "/login.xhtml");
					else if(pagina.contains("cadastrar"))
						((HttpServletResponse) response).sendRedirect(contextPath + "/cadastrar.xhtml");
				}
			}
		}

	}

}