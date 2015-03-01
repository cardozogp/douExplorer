package br.org.baixadou;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import br.org.baixadou.download.Dou;
import br.org.baixadou.entity.TipoDou;

public class MainClass {

	private static ResourceBundle Constantes = ResourceBundle.getBundle("config");
	
	//ARGS
    @Option(name="-g", 		aliases="--startgui", 		
    		usage="Inicia a interface gráfica")
    private boolean startGui;
	
    @Option(name="-t", 		aliases="--termo", 			
    		usage="Termo a ser usado na busca avançada", forbids={"-dtDOU"})
    private static String termo;
    
    @Option(name="-dtini", 	aliases="--datainicial", 	
    		usage="Data inicial a ser usada na busca avançada. Padrão = Hoje", 	forbids={"-dtDOU"}, depends={"-t"})
    private static Date dtIni;
    
    @Option(name="-dtfim", 	aliases="--datafinal",   	
    		usage="Data final a ser usada na busca avançada. Padrão = Hoje", 	forbids={"-dtDOU"}, depends={"-t"})
    private static Date dtFim;
    
    @Option(name="-dtDOU", 	aliases="--dataDOU",   		
    		usage="Usada para baixar um DOU completo de um determinado dia(Opcional). Padrão = Hoje", forbids={"-dtini", "-dtfim"})
    private static Date dtDou;
    
    @Option(name="-d", 		aliases="--diretorio", 		
    		usage="Diretorio onde salvar o documento(Opcional). Padrão = ouput")
    private static String dir;
    
    //TODO: tratar entrada de 2 ou +
    @Option(name="-j", 		aliases="--jornal", 		
    		usage="Jornal a ser baixado (SECAO_1, SECAO_2, SECAO_3, ED_EXTRA, TODOS). Padrão = SECAO_1")
    private static TipoDou jornal;
    //ARGS
    
	public static void main(String[] args) {
		CmdLineParser parser = new CmdLineParser(MainClass.class);
		
        try {
            parser.parseArgument(args);
            // you can parse additional arguments if you want.
            // parser.parseArgument("more","args");

        } catch( CmdLineException e ) {
            System.err.println(e.getMessage());
            System.err.println("java SampleMain [options...] arguments...");
            // print the list of available options
            parser.printUsage(System.err);
            System.err.println();
            // print option sample. This is useful some time
            //System.err.println("  Example: java SampleMain"+parser.printExample(ALL));
            return;
        }
        
        Dou dou;        
		try {
			dou = new Dou();
			dou.pesquisaPorTermo(termo
							   , new SimpleDateFormat("dd/MM/yyyy").format(dtIni)
							   , new SimpleDateFormat("dd/MM/yyyy").format(dtFim)
							   , Constantes.getString("Diretorio")
							   , true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
