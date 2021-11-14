/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package metier;

import futoshiki.InputText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.JTextField;

public class Backtracking
{
    public  boolean WITHMRV = false ;        
    public  boolean WITHDEGRES = false;       
    public  boolean WITHLCV = false;       
    public  boolean WITHFC = false;         
    public  boolean WITHAC1 = false;
    
    static JTextField [][] maGrille;
    static int [][] valGrille; // grille des valeurs
    static char [][] contraintesHorizontale; // Grille des contraintes horizontales : < et >
    static char [][] contraintesVerticale; // Grille des contraintes verticales : ^ et v
    static ST<String, SET<String>> domain;
    
    // --- Constructeur ---
    public Backtracking()
    {
    	super();
    }
    
    public Backtracking(futoshiki.Interface game)
    {
    	InputText[][] cellules = game.getCellules();
    	int n = (cellules.length+1)/2;
    	valGrille = new int[n][n];
        contraintesHorizontale = game.contraintesHoriz;
        contraintesVerticale = game.contraintesVert;
        
    	for(int i=0;i <cellules.length;i++)
    	{
    		for(int j=0;j<cellules.length;j++)
    		{
    			if(cellules[i][j].getType()==InputText.NUMBER && !cellules[i][j].getText().isEmpty())
    				valGrille[i/2][j/2]=Integer.parseInt(cellules[i][j].getText());
    		}
    	}
    }
    
    public static String getVariable(ST<String, String> config) {	
	//retrieve a variable based on a heuristic or the next 'unfilled' one if there is no heuristic
    	
        for (String s : config) 
        {
            if(config.get(s).equalsIgnoreCase(""))
                return s;
        }	
        //get variable failed (all variables have been coloured)
        return null;
    }
    
    public static SET<String> orderDomainValue2(String variable, ST<String, SET<String>> domain)
    {
        //return the SET of domain values for the variable
        return domain.get(variable);
    }
	
    public static boolean complete(ST<String, String> config)
    {
        for(String s: config) {
            //if we find a variable in the config with no value, then this means that the config is NOT complete
            if(config.get(s).equalsIgnoreCase(""))
                return false;
        }
        //ALL variables in config have a value, so the configuration is complete
        return true;
    }
		/*------ cette fonction traite les inf et sup -----*/
    public static boolean consistent(String value, String variable, ST<String, String> config, Graph g)
    {	//on prent les variable adjacent a notre variable 
        for(String adj: g.adjacentTo(variable)) 
        {	// s pour superieur et i pour inferieur 
            if(!adj.contains("s") && !adj.contains("i"))
            { 	// si on a ni s ni i et no val donc false 
                if(config.get(adj).equalsIgnoreCase(value))
                    return false;
            }else if(adj.contains("s")) // si on a s 
            {    // on remplace s par le nom originale
                String nomOriginal = adj.replace("s", "x"); 
                if(!config.get(nomOriginal).equals(""))
                {	// notre valeur
                    int valeur = Integer.parseInt(value);
                    // numero sup
                    int numS = Integer.parseInt(config.get(nomOriginal));
                    // on compare les deux 
                    if(numS <= valeur)
                        return false;
                }
            }else
            {	//si on a i
            	// on remplace i par le nom originale
                String nomOriginal = adj.replace("i", "x");
                if(!config.get(nomOriginal).equals(""))
                {	//notre valeur
                    int valeur = Integer.parseInt(value);
                    // numero inf
                    int numI = Integer.parseInt(config.get(nomOriginal));
                    //on compare les deux
                    if(numI > valeur)
                        return false;
                }
            }
        }
        return true;
    }
	
    public static boolean consistent(String value, String variable, ST<String, String> config,
                                            ST<String, ST<String, ST<String, SET<String>>>> constraintsTable) {
        //we need to get the constraint list for the variable
        for(String constraints: constraintsTable.get(variable)) {
            //if the adjacency list member's value is equal to the variable's selected value, then consistency fails
            if(!config.get(constraints).equals("") && !(constraintsTable.get(constraints).get(value).contains(config.get(constraints)))) {
                return false;
            }
        }
        //consistency check passed according to the variable's adjacancy list
        return true;
    }
    
    static void aff(ST<String, String> config){
        System.out.println("");
        System.out.print(" - ");

        if(config ==null)
            System.out.print("Pas de solution");
        else
        {
            for (String s : config)
            {
               System.out.print("("+s + ", "+ config.get(s)+")");
            }
        }
    }
    /*---------------------- MVR ------------------------------*/
    public static String MVR(ST<String, SET<String>> domain , ST<String, String> config)
    {
        // la liste des tailles de domaine correpondant a chaque variable 
        ArrayList<Integer> taille = new ArrayList<Integer>() ;
        for (String s : config) 
        {
            if(config.get(s).equalsIgnoreCase(""))
            {
                taille.add(domain.get(s).size()) ;
            }  
        }
        // le tri de la liste taille (croissant)
        Collections.sort(taille);
        for(String s : config )   
        {
            if(domain.get(s).size() == taille.get(0)  && config.get(s).equalsIgnoreCase(""))  
               return s;
        }
        return null ;
    }
    
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static String getVariableMRV(ST<String, SET<String>> domain , ST<String, String> config)
    {
        // Stocker (variable, taille du domaine)
        TreeMap<String, Integer> compteParVariable = new TreeMap<>();
        // Table associative triée par ordre croissant
        for (String var : config)
            if(config.get(var).equalsIgnoreCase(""))
                compteParVariable.put(var,domain.get(var).size()) ;
        // Mettre sous forme d'une liste puis trier
        List list = new ArrayList(compteParVariable.entrySet());
        Collections.sort(list, new cmpComptage());
        return ((Map.Entry<String, Integer>)list.get(0)).getKey();
     }    
    
