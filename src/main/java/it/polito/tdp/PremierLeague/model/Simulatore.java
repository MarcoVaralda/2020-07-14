package it.polito.tdp.PremierLeague.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class Simulatore {
	
	Model model = new Model();
	
	// Eventi
	private PriorityQueue<Match> queue ;
	
	// Parametri di simulazione
	private int N;
	private int X;
	Map<Integer,Team> idMap;
	
	// Stato del sistema
	Map<Team,Integer> reporterPerTeam = new HashMap<>();
	
	// Misure in uscita
	double media = 0.0;
	int numPartiteCritiche = 0;
	
	
	public void init(int N, int X, Map<Integer,Team> idMap) {
		this.N=N;
		this.X=X;
		this.idMap = new HashMap<>(idMap);
		this.model.creaGrafo();
		
		for(Team t : this.idMap.values())
			this.reporterPerTeam.put(t, N);
		
	}
	
	public void run() {
		this.queue = new PriorityQueue<Match>();
		
		// Eventi iniziali
		for(Match m : this.model.getMatches())
			this.queue.add(m);
		
		// Ciclo di simulazione
		while(!this.queue.isEmpty()) {
			Match m = this.queue.poll();
			processEvent(m);
		}
		
	}

	private void processEvent(Match m) {
		
		Team vincente=null;
		Team perdente=null;
		if(m.resultOfTeamHome==1) {
			vincente = this.idMap.get(m.teamHomeID);
			perdente = this.idMap.get(m.teamAwayID);
		}
		else if(m.resultOfTeamHome==-1) {
			perdente = this.idMap.get(m.teamHomeID);
			vincente = this.idMap.get(m.teamAwayID);
		}
		else {
			vincente = this.idMap.get(m.teamHomeID);
			perdente = this.idMap.get(m.teamAwayID);
		}
		
		if(vincente == null || perdente == null)
			return;
		
		// Aggiorno le misure in uscita
		int reporterPartita = this.reporterPerTeam.get(vincente) + this.reporterPerTeam.get(perdente);
		media = media + reporterPartita;
		if(reporterPartita<X)
			numPartiteCritiche++;
		
		// Gestisco i reporter della squadra vincente
		int prob1 = (int)(Math.random()*100);
		if(prob1<=50) { // Sposto un reporter verso una squadra migliore
			List<Team> migliori = this.model.getListaMigliori(vincente);

			if(!migliori.isEmpty()) {
				int teamRandom = (int)(Math.random()*migliori.size()-1);
				Team scelto = migliori.get(teamRandom);
							
				this.reporterPerTeam.replace(scelto,this.reporterPerTeam.get(scelto)+1);
				this.reporterPerTeam.replace(vincente,this.reporterPerTeam.get(vincente)-1);
			}
		}
		
		// Gestisco i reporter della squadre perdente
		int prob2 = (int)(Math.random()*100);
		if(prob2<=20) { // Sposto uno o più reporter verso una squadra peggiore
		    List<Team> peggiori = this.model.getListaPeggiori(perdente);
			int numReporterRandom = (int)(Math.random()*this.reporterPerTeam.get(perdente));
						
			if(!peggiori.isEmpty()) {
		        int teamRandom = (int)(Math.random()*peggiori.size()-1);
				Team scelto = peggiori.get(teamRandom);
							
				this.reporterPerTeam.replace(perdente,this.reporterPerTeam.get(perdente)-numReporterRandom);
				this.reporterPerTeam.replace(scelto,this.reporterPerTeam.get(scelto)+numReporterRandom);
			}
		}
	}
	
	public double getMedia() {
		return this.media/this.model.getMatches().size();
	}
	
	public int getPartiteCritiche() {
		return this.numPartiteCritiche;
	}

}
