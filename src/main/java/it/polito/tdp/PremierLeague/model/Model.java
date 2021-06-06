package it.polito.tdp.PremierLeague.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

public class Model {
	
	PremierLeagueDAO dao;
	Simulatore simulatore ;
	
	Graph<Team,DefaultWeightedEdge> grafo;
	Map<Integer,Team> idMap;
	Map<Integer,Integer> classifica;
	
	public Model() {
		this.dao = new PremierLeagueDAO();
		this.idMap = new HashMap<>();
		this.classifica = new HashMap<>();
		setClassifica(this.classifica);
	}
	
	public void creaGrafo() {
		this.grafo = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		
		// Aggiungo i vertici
		Graphs.addAllVertices(this.grafo,dao.listAllTeams(this.idMap));
		
		// Aggiungo gli archi		
		for(Integer team1 : this.classifica.keySet())
			for(Integer team2: this.classifica.keySet())
					if(!this.grafo.containsEdge(this.idMap.get(team1),this.idMap.get(team2)) && !this.grafo.containsEdge(this.idMap.get(team2),this.idMap.get(team1))) {
						int peso = this.classifica.get(team1) - this.classifica.get(team2);
						if(peso>0)
							Graphs.addEdgeWithVertices(this.grafo,this.idMap.get(team1),this.idMap.get(team2), peso);
						else if(peso<0)
							Graphs.addEdgeWithVertices(this.grafo,this.idMap.get(team2),this.idMap.get(team1), (-1)*peso);
					}
	}

	public String getNumeroVertici() {
		return "Numero vertici: " +this.grafo.vertexSet().size()+"\n";
	}
	
	public String getNumeroArchi() {
		return "Numero archi: " +this.grafo.edgeSet().size() +"\n";
	}
	
	public List<Team> getTeams() {
		List<Team> teams = new LinkedList<>();
		for(Team t : this.grafo.vertexSet()) {
			teams.add(t);
		}
		return teams;
	}
	
	private void setClassifica(Map<Integer, Integer> classifica) {
		List<Match> matches = this.dao.listAllMatches();
		
		for(Match m : matches) {
			if(!classifica.containsKey(m.teamHomeID)) {
				classifica.put(m.teamHomeID,0);
			}
			if(!classifica.containsKey(m.teamAwayID)) {
				classifica.put(m.teamAwayID,0);
			}
			
			if(m.resultOfTeamHome==1) {
				classifica.replace(m.teamHomeID,classifica.get(m.teamHomeID)+3);
			}
			else if(m.resultOfTeamHome==-1) {
				classifica.replace(m.teamAwayID,classifica.get(m.teamAwayID)+3);
			}
			else {
				classifica.replace(m.teamHomeID, classifica.get(m.teamHomeID)+1);
				classifica.replace(m.teamAwayID, classifica.get(m.teamAwayID)+1);
			}
		}
	}
	
	public LinkedHashMap<Team,Integer> getPeggiori(Team s) {
		Map<Team,Integer> peggiori = new HashMap<>();
		
		for(Integer team : this.classifica.keySet()) {
			if(classifica.get(team)<classifica.get(s.teamID)) {
				peggiori.put(this.idMap.get(team),(this.classifica.get(s.teamID)-this.classifica.get(team)));
			}
		}
		return this.sortHashMapByValues(peggiori);
	}

	public LinkedHashMap<Team,Integer> getMigliori(Team s) {
        Map<Team,Integer> migliori = new HashMap<>();
		
		for(Integer team : this.classifica.keySet()) {
			if(classifica.get(team)>classifica.get(s.teamID)) {
				migliori.put(this.idMap.get(team),(this.classifica.get(team)-this.classifica.get(s.teamID)));
			}
		}
		return this.sortHashMapByValues(migliori);
	}
	
	public LinkedHashMap<Team, Integer> sortHashMapByValues(Map<Team, Integer> migliori) {
	    List<Team> mapKeys = new ArrayList<>(migliori.keySet());
	    List<Integer> mapValues = new ArrayList<>(migliori.values());
	    Collections.sort(mapValues);
	    Collections.sort(mapKeys);

	    LinkedHashMap<Team, Integer> sortedMap = new LinkedHashMap<>();

	    Iterator<Integer> valueIt = mapValues.iterator();
	    while (valueIt.hasNext()) {
	        Integer val = valueIt.next();
	        Iterator<Team> keyIt = mapKeys.iterator();

	        while (keyIt.hasNext()) {
	            Team key = keyIt.next();
	            Integer comp1 = migliori.get(key);
	            Integer comp2 = val;

	            if (comp1.equals(comp2)) {
	                keyIt.remove();
	                sortedMap.put(key, val);
	                break;
	            }
	        }
	    }
	    return sortedMap;
	}
	
	public List<Match> getMatches() {
		return this.dao.listAllMatches();
	}
	
	public List<Team> getListaMigliori(Team team) {
		List<Team> migliori = new LinkedList<>();
		for(Team t : this.getMigliori(team).keySet()) {
			migliori.add(t);
		}
		return migliori;
	}
	
	public List<Team> getListaPeggiori(Team team) {
		List<Team> peggiori = new LinkedList<>();
		for(Team t : this.getPeggiori(team).keySet()) {
			peggiori.add(t);
		}
		return peggiori;
	}
	
	
	
	public String simula(int N, int X) {
		this.simulatore = new Simulatore();
		
		this.simulatore.init(N, X, idMap);
		this.simulatore.run();
		
		String risultato = "";
		risultato += "Numero medio di reporter per partita: " +this.simulatore.getMedia()+"\n";
		risultato += "Numero di partite critiche: "+this.simulatore.getPartiteCritiche();
		
		return risultato;
	}
}
