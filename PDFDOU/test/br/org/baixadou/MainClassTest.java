package br.org.baixadou;

import java.util.Date;
import java.util.ResourceBundle;

import org.junit.Test;

import br.org.baixadou.entity.TipoDou;


public class MainClassTest {
//	private ResourceBundle constantes;
//
//	private String dir;
//	private Date dtDou;
//	private Date dtFim;
//	private Date dtIni;
//	private TipoDou jornal;
//	private boolean startGui;
//	private String termo;
	
	@Test
	public void mainCmdLnTest(){
		String args[] = {"-t Dilma"
					   , "-dtini 20/12/2014"
					   , "-dtfim 29/12/2014"};
		
		MainClass.main(args);
	};

}