    /*---------------------- Forward Checking ------------------------------*/
    public  static void forwardChecking(String u , String variable , Graph g ,ST<String, String> config ,ST<String, SET<String>> domain )
    {   
        for(String adj: g.adjacentTo(variable))                       
            if(config.get(adj)!=null && config.get(adj).equalsIgnoreCase("") && domain.get(adj).contains(u)) 
                domain.get(adj).remove(u);
    }
    
    /*---------------------- Retour en arriere ------------------------------*/
    @SuppressWarnings("unchecked")
	public static void retour(String variable, Graph g, ST<String, String> config, ST<String, SET<String>> domain, String val) 
    {
        SET<String> intervalle = new SET<String>();

        for(String adj: g.adjacentTo(variable)) {
            if(config.get(adj)!=null && config.get(adj).equalsIgnoreCase(""))
            {
                intervalle = (SET<String>) orderDomainValue(adj, domain);
                if( !intervalle.contains(val)){
                    orderDomainValue(adj, domain).add(val);
                }
            }
        }
    }
    
    
    public static void AC1(Graph g ,ST<String, String> config, ST<String, SET<String>> domain )
    {              
        boolean changement;              
        do
        {                  
            changement = false;               
            for(String variable: config)                       
                if(config.get(variable).equalsIgnoreCase("")) 
                // Pour chaque variable non affectée    
                    for(String adj: g.adjacentTo(variable))
                    {                              
                        if(config.get(adj)!=null && config.get(adj).equalsIgnoreCase(""))
                        {   // ... adjacente non affectée                                
                                // Pour éviter l'erreur : Exception in thread "main"                                  
                                // java.util.ConcurrentModificationException                                 
                            SET<String> valeurs = new SET<String>(domain.get(variable).getSet());                                  
                            for(String val: valeurs)
                            {                                     
                                SET<String> adjDomain = domain.get(adj);                                   
                                // Valeur consistante introuvable                
                                if((adjDomain!=null)&& adjDomain.contains(val) && (adjDomain.size()==1))
                                {                                        
                                    // Supprimer du domaine de la variable                                         
                                    domain.get(variable).remove(val);                                           
                                    changement = true;                                     
                                }                                 
                            }                            
                        }                         
                    }                
        } while(changement);
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static String getVariableDegresMRV(Graph g, ST<String, SET<String>> domain , ST<String,String> config)
    {
        // Stocker (variable, nombre de contraintes)
        TreeMap<String, Integer> compteParVariable1 = new TreeMap<>();
        // Stocker (variable, nombre de valeurs)
        TreeMap<String, Integer> compteParVariable2 = new TreeMap<>();
        // Table associative triée par ordre décroissant (à cause du - )
        for (String var : config)
        if(config.get(var).equalsIgnoreCase(""))
        compteParVariable1.put(var, -g.degree(var)) ;
        // Mettre sous forme d'une liste puis trier
        List list = new ArrayList(compteParVariable1.entrySet());
        Collections.sort(list, new cmpComptage());
        Integer compte0 = ((Map.Entry<String, Integer>)list.get(0)).getValue();
        Iterator it = list.iterator();
        // Garder les variables avec le nombre de degrés

        while(it.hasNext())
        {
            Map.Entry entree = (Map.Entry)it.next();
            if(((Integer)entree.getValue()).equals(compte0))
            {
                String var = (String)entree.getKey();
                compteParVariable2.put(var,domain.get(var).size());
            }       
            else
                break;
        }
        list = new ArrayList(compteParVariable2.entrySet());
        Collections.sort(list, new cmpComptage());
        
        return ((Map.Entry<String, Integer>)list.get(0)).getKey();
    }
    
    public static List<String> orderDomainValue(String variable, ST<String, SET<String>> domain)
    {
        List<String> valeurs = new ArrayList<>();
        
        for(String val:domain.get(variable))
            valeurs.add(val);
        
        return valeurs;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static List<String> orderDomainValueLCV(String variable,Graph g, ST<String, SET<String>>domain)
    {
        // Stocker (variable, nombre de contraintes)
        TreeMap< String, Integer> compteParValeur = new TreeMap<>();
        //return the SET of domain values for the variable
        SET<String> vu = domain.get(variable);
        
        for(String v:vu)
        {
            int n=1;
            for(String adj: g.adjacentTo(variable))
                if(domain.get(adj)!=null && domain.get(adj).contains(v))
                    n++;
            compteParValeur.put(v,n);
        }
        // Mettre sous forme d'une liste puis trier
        List list = new ArrayList(compteParValeur.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>()
                                {
                                    @Override
                                    public int compare(Map.Entry<String, Integer> e1, Map.Entry<String, Integer> e2) {
                                    return e1.getValue().compareTo(e2.getValue());
                                }
        });
        // Liste des valeurs
        List<String> vals = new ArrayList<>();
        Iterator it = list.iterator();
        while(it.hasNext())
        {
            Map.Entry<String, Integer> entree= (Map.Entry<String, Integer>)it.next();
            vals.add((String)entree.getKey());
        }
        return vals;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static String getVariableDegres( Graph g, ST<String, String> config)
    {
        // Stocker (variable, nombre de contraintes)
        TreeMap<String,Integer> compteParVariable = new TreeMap<>();
        // Table associative triée par ordre décroissant (à cause du - )
        for (String var : config)
            if(config.get(var).equalsIgnoreCase(""))
                compteParVariable.put(var, -g.degree(var)) ;
                // Mettre sous forme d'une liste puis trier
        List list = new ArrayList(compteParVariable.entrySet());
        Collections.sort(list, new cmpComptage());
        
        return ((Map.Entry<String, Integer>)list.get(0)).getKey();
    }
    
    @SuppressWarnings("unused")
	public ST<String, String> backtracking(ST<String, String> config, ST<String, SET<String>> domain, Graph g)
    {
        
        if(complete(config))
            return  config;
        ST<String, String> result = null;
        // Extraire une variable               
        String v =null; 
        
        if(WITHMRV && WITHDEGRES)                     
            v = getVariableDegresMRV(g, domain, config);                
        else if (WITHMRV)                     
            v = getVariableMRV(domain, config);                
        else if(WITHDEGRES)                     
            v=getVariableDegres(g, config);                
        else                    
        v =  getVariable(config);               
        // Liste des valeurs du domaine de la variable choisie               
        List <String> vu;                 
        if(WITHLCV)
            vu = orderDomainValueLCV(v, g, domain);                        
        else
            vu = orderDomainValue(v, domain);                 
        // Préparer la sauvegarde des domaines                
        ST<String, SET<String>> tmpDomain = null;               
        for(String u: vu) 
        {                     
            if(consistent(u, v, config, g)) 
            {                         
                config.put(v, u);                        
                //Sauvegarde des domaine                         
                if(WITHAC1 || WITHFC)
                { 
                    tmpDomain = new ST<>();
                    for(String vr :domain)                                
                        tmpDomain.put(vr, new SET<>(domain.get(vr).getSet()));  
                }                        
                // FC et/ou AC1                        
                if(WITHFC)                            
                    forwardChecking(u, v, g, config, tmpDomain);   
                if(WITHAC1)                         
                    AC1(g,config,tmpDomain);           
                if(WITHAC1 || WITHFC)                              
                    result = backtracking(config, tmpDomain, g);        
                else                              
                    result = backtracking(config, domain, g);      
                if(result != null)                            
                    return result;                        
                config.put(v,"");                               
            }   
        }               

        return null;         
    }
    
    public ST<String, String> backtracking(int type,ST<String, String> config, ST<String, SET<String>> domain, Graph g)
    {
    	
    	if(complete(config))
            return config;
    	ST<String, String> result = null;
    	
    	//variable a affect�
    	String v;
		if(type == 0)
    		v = getVariable(config); // Backtracking simple
    	else
    		v = MVR(domain, config); // En utilisant MVR
    	
		SET<String> vu = orderDomainValue2(v, domain);
		
		if(type != 2)
		{
			for(String u: vu)
	        {
	            if(consistent(u, v, config, g))
	            { 
	                config.put(v, u);
	                // --- Changement de la valeur dans la grille des valeurs ---
	                valGrille[Character.getNumericValue(v.charAt(1))][Character.getNumericValue(v.charAt(2))] = Integer.parseInt(config.get(v));

	                result = backtracking(type,config, domain, g);
	                if(result != null)
	                    return result;

	                config.put(v,"");
	            }
	        }
		}
		else
		{
			for(String u: vu) 
	        {
	            if(consistent(u,v, config, g)) 
	            {
	                config.put(v, u);
	                forwardChecking(u,v,g,config, domain);
	                
	                result = backtracking(type,config, domain, g);
	                if(result != null)
	                        return result;
	                retour(v, g, config, domain, u);
	                config.put(v,"");
	            }
	        }
		}
		return null;
    }
}
@SuppressWarnings("rawtypes")
class cmpComptage implements Comparator
{
    @SuppressWarnings("unchecked")
	@Override
    public int compare(Object e1, Object e2)
    {
        return ((Map.Entry<String, Integer>)e1).getValue().compareTo(((Map.Entry<String,
        Integer>)e2).getValue());
    }
}