package br.org.baixadou.entity;

public enum TipoDou {
	SECAO_1(1),
	SECAO_2(2),
	SECAO_3(3),
	ED_EXTRA(4),
	TODOS(99);
	
	public int tipoDou;
	
	TipoDou(int tipoDou){
		this.tipoDou = tipoDou;
	}
}
