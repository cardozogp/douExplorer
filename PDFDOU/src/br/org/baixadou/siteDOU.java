package br.org.baixadou;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.pdfbox.util.PDFMergerUtility;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlBold;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlHiddenInput;
import com.gargoylesoftware.htmlunit.html.HtmlLink;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.gargoylesoftware.htmlunit.protocol.data.DataUrlDecoder;

public class siteDOU {

	public static void downloadDow(int jornal, final String DATA) {

		// variável tipo bool para apagar os arquivos individuais
		boolean delFiles = !frame.getjChkMntArqBaixados().isSelected();
		// objeto tipo Lista para guardar o caminho e nome de cad arquivo
		// baixado
		ArrayList<String> downloadedFiles = new ArrayList();

		// link para o site da imprensa nacional que mostra o numero de páginas
		// de cada caderno
		String link = "http://pesquisa.in.gov.br/imprensa/jsp/visualiza/index.jsp?jornal="
				+ jornal + "&pagina=1&data=" + DATA;
		// captura o código fonte HTML do link acima, algo como vizualizar o
		// código fonte da página no browser.
		String fonte_html = getHtmlCode(link).toString();
		// pega numero de paginas do jornal
		int numPgs = // 99;
		Integer.parseInt(fonte_html.substring(
				fonte_html.indexOf("totalArquivos=") + 14,
				fonte_html.indexOf(" scrolling") - 1).trim());

		// Download página por página
		try {
			for (int i = 1; i < numPgs + 1; i++) {
				// atualiza o label3 com o progresso atual
				frame.getjLblInfBaixandoDou().setText("Baixando DOU " + jornal + " página " + i
						+ " de " + numPgs);
				// nome do arquivo invidual
				String fileName = frame.getjTxtCaminhoDiretorio().getText() + "pg_" + i + "_dou_"
						+ jornal + ".pdf";
				// stream para o arquivo PDF do site
				FileOutputStream output = new FileOutputStream(fileName);
				// link para o site da imprensa nacional de onde os
				// cadernos/jornais serão baixados
				String site = "http://pesquisa.in.gov.br/imprensa/servlet/INPDFViewer?jornal="
						+ jornal
						+ "&pagina="
						+ i
						+ "&data="
						+ DATA
						+ "&captchafield=firistAccess";
				// String site =
				// "http://www.in.gov.br/servlet/INPDFViewer?jornal="+jornal+"&pagina="+i+"&data="+DATA+"&captchafield=firistAccess";
				URL url = new URL(site);

				// cria uma conexão http e um buffer de dados contendo o PDF em
				// si
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				BufferedInputStream input = new BufferedInputStream(
						conn.getInputStream());

				byte[] buf = new byte[4096];
				int len;
				// baixa o PDF
				while ((len = input.read(buf)) > 0)
					output.write(buf, 0, len);

				conn.disconnect();
				input.close();
				output.close();

				// adiciona o nome do PDF baixado a uma lista de exclusão
				if (delFiles)
					downloadedFiles.add(fileName);
			}

			// Mescla os arquivos em 1 unico
			List<InputStream> sourcePDFs = new ArrayList<InputStream>();
			final String OUTPT_FILENAME = "DOU_" + DATA.replace("/", ".")
					+ "_Jornal_" + jornal + ".pdf";

			for (int i = 1; i < numPgs + 1; i++) {
				frame.getjLblInfBaixandoDou().setText("Aguarde... Criando " + OUTPT_FILENAME);
				String fileName = frame.getjTxtCaminhoDiretorio().getText() + "pg_" + i + "_dou_"
						+ jornal + ".pdf";
				sourcePDFs.add(new FileInputStream(new File(fileName)));
			}

			// mescla
			PDFMergerUtility mergerUtility = new PDFMergerUtility();
			mergerUtility.addSources(sourcePDFs);
			mergerUtility.setDestinationFileName(frame.getjTxtCaminhoDiretorio().getText()
					+ OUTPT_FILENAME);
			mergerUtility.mergeDocuments();

			// Apaga os arquivos induviduais se o jCheckBox6 estiver desmarcado
			for (String sfile : downloadedFiles) {
				new File(sfile).delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void downloadAvulso(List<String> links, String termo, String dtIni, String dtFim, String ano) {

		// variável tipo bool para apagar os arquivos individuais
		boolean delFiles = !frame.getjChkMntArqBaixados().isSelected();
		// objeto tipo Lista para guardar o caminho e nome de cad arquivo
		// baixado
		ArrayList<String> downloadedFiles = new ArrayList();
		
		dtIni = dtIni.replace("/", "-");
		dtFim = dtFim.replace("/", "-");

		// link para o site da imprensa nacional que mostra o numero de páginas
		// de cada caderno
		// String link =
		// "http://pesquisa.in.gov.br/imprensa/jsp/visualiza/index.jsp?jornal="+jornal+"&pagina=1&data="+DATA;
		// captura o código fonte HTML do link acima, algo como vizualizar o
		// código fonte da página no browser.
		// String fonte_html = getHtmlCode(link).toString();
		// pega numero de paginas do jornal
		// int numPgs = //99;
		// Integer.parseInt(fonte_html.substring(fonte_html.indexOf("totalArquivos=")+14,
		// fonte_html.indexOf(" scrolling")-1).trim());

		//0 jornal=x
		//1 pagina=x
		//2 data=x
		HashMap<String, String> IdentificadorDocumento = new HashMap<String, String>();		
		String[] partes;

		// Download página por página
		try {
			for (int i = 0; i < links.size(); i++) {
				IdentificadorDocumento.clear();
				
				partes = links.get(i).substring(links.get(i).indexOf("?")+1).split("&");
				
				IdentificadorDocumento.put(partes[0].split("=")[0], partes[0].split("=")[1]);
				IdentificadorDocumento.put(partes[1].split("=")[0], partes[1].split("=")[1]);
				IdentificadorDocumento.put(partes[2].split("=")[0], partes[2].split("=")[1].replace("/", "-"));
				
				// atualiza o label3 com o progresso atual
				frame.getjLblInfBaixandoDou().setText("Baixando DOU " + i);
				// nome do arquivo invidual
				String fileName = frame.getjTxtCaminhoDiretorio().getText() 
						+ "dou_" + IdentificadorDocumento.get("jornal") 
						+ "_pg_" + IdentificadorDocumento.get("pagina") 
						+ "_dt_" + IdentificadorDocumento.get("data") 
						+ ".pdf";
				// stream para o arquivo PDF do site
				FileOutputStream output = new FileOutputStream(fileName);
				// link para o site da imprensa nacional de onde os
				// cadernos/jornais serão baixados
								
				String site = "http://pesquisa.in.gov.br/imprensa/servlet/INPDFViewer"
						+ links.get(i).substring(links.get(i).indexOf("?"))
						+ "&captchafield=firistAccess";

				URL url = new URL(site);

				// cria uma conexão http e um buffer de dados contendo o PDF em
				// si
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				BufferedInputStream input = new BufferedInputStream(
						conn.getInputStream());

				byte[] buf = new byte[4096];
				int len;
				// baixa o PDF
				while ((len = input.read(buf)) > 0)
					output.write(buf, 0, len);

				conn.disconnect();
				input.close();
				output.close();

				// adiciona o nome do PDF baixado a uma lista de [não mais] exclusão
				//if (delFiles)
					downloadedFiles.add(fileName);
			}

			// Mescla os arquivos em 1 unico
			List<InputStream> sourcePDFs = new ArrayList<InputStream>();
			final String OUTPT_FILENAME = "DOU_Busca." + termo 
											+ ".Inicio." + dtIni
											+ ".Fim."	 + dtFim
											+ ".Ano."	 + ano
											+ ".pdf";

			for	(String fileName : downloadedFiles){ //for (int i = 0; i < links.size(); i++) {
				frame.getjLblInfBaixandoDou().setText("Aguarde... Criando " + OUTPT_FILENAME);
				//String fileName = frame.getjTxtCaminhoDiretorio().getText() + "pg_" + i + "_dou_"	+ i + ".pdf";
				sourcePDFs.add(new FileInputStream(new File(fileName)));
			}

			// mescla
			PDFMergerUtility mergerUtility = new PDFMergerUtility();
			mergerUtility.addSources(sourcePDFs);
			mergerUtility.setDestinationFileName(frame.getjTxtCaminhoDiretorio().getText()
					+ OUTPT_FILENAME);
			mergerUtility.mergeDocuments();

			// Apaga os arquivos induviduais se o jCheckBox6 estiver desmarcado
			for (String sfile : downloadedFiles) {
				new File(sfile).delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void obtemlinksDou(String termo, String dtIni, String dtFim, String ano) throws IOException{
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
		txtPesquisa.setValueAttribute(termo.trim());
		txtDtInicio.setValueAttribute(dtIni);
		txtDtFim.setValueAttribute(dtFim);
		slcDtFim.getOptionByValue(ano).setSelected(true);

		// Now submit the form by clicking the button and get back the second
		// page.
		HtmlPage page2 = button.click();
		//System.out.println(page2.);
		//List<DomElement> tabela = (List<DomElement>) page2.getByXPath("//*[@id='conteudo']/table");
		
		List<String> links = new ArrayList<String>();
		List<?> linkListUnico = null;
		HtmlAnchor link = null;
		HtmlForm formSumit = null;
		HtmlButton submitButton = null;
		HtmlHiddenInput hdnPgAtual = null;
		int j = 1;
		
		while (!((HtmlBold) page2.getElementsByTagName("b")).getTextContent().contains("Último")  /*getByXPath("//*[@id='paginacao']/span/b").contains(new HtmlBold(ano, page2, null)) asXml().contains("<b>Último</b>")*/
				){
		
			linkListUnico = page2.getByXPath("//*[@id='conteudo']/table/tbody/tr[1]/th/a");

			try{
			for(int i = 1; /*null*/; i += 2){
				linkListUnico = page2.getByXPath("//*[@id='conteudo']/table/tbody/tr[" + i + "]/th/a");
				links.add(linkListUnico.get(0).toString().replace("amp;", "").replace("\">]", ""));
				System.out.println(linkListUnico.get(0).toString());
				
			}
			}catch(ClassCastException | IndexOutOfBoundsException iuex){
				System.out.println("Próxima página.");
			}
			
			try{
				linkListUnico = page2.getByXPath("//*[@id='paginacao']/span/a");
				link = (HtmlAnchor) linkListUnico.get(linkListUnico.size() - 2); //penultimo link ("Próximo")
				System.out.println(link.getTextContent());
				
				j++;
				//hdnPgAtual = form.getInputByName("edicao.paginaAtual");
				//hdnPgAtual.setValueAttribute(String.valueOf(j));

				page2.getForms().get(0).setActionAttribute("consulta.action");
				page2.getForms().get(0).getInputByName("edicao.paginaAtual").setValueAttribute(String.valueOf(j));
				//		("pesquisaAvancada");
				//formSumit.setActionAttribute("consulta.action");
				submitButton = (HtmlButton)page2.createElement("button");
				
				submitButton.setAttribute("type", "submit");
				page2.getForms().get(0).appendChild(submitButton);
				page2 = submitButton.click();
				
				
/*				page2 = link.click();
				synchronized (page2){
					try {
						page2.wait(700);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				webClient.waitForBackgroundJavaScript(700);*/
			}catch(ClassCastException iuex){
				System.out.println("Última página.");
				break;
			}
		}
		
		webClient.closeAllWindows();

		downloadAvulso(links, termo, dtIni, dtFim, ano);
	}
	
	private static StringBuilder getHtmlCode(String endereco) {
		// pega o ''codigo fonte'' em html da pagina e salva em um objeto tipo
		// StringBuilder
		BufferedReader in = null;
		StringBuilder ret = new StringBuilder("");
		try {
			URL url = new URL(endereco);
			in = new BufferedReader(new InputStreamReader(url.openStream()));
			String str;
			while ((str = in.readLine()) != null) {
				ret.append(str);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
				}
			}
		}
		return ret;
	}

}
