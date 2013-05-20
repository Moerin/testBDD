/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.urlshortener.database;

import java.io.File;
import java.util.logging.Logger;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;

/**
 *
 * @author Sebastien
 */
public class ConnectNeo4j {

    // Creation du path
    private String dbPath;
    // Creation d'une instance Neo4j
    private GraphDatabaseService graphDb = null;
    // Creation de l'index de node
    private Index<Node> nodeIndex;
    // Constante sur l'id du node
    private String key; /* la valeur doit etre changé
    dans la base de donnée */
    // Nom de l'index de la base de donnée
    private String index;

    /**
     * Constructeur qui valorise l'adresse de la base de donnée, l'identifiant
     * de la propriète du node et l'index de la base de donnée
     * 
     * @param dbPath Adresse absolue de la base de donnée
     * @param key L'identifiant de la propriété de la base de donnée
     * @param index Le nom de l'index à rechercher
     */
    public ConnectNeo4j(final String dbPath, final String key, final String index) {
        this.dbPath = dbPath;
        this.key = key;
        this.index = index;
    }
    /**
     * Constructeur qui valorise l'adresse de la base de donnée et l'identifiant
     * de la propriète du node
     * 
     * @param dbPath Adresse absolue de la base de donnée
     * @param key L'identifiant de la propriété de la base de donnée
     */
    public ConnectNeo4j(final String dbPath, final String key) {
        this.dbPath = dbPath;
        this.key = key;
    }
    
    /**
     * Demarre la connection à la base de donnée
     * 
     * @param 
     * @return
     */
    public void startConnect() {
        // Démarrage du serveur avec le path en propriété
        graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(dbPath);
        System.out.println("Base de donnée initialisé");
        // Recuperation d'un node index avec "nodes" comme nom (deja present dans la base de donnée)
        if (graphDb.index().existsForNodes(index)) {
            System.out.println("L'index : " + index + " existe");
        }
        nodeIndex = graphDb.index().forNodes(index);
        // Fermeture de l'instance
        registerShutdownHook(graphDb);
    }

    /**
     * Demarre une transaction qui se charge de recupérer la valeur de la propriété du node
     * 
     * @param unId long qui représente l'id du node
     * @return String qui représente la valeur du propriété du node
     */
    public String transactionId(long unId) {
        String requete = null;
        
        // Debut de la transaction
        Transaction tx = graphDb.beginTx();
            try {
                Node node = graphDb.getNodeById(unId);
                requete = String.valueOf(node.getProperty(key));
                // Signale que la transaction a reussi
                tx.success();
            } catch (Exception e) {
                // TODO : remplacer par des logs et slf4j
                // Signale que la transaction a ete un echec
                tx.failure(); // A VERIFIER SI CETTE LIGNE DE CODE EST UTILE
            } finally {
                // Cloture la transaction
                tx.finish();
            }
        return requete;
    }
    /**
     * Demarre une transaction qui se charge de recupérer l'id du node
     * 
     * @param unString String qui représente la valeur de la propriété du node
     * @return long qui représente l'id du node
     */
    public long transactionValue(String unString){
        long requete = -1; 
        
        // Debut de la transaction
        Transaction tx = graphDb.beginTx();

            // C'est une chaine
            try {
                IndexHits<Node> hits = nodeIndex.get(key, unString);
                Node node = hits.getSingle();
                requete = node.getId();
                // Signale que la transaction a reussi
                tx.success();
            } catch (Exception e) {
                // Signale que la transaction a été un echec
                System.err.println("Raté"); // TODO : remplacer par des logs et slf4j
                tx.failure(); // A VERIFIER SI CETTE LIGNE DE CODE EST UTILE
            } finally {
                // Cloture la transaction
                tx.finish();
            }
            return requete;
    }
    
    // Registers a shutdown hook for the Neo4j and index service instances
    // so that it shuts down nicely when the VM exits (even if you
    // "Ctrl-C" the running example before it's completed)
    public static void registerShutdownHook(final GraphDatabaseService graphDb) {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                graphDb.shutdown();
            }
        });
        System.out.println("Serveur Arreté");
    }
}
