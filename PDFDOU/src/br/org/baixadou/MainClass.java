package br.org.baixadou;

import java.io.IOException;
import java.util.HashMap;
import java.util.ResourceBundle;

import br.org.baixadou.download.Dou;

public class MainClass {

	private static ResourceBundle Constantes = ResourceBundle.getBundle("config");
	
	public static void main(String[] args) {
		Dou dou;

		try {
			dou = new Dou();
			dou.pesquisaPorTermo("Dilma", "20/12/2014", "29/12/2014",
					Constantes.getString("Diretorio"), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
