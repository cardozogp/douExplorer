package br.org.baixadou;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.SAXException;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlLink;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

public class ImpNacSimulator {

	public static void main(String[] args) throws IOException, SAXException {
		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
		System.setProperty("org.apache.commons.logging.simplelog.defaultlog", "error");
		
		final WebClient webClient = new WebClient();
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		webClient.getOptions().setPrintContentOnFailingStatusCode(false);
		//webClient.getOptions().
		
		// Get the first page
		final HtmlPage page1 = webClient.getPage("http://portal.in.gov.br/");

		// Get the form that we are dealing with and within that form,
		// find the submit button and the field that we want to change.
		final HtmlForm form = page1.getFormByName("pesquisaAvancada");

		final HtmlSubmitInput button = form.getInputByValue("BUSCAR");
		final HtmlTextInput txtPesquisa = form.getInputByName("edicao.txtPesquisa");
		final HtmlTextInput txtDtInicio = form.getInputByName("edicao.dtInicio");
		final HtmlTextInput txtDtFim = form.getInputByName("edicao.dtFim");
		final HtmlSelect slcDtFim = form.getSelectByName("edicao.ano");
		

		// Change the value of the text field
		txtPesquisa.setValueAttribute("dataprev");
		txtDtInicio.setValueAttribute("15/12");
		txtDtFim.setValueAttribute("29/12");
		slcDtFim.getOptionByValue("2014").setSelected(true);

		// Now submit the form by clicking the button and get back the second
		// page.
		final HtmlPage page2 = button.click();
		//System.out.println(page2.);
		List<DomElement> tabela = (List<DomElement>) page2.getByXPath("//*[@id='conteudo']/table");
		
		List<?> linkListUnico = page2.getByXPath("//*[@id='conteudo']/table/tbody/tr[1]/th/a");
		
		try{
		for(int i = 1; /*null*/; i += 2){
			linkListUnico = page2.getByXPath("//*[@id='conteudo']/table/tbody/tr[" + i + "]/th/a");
			System.out.println(linkListUnico.get(0).toString());
		}
		}catch(IndexOutOfBoundsException iuex){
			System.out.println("Pegou os links da página.");
		}
		

		webClient.closeAllWindows();
	}

}
