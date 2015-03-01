package br.org.baixadou.download;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.pdfbox.util.PDFMergerUtility;
import org.joda.time.DateTime;

import br.org.baixadou.Constantes;
import br.org.baixadou.entity.TipoDou;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlBold;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

public class Dou {	
	private  ResourceBundle Constantes = ResourceBundle.getBundle("config");

	private int identificaNumeroPaginasJornal(String html){
		return Integer.parseInt(html.substring(
				html.indexOf("totalArquivos=") + 14,
				html.indexOf(" scrolling") - 1).trim());
	}
	
	public void pesquisaPorTermo(String termo, String dtIni, String dtFim, String diretorio, boolean delFiles) throws IOException{
		DateTime ini = new DateTime(Integer.parseInt(dtIni.split("/")[2])
								  , Integer.parseInt(dtIni.split("/")[1])
								  , Integer.parseInt(dtIni.split("/")[0]),0,0);
		
		DateTime fim = new DateTime(Integer.parseInt(dtFim.split("/")[2])
								  , Integer.parseInt(dtFim.split("/")[1])
								  , Integer.parseInt(dtFim.split("/")[0]),0,0);
		
		List<String> links = new ArrayList();
		
		DateTime work = ini.plusDays(30);
		if(work.compareTo(fim) < 0){
			//TODO: Tratar mudanca de ano
			while(work.compareTo(fim) < 0){					
				links.addAll(obterlinksDou(termo
						, ini.getDayOfMonth() + "/" + ini.getMonthOfYear()
						, work.getDayOfMonth() + "/" + work.getMonthOfYear()
						, String.valueOf(work.getYear())
						, diretorio
						, delFiles));
//				downloadAvulso(links
//						, termo
//						, ini.getDayOfMonth() + "/" + ini.getMonthOfYear()
//						, work.getDayOfMonth() + "/" + work.getMonthOfYear()
//						, String.valueOf(work.getYear())
//						, diretorio
//						, true);
				ini = work;
				work = work.plusDays(30);
				ini = ini.plusDays(1);
			}
		}
				links.addAll(obterlinksDou(termo
						, ini.getDayOfMonth() + "/" + ini.getMonthOfYear()
						, fim.getDayOfMonth() + "/" + fim.getMonthOfYear()
						, String.valueOf(fim.getYear())
						, diretorio
						, delFiles));
				downloadAvulso(links
						, termo
						, ini.getDayOfMonth() + "/" + ini.getMonthOfYear()
						, fim.getDayOfMonth() + "/" + fim.getMonthOfYear()
						, String.valueOf(fim.getYear())
						, diretorio
						, true);
			

		
		//links = obterlinksDou(termo, dtIni, dtFim, ano, diretorio, delFiles);
		//downloadAvulso(links, termo, dtIni, dtFim, ano, Constantes.getString("Diretorio"), true);
	}

	/**
	 * 
	 * @param jornal enum que identifica qual jornal baixar
	 * @param DATA data do Diario para baixar
	 * @param diretorio em qual diretorio baixar
	 */
	public void pesquisaPorDia(final TipoDou jornal, final String diretorio) {
		 pesquisaPorDia(jornal, new SimpleDateFormat("dd/MM/yyyy").format(new Date()) , diretorio, true);
	}
	
	/**
	 * 
	 * @param jornal enum que identifica qual jornal baixar
	 * @param DATA data do Diario para baixar
	 * @param diretorio em qual diretorio baixar
	 */
	public void pesquisaPorDia(final TipoDou jornal, final String DATA, final String diretorio) {
		 pesquisaPorDia(jornal, DATA, diretorio, true);
	}
	
