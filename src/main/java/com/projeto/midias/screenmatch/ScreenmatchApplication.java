package com.projeto.midias.screenmatch;


import com.projeto.midias.screenmatch.model.DadosSeries;
import com.projeto.midias.screenmatch.model.DadosTemporada;
import com.projeto.midias.screenmatch.model.Episodio;
import com.projeto.midias.screenmatch.principal.Principal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class ScreenmatchApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ScreenmatchApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Principal menu = new Principal();

		DadosSeries dadosSeries = menu.exibeMenu();
		List<DadosTemporada> especificacoesTemp = menu.especificacoesSerie(dadosSeries);
		List<Episodio> episodios = menu.listaDeEpisodios(especificacoesTemp);
		menu.episodiosFavoritos(especificacoesTemp);
		menu.buscaDeEps(episodios);


	}
}
