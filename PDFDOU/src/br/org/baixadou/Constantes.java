package br.org.baixadou;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Constantes extends ResourceBundle{
	
	private static ResourceBundle rb;
	private static HashMap<String, String> valores;
	
	static {
		valores = new HashMap<String, String>();
		try{
			rb = ResourceBundle.getBundle("br/org/baixadou/config.properties");
			
			putForMe("SiteInicial");
			putForMe("SiteImprensaNacionaNumeroPaginas");
			putForMe("SiteImprensaNacionaPDF");
			putForMe("SubstituicaoJornal");
			putForMe("SubstituicaoPagina");
			putForMe("SubstituicaoData");
			
			putForMe("LimiteMesesSiteImp");
			putForMe("XPath1oItemListaLinks");
			putForMe("Substituicao1oItemListaLinks");
			putForMe("NenhumItemEncontrado");
			putForMe("XPath2aPagina");
			putForMe("ElementoUltimaPagina");
			

		}catch(MissingResourceException | NullPointerException ex){
			valores.put("SiteInicial"						, "http://portal.in.gov.br/");
			valores.put("SiteImprensaNacionaNumeroPaginas"	, "http://pesquisa.in.gov.br/imprensa/jsp/visualiza/index.jsp?jornal=JORNAL&pagina=1&data=DATA");
			valores.put("SiteImprensaNacionaPDF"			, "http://pesquisa.in.gov.br/imprensa/servlet/INPDFViewer?jornal=JORNAL&pagina=PAGINA&data=DATA&captchafield=firistAccess");
			valores.put("SubstituicaoJornal"				, "JORNAL");
			valores.put("SubstituicaoPagina"				, "PAGINA");
			valores.put("SubstituicaoData"					, "DATA");
			
			valores.put("LimiteMesesSiteImp"				, "6");
			valores.put("XPath1oItemListaLinks"				, "//*[@id='conteudo']/table/tbody/tr[1]/th/a");
			
		}			
	}
	
	public Constantes(){
	
	}
	
	private static void putForMe(String termo){
		valores.put(termo, rb.getString(termo));
	}
	
	public static String getConstante(String valor){
		String retorno = null;
		try{
			retorno = valores.get(valor);
		}catch(MissingResourceException | NullPointerException ex){
			rb = ResourceBundle.getBundle("br/org/baixadou/config.properties");
			return rb.getString(valor);
		}
		if (retorno == null){
			rb = ResourceBundle.getBundle("br/org/baixadou/config.properties");
			return rb.getString(valor);
		}
		return valores.get(valor);
	}

	@Override
	protected Object handleGetObject(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Enumeration<String> getKeys() {
		// TODO Auto-generated method stub
		return null;
	}

}
