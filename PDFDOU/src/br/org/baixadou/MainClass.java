package br.org.baixadou;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.org.baixadou.download.Dou;
import br.org.baixadou.entity.TipoDou;

public class MainClass {

	private static final Log logger  = LogFactory.getLog(MainClass.class);
	
	private static ResourceBundle Constantes = ResourceBundle
			.getBundle("config");
	
	private String[] args = null;
	private Options options = new Options();

	// ARGS
	private boolean startGui;
	private String termo;
	private String dtIni;
	private String dtFim;
	private String dtDou;
	private String dir;
	private TipoDou jornal;
	// ARGS
	
	
	public static void main(String[] args){
		MainClass prog = new MainClass(args);
		prog.parse();
		if (prog.startGui){
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					new frame().setVisible(true);
				}
			});
		}
		
	}
	
	public MainClass(String[] args) {
		this.args = args;
		// ARGS
		options.addOption("g"	 , "startgui"	, false	, "Inicia a interface gráfica");
		options.addOption("t"	 , "termo"		, true	, "Na busca avançada termo a ser pesquisado");
		options.addOption("dtini", "datainicial", true	, "Na busca avançada data inicial a ser usada na pesquisa. Padrão = Hoje");
		options.addOption("dtfim", "datafinal"	, true	, "Na busca avançada data final a ser usada na pesquisa. Padrão = Hoje");
		options.addOption("dtdou", "dataDOU"	, true	, "Usada para baixar um DOU completo de um determinado dia(Opcional). Padrão = Hoje");
		options.addOption("d"	 , "diretorio"	, true	, "Diretorio onde salvar o documento(Opcional). Padrão = ouput");
		options.addOption("j"	 , "jornal"		, true	, "Jornal a ser baixado (SECAO_1, SECAO_2, SECAO_3, ED_EXTRA, TODOS). Padrão = SECAO_1");
		// ARGS
	}
	
	public void parse() {
		SimpleDateFormat sdf = null;
		boolean parseError = false;
		CommandLineParser parser = new BasicParser();

		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);

			if (cmd.hasOption("g")){
				startGui = true;
			}
			
			if (cmd.hasOption("t")) {
				if (cmd.getOptionValue("t").equals("")){
					logger.error("Argumento -t inválido.");
					parseError = true;
				}else{
					termo = cmd.getOptionValue("t");
				}
			}

			if (cmd.hasOption("dtini")) {
				sdf = new SimpleDateFormat();
				try {
					sdf.parse(cmd.getOptionValue("dtini"));
				} catch (java.text.ParseException e) {
					logger.error("Argumento -dtini inválido");
					parseError = true;
				}
				dtIni = cmd.getOptionValue("dtini");
				
			}

			if (cmd.hasOption("dtfim")) {
				sdf = new SimpleDateFormat();
				try {
					sdf.parse(cmd.getOptionValue("dtfim"));
				} catch (java.text.ParseException e) {
					logger.error("Argumento -dtfim inválido");
					parseError = true;
				}
				dtFim = cmd.getOptionValue("dtfim");
			}
			if (cmd.hasOption("dtdou")) {
				sdf = new SimpleDateFormat();
				try {
					sdf.parse(cmd.getOptionValue("dtdou"));
				} catch (java.text.ParseException e) {
					logger.error("Argumento -dtdou inválido");
					parseError = true;
				}
				dtDou = cmd.getOptionValue("dtdou");
			}
			if (cmd.hasOption("d")) {

			}
			
			if (cmd.hasOption("j")) {

			}

			if(!parseError){
				Dou dou;
				try {
					dou = new Dou();
					dou.pesquisaPorTermo(termo,
							new SimpleDateFormat("dd/MM/yyyy").format(dtIni),
							new SimpleDateFormat("dd/MM/yyyy").format(dtFim),
							Constantes.getString("Diretorio"), true);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else{
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp( "douExplorer", options );
			}
			

			
//			if (cmd.hasOption("v")) {
//				log.log(Level.INFO,
//						"Using cli argument -v=" + cmd.getOptionValue("v"));
//				// Whatever you want to do with the setting goes here
//			} else {
//				log.log(Level.SEVERE, "MIssing v option");
//				//help();
//			}

		} catch (ParseException e) {
			logger.error("Falha ao analisar os argumentos", e);

			
		}
	}
	

//	public static void main(String[] args) {
//		public static void main(String[] args) {
//			new Cli(args).parse();
//		}
//		
//		
//		CmdLineParser parser = new CmdLineParser(MainClass.class);
//
//		try {
//			parser.parseArgument(args);
//			// you can parse additional arguments if you want.
//			// parser.parseArgument("more","args");
//
//		} catch (CmdLineException e) {
//			System.err.println(e.getMessage());
//			System.err.println("java SampleMain [options...] arguments...");
//			// print the list of available options
//			parser.printUsage(System.err);
//			System.err.println();
//			// print option sample. This is useful some time
//			// System.err.println("  Example: java SampleMain"+parser.printExample(ALL));
//			return;
//		}
//
//		Dou dou;
//		try {
//			dou = new Dou();
//			dou.pesquisaPorTermo(termo,
//					new SimpleDateFormat("dd/MM/yyyy").format(dtIni),
//					new SimpleDateFormat("dd/MM/yyyy").format(dtFim),
//					Constantes.getString("Diretorio"), true);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
}