	/**
	 * 
	 * @param jornal enum que identifica qual jornal baixar
	 * @param DATA data do Diario para baixar
	 * @param diretorio em qual diretorio baixar
	 * @param delFiles indica se deve apagar os arquivos individuais depois de juntar todos
	 */
	public void pesquisaPorDia(final TipoDou jornal, final String DATA, final String diretorio, final boolean delFiles) {
		// objeto tipo Lista para guardar o caminho e nome de cad arquivo
		// baixado
		ArrayList<String> downloadedFiles = new ArrayList<String>();

		// link para o site da imprensa nacional que mostra o numero de páginas
		// de cada caderno
		String linkComQtdPaginas = 
			Constantes.getString("SiteImprensaNacionaNumeroPaginas")
				.replace(Constantes.getString("SubstituicaoJornal"), jornal.toString())
				.replace(Constantes.getString("SubstituicaoData"), DATA);
		
		// captura o código fonte HTML do link acima
		String fonte_html = getHtmlCode(linkComQtdPaginas).toString();
		
		int numPgs = identificaNumeroPaginasJornal(fonte_html);

		FileOutputStream output = null;	
		HttpURLConnection conn = null;
		BufferedInputStream input = null;
		
		// Download página por página
		try {
			for (int i = 1; i < numPgs + 1; i++) {
				// nome do arquivo invidual
				String fileName = diretorio + "pg_" + i + "_dou_" + jornal + ".pdf";
				
				// stream para o arquivo PDF do site
				output = new FileOutputStream(fileName);
				// link para o site da imprensa nacional de onde os
				// cadernos/jornais serão baixados
				String site = Constantes.getString("SiteImprensaNacionaPDF")
						.replace(Constantes.getString("SubstituicaoJornal"), jornal.toString())
						.replace(Constantes.getString("SubstituicaoPagina"), String.valueOf(i))
						.replace(Constantes.getString("SubstituicaoData"), DATA);

				URL url = new URL(site);

				// cria uma conexão http e um buffer de dados contendo o PDF em
				// si
				conn = (HttpURLConnection) url.openConnection();
				input = new BufferedInputStream(conn.getInputStream());

				byte[] buf = new byte[4096];
				int len;
				// baixa o PDF
				while ((len = input.read(buf)) > 0)
					output.write(buf, 0, len);

				conn.disconnect();
				input.close();
				output.close();

				// adiciona o nome do PDF baixado a uma lista
					downloadedFiles.add(fileName);
			}

			// Mescla os arquivos em 1 unico
			List<InputStream> sourcePDFs = new ArrayList<InputStream>();
			final String OUTPT_FILENAME = "DOU_" + DATA.replace("/", ".")
					+ "_Jornal_" + jornal + ".pdf";

			for (int i = 1; i < numPgs + 1; i++) {
				String fileName = diretorio + "pg_" + i + "_dou_" + jornal + ".pdf";
				sourcePDFs.add(new FileInputStream(new File(fileName)));
			}

			// mescla
			PDFMergerUtility mergerUtility = new PDFMergerUtility();
			mergerUtility.addSources(sourcePDFs);
			mergerUtility.setDestinationFileName(diretorio + OUTPT_FILENAME);
			mergerUtility.mergeDocuments();

			// Apaga os arquivos induviduais 
			if (delFiles)
				for (String sfile : downloadedFiles) {
					new File(sfile).delete();
				}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param links lista das páginas de busca para baixar
	 * @param termo texto que foi pesquisado
	 * @param dtIni data inicial da busca
	 * @param dtFim data final da busca
	 * @param ano ano da busca
	 * @param delFiles indica se deve apagar os arquivos individuais depois de juntar todos
	 */
	public void downloadAvulso(List<String> links, String termo, String dtIni, String dtFim, String ano, final String diretorio, boolean delFiles) {

		// objeto tipo Lista para guardar o caminho e nome de cad arquivo
		// baixado
		ArrayList<String> downloadedFiles = new ArrayList();
		
		dtIni = dtIni.replace("/", "-");
		dtFim = dtFim.replace("/", "-");

		//0 jornal=x
		//1 pagina=x
		//2 data=x
		HashMap<String, String> IdentificadorDocumento = new HashMap<String, String>();		
		String[] partes;

		// Download página por página
		System.out.println("Iniciando download de cada página");
		try {
			for (int i = 0; i < links.size(); i++) {
				System.out.printf(
					String.format("Pagina %0" + (links.size()/5) + "d",  i + 1)
				);
				
				IdentificadorDocumento.clear();
				
				partes = links.get(i).substring(links.get(i).indexOf("?")+1).split("&");
				
				IdentificadorDocumento.put(partes[0].split("=")[0], partes[0].split("=")[1]);
				IdentificadorDocumento.put(partes[1].split("=")[0], partes[1].split("=")[1]);
				IdentificadorDocumento.put(partes[2].split("=")[0], partes[2].split("=")[1].replace("/", "-"));
				
				// nome do arquivo invidual
				String fileName = diretorio
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
				HttpURLConnection conn = null;
				BufferedInputStream input = null;
				try{
					conn = (HttpURLConnection) url
						.openConnection();
					input = new BufferedInputStream(
						conn.getInputStream());
				}catch(java.net.SocketException ex){
					ex.printStackTrace();
					i--;
				}
				
				byte[] buf = new byte[4096];
				int len;
				// baixa o PDF
				while ((len = input.read(buf)) > 0)
					output.write(buf, 0, len);

				conn.disconnect();
				input.close();
				output.close();

				// adiciona o nome do PDF baixado a uma lista
					downloadedFiles.add(fileName);
				System.out.println("..OK!");
			}
			
			System.out.print("Juntando arquivos");
			// Mescla os arquivos em 1 unico
			List<InputStream> sourcePDFs = new ArrayList<InputStream>();
			final String OUTPT_FILENAME = "DOU_Busca." + termo 
											+ ".Inicio." + dtIni
											+ ".Fim."	 + dtFim
											+ ".Ano."	 + ano
											+ ".pdf";

			for	(String fileName : downloadedFiles){ //for (int i = 0; i < links.size(); i++) {
				//String fileName = frame.getjTxtCaminhoDiretorio().getText() + "pg_" + i + "_dou_"	+ i + ".pdf";
				sourcePDFs.add(new FileInputStream(new File(fileName)));
			}

			// mescla
			PDFMergerUtility mergerUtility = new PDFMergerUtility();
			mergerUtility.addSources(sourcePDFs);
			mergerUtility.setDestinationFileName(diretorio + OUTPT_FILENAME);
			mergerUtility.mergeDocuments();
			System.out.println("..OK!");
			
			// Apaga os arquivos induviduais
			if (delFiles)
				for (String sfile : downloadedFiles) {
					new File(sfile).delete();
				}
			System.out.println("Processamento terminado!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param termo texto que foi pesquisado
	 * @param dtIni data inicial da busca
	 * @param dtFim data final da busca
	 * @param ano ano da busca
	 * @param diretorio em qual diretorio baixar
	 * @param delFiles indica se deve apagar os arquivos individuais depois de juntar todos

	 * @throws IOException
	 */
	public List<String> obterlinksDou(String termo, String dtIni, String dtFim, String ano, String diretorio, boolean delFiles) throws IOException{
		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
		System.setProperty("org.apache.commons.logging.simplelog.defaultlog", "error");
		
		final WebClient webClient = new WebClient();
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		webClient.getOptions().setPrintContentOnFailingStatusCode(false);
		//webClient.getOptions().
		
		// Get the first page
		System.out.println("Acessando " + Constantes.getString("SiteInicial"));
		final HtmlPage page1 = webClient.getPage(Constantes.getString("SiteInicial"));

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

		// Now submit the form by clicking the button and get back the second page.
		HtmlPage page2 = button.click();
		
		List<String> links = new ArrayList<String>();
		List<?> linkListUnico = null;
		HtmlAnchor link = null;
		HtmlButton submitButton = null;
		int j = 1;

		do //()
		{

				linkListUnico = page2.getByXPath("//*[@id='conteudo']/table/tbody/tr/th/a");
			try{
			for(Object item : linkListUnico){

				links.add( ((HtmlAnchor) item).getHrefAttribute() );
				System.out.println( ((HtmlAnchor) item).getHrefAttribute() );
				
			}
			}catch(ClassCastException | IndexOutOfBoundsException iuex){
				System.out.println("Próxima página.");
			}
			
			try{
				linkListUnico = page2.getByXPath("//*[@id='paginacao']/span/a");
				link = (HtmlAnchor) linkListUnico.get(linkListUnico.size() - 2); //penultimo link ("Próximo")
				System.out.println("Emulando click no link " + link.getTextContent());
				
				j++;
				page2.getForms().get(0).setActionAttribute("consulta.action");
				page2.getForms().get(0).getInputByName("edicao.paginaAtual").setValueAttribute(String.valueOf(j));

				submitButton = (HtmlButton)page2.createElement("button");
				
				submitButton.setAttribute("type", "submit");
				page2.getForms().get(0).appendChild(submitButton);

				page2 = submitButton.click();

			}catch(ClassCastException | IndexOutOfBoundsException iuex){
				System.out.println("Última página.");
				break;
			}
		}while(!((HtmlBold) page2.getByXPath("//*[@id='paginacao']/span/b").get(page2.getByXPath("//*[@id='paginacao']/span/b").size() - 1)).asText().contains("Último"));
		
		return links;
	}
	
	private StringBuilder getHtmlCode(String endereco) {
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
