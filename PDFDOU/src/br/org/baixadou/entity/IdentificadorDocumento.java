package br.org.baixadou.entity;

import java.util.ArrayList;
import java.util.Date;

public class IdentificadorDocumento {
	private Integer jornal;
	private Integer pagina;
	private Date data;
	
	public IdentificadorDocumento(String URL){
		URL = URL.substring(URL.indexOf("?"));
//		ArrayList partes = new ArrayList()
//		
//		String.split
//		links.get(i).substring(links.get(i).indexOf("?"))
//		+ "&captchafield=firistAccess";
		
	}
	
	//?jornal=3&amp;pagina=1375&amp;data=29/12/2014"
}
