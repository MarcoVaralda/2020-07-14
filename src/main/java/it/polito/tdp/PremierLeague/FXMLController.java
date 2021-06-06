/**
 * Sample Skeleton for 'Scene.fxml' Controller Class
 */

package it.polito.tdp.PremierLeague;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.PremierLeague.model.Model;
import it.polito.tdp.PremierLeague.model.Team;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLController {
	
	private Model model = new Model();

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="btnCreaGrafo"
    private Button btnCreaGrafo; // Value injected by FXMLLoader

    @FXML // fx:id="btnClassifica"
    private Button btnClassifica; // Value injected by FXMLLoader

    @FXML // fx:id="btnSimula"
    private Button btnSimula; // Value injected by FXMLLoader

    @FXML // fx:id="cmbSquadra"
    private ComboBox<Team> cmbSquadra; // Value injected by FXMLLoader

    @FXML // fx:id="txtN"
    private TextField txtN; // Value injected by FXMLLoader

    @FXML // fx:id="txtX"
    private TextField txtX; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader

    @FXML
    void doClassifica(ActionEvent event) {
    	Team teamScelto = this.cmbSquadra.getValue();
    	if(teamScelto == null) {
    		this.txtResult.setText("Devi Inserire un team dall'elenco!");
    		return;
    	}
    	
    	String risultato = "\nSquadre peggiori di "+teamScelto.getName()+":\n\n";
    	
    	for(Team peggiore : this.model.getPeggiori(teamScelto).keySet()) {
    		risultato += peggiore.getName()+" (" +this.model.getPeggiori(teamScelto).get(peggiore) +")\n";
    	}
    	
    	risultato += "\n"+"Squadre migliori di "+teamScelto.getName()+":\n\n";
    	
    	for(Team migliore : this.model.getMigliori(teamScelto).keySet()) {
    		risultato += migliore.getName()+" (" +this.model.getMigliori(teamScelto).get(migliore) +")\n";
    	}
    	
    	this.txtResult.appendText(risultato);
    }

    @FXML
    void doCreaGrafo(ActionEvent event) {
    	this.model.creaGrafo();
    	this.cmbSquadra.getItems().addAll(this.model.getTeams());
    	
    	this.txtResult.setText("Grafo creato!\n\n" +this.model.getNumeroVertici() +this.model.getNumeroArchi());
    }

    @FXML
    void doSimula(ActionEvent event) {
    	String Nstring = this.txtN.getText();
    	String XString = this.txtX.getText();
    	
    	int N=0;
    	int X=0;
    	
    	try {
    		N = Integer.parseInt(Nstring);
    		X = Integer.parseInt(XString);
    	}
    	catch(NumberFormatException nbe) {
    		this.txtResult.setText("Devi inserire due valori interi N ed X!");
    		return;
    	}
    	
    	this.txtResult.appendText("\n\n"+this.model.simula(N, X));
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert btnCreaGrafo != null : "fx:id=\"btnCreaGrafo\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnClassifica != null : "fx:id=\"btnClassifica\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnSimula != null : "fx:id=\"btnSimula\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbSquadra != null : "fx:id=\"cmbSquadra\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtN != null : "fx:id=\"txtN\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtX != null : "fx:id=\"txtX\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";
    }
    
    public void setModel(Model model) {
    	this.model = model;
    }
}
