package com.indieappsbr.lojavirtual.logica;

//Api responsavel por consumir o webservice
public interface BancoDadosApi
{
	void resposta(int op, String json);
	void erro(int op, String erro);
}
