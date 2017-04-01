package it.polito.tdp.spellchecker.controller;

import java.net.URL;
import java.util.*;
import java.util.ResourceBundle;

import it.polito.tdp.spellchecker.model.*;
import it.polito.tdp.spellchecker.model.Dictionary;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class SpellCheckerController {

	private Dictionary dizionario;
	
	List<String> listaDaCorreggere = new LinkedList<String>();

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private ComboBox<String> boxLingua;

	@FXML
	private TextArea txtDaCorreggere;

	@FXML
	private TextArea txtCorretto;

	@FXML
	private Label lblErrori;
	
	@FXML
	private Label lblStato;


	@FXML
	void doClearText(ActionEvent event) {
		// Reset dell'interfaccia grafica
		txtDaCorreggere.clear();
		listaDaCorreggere.clear();
		txtCorretto.clear();
		lblErrori.setText("");
	}

	

	@FXML
	void doActivation(ActionEvent event) {
		// L'utente non può inserire del testo prima di aver selezionato la lingua
		if (boxLingua.getValue() != null) {
			txtDaCorreggere.setDisable(false);
			txtCorretto.setDisable(false);
			
			txtDaCorreggere.clear();
			txtCorretto.clear();
		}
	}
	

	@FXML
	void doSpellCheck(ActionEvent event) {

		// Inizializzazione
		txtCorretto.clear();
		listaDaCorreggere.clear();
		
		// Gestisco la selezione della lingua
		dizionario.loadDictionary(boxLingua.getValue());
		

		// Prendo il testo da correggere
		String inputText = txtDaCorreggere.getText();
		if (inputText.isEmpty())
			return;
		
		// Divido il testo usando gli spazi e elimino la punteggiatura
		StringTokenizer st = new StringTokenizer(inputText, " ");
		while (st.hasMoreTokens()) {
			listaDaCorreggere.add(st.nextToken().replaceAll("[ \\p{Punct}]", "").trim().toLowerCase());
		}
		
		// Chiamo la funzione per la correzione del testo 
		long l1 = System.nanoTime();
		List<RichWord> lista = dizionario.spellCheckText(listaDaCorreggere);
		long l2 = System.nanoTime();
		
		// Stampo il rich text
		int errori = 0;
		String richText = "";
		
		for (RichWord r : lista) {
			if(!r.isCorretta()){
				errori ++;
				richText += r.getParola();
				richText += "\n";
			}
		}
		
		txtCorretto.setText(richText);
		lblStato.setText("Spell check completed in " + (l2 - l1) / 1E9 + " seconds");
		
		// Aggiorno il contenuto della label.
		lblErrori.setText("The text contains " + errori + " errors");
	}

	@FXML
	void initialize() {
		assert boxLingua != null : "fx:id=\"boxLingua\" was not injected: check your FXML file 'SpellChecker.fxml'.";
		assert txtDaCorreggere != null : "fx:id=\"txtDaCorreggere\" was not injected: check your FXML file 'SpellChecker.fxml'.";
		assert txtCorretto != null : "fx:id=\"txtCorretto\" was not injected: check your FXML file 'SpellChecker.fxml'.";
		assert lblErrori != null : "fx:id=\"lblErrori\" was not injected: check your FXML file 'SpellChecker.fxml'.";
		assert lblStato != null : "fx:id=\"lblStato\" was not injected: check your FXML file 'SpellChecker.fxml'.";
		
		// Inizializzazione dei componenti
		txtDaCorreggere.setText("Selezionare una lingua");
		txtDaCorreggere.setDisable(true);
		txtCorretto.setDisable(true);
		boxLingua.getItems().addAll("English", "Italian");
		
		lblErrori.setText("");
		lblStato.setText("");
		
		this.dizionario = new Dictionary();
		
	}
}

