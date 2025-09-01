package com.projeto.midias.screenmatch.principal;

import com.projeto.midias.screenmatch.model.DadosSeries;
import com.projeto.midias.screenmatch.model.DadosSeriesEpisodios;
import com.projeto.midias.screenmatch.model.DadosTemporada;
import com.projeto.midias.screenmatch.model.Episodio;
import com.projeto.midias.screenmatch.service.ConsumoAPI;
import com.projeto.midias.screenmatch.service.ConverteDados;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Principal {
    private Scanner input = new Scanner(System.in);

    private ConsumoAPI consumo = new ConsumoAPI();
    private ConverteDados conversor = new ConverteDados();

    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String TEMPORADA = "&season=";
    private final String EPISODIO = "&episode=";
    private final String APIKEY = "&apikey=12553ec9";

    public DadosSeries exibeMenu(){
        System.out.println("Digite o nome da série para busca...");
        var nomeSerie = input.nextLine();
        var json = consumo.obterDados(ENDERECO +nomeSerie.replace(" ", "+") + APIKEY);
        DadosSeries dados = conversor.obterDados(json, DadosSeries.class);
        System.out.println(dados);
        return dados;
    }

    public List<DadosTemporada> especificacoesSerie(DadosSeries dadosSeries){
        List<DadosTemporada> listaTemporadas = new ArrayList<>();

        for (int i = 1; i <= dadosSeries.totalTemporadas(); i++) {
            var json = consumo.obterDados(ENDERECO+dadosSeries.titulo().replace(" ", "+")+TEMPORADA+i+APIKEY);
            DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
            listaTemporadas.add(dadosTemporada);
        }
        listaTemporadas.forEach(System.out::println);
        return listaTemporadas;
    }

    public List<Episodio> listaDeEpisodios(List<DadosTemporada> listaTemps) {
        List<Episodio> episodios = listaTemps.stream().flatMap(t -> t.episodios().stream().map(d -> new Episodio(t.numero(), d))).collect(Collectors.toList());
        return episodios;
    }


    public void episodiosFavoritos(List<DadosTemporada> listaTemps){
        List<DadosSeriesEpisodios> dadosEps;
        System.out.println("\nLista dos Top 5 episódios!!!");
        dadosEps = listaTemps.stream().flatMap(t -> t.episodios().stream()).collect(Collectors.toList());
        dadosEps.stream()
                .filter(e -> !e.avaliacao().equals("N/A"))
                .sorted(Comparator.comparing(DadosSeriesEpisodios::avaliacao).reversed())
                .limit(5)
                .map(e-> e.titulo().toUpperCase())
                .forEach(System.out::println);
    }

    public void buscaDeEpsAno(List<Episodio> episodios){
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        System.out.println("\nA partir de que ano você deseja ver os resultados?");
        var ano = input.nextInt();
        input.nextLine();

        LocalDate dataBusca = LocalDate.of(ano, 1, 1);
        episodios.stream()
                .filter(e -> e.getDataDeLancamento() != null && e.getDataDeLancamento().isAfter(dataBusca))
                .forEach(e -> System.out.println("Temporada: "+e.getTemporada()+"\nEpisódio: "+e.getTitulo()+"\nData de lançamento: " +e.getDataDeLancamento()
                                .format(df)));
    }

    public void encontrarEpisodio(List<Episodio> listaDeEpisodios){
        System.out.println("\nQual episodio você está buscando?");
        var trechoTitulo = input.nextLine();

        Optional<Episodio> episodioBuscado = listaDeEpisodios.stream()
                .filter(e-> e.getTitulo().toLowerCase().contains(trechoTitulo.toLowerCase()))
                .findFirst();
        if (episodioBuscado.isPresent()){
            System.out.println("Episódio encontrado!!");
            System.out.println("NOME: "+episodioBuscado.get().getTitulo()+"   TEMPORADA: "+episodioBuscado.get().getTemporada());
        } else {
            System.out.println("Episódio não localizado!!");
        }
    }

    public void AvaliacaoTemporadas(List<Episodio> listaDeEpisodios){
        Map<Integer, Double> avaliacoesPTemporada = listaDeEpisodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.groupingBy(Episodio::getTemporada, Collectors.averagingDouble(Episodio::getAvaliacao)));
        System.out.println("\nAvaliação das temporadas: "+avaliacoesPTemporada);
    }

    public void ColetaDeEstatisticas(List<Episodio> listaDeEpisodios){
        DoubleSummaryStatistics est = listaDeEpisodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.summarizingDouble(Episodio::getAvaliacao));
        System.out.println("Média: "+est.getAverage()+" | Melhor episódio: "+est.getMax()+" | Pior episódio: "+est.getMin());
    }
}
