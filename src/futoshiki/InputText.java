package futoshiki;

import java.awt.Component;

import javax.swing.JTextField;
// cette class pour faire les différet type de notre cellule
public class InputText extends JTextField 
{
	private int type; // le type du cellule 
	
	/*--- getters and setters */
	public void setType(int type) 
	{
		this.type = type;
	}
	public int getType()
	{
		return type;
	}
	//constructeur
	public InputText(int type)
	{
		super();
		this.type = type;
	}
	// switch des types 
	public String printType() 
	{
		switch(this.type)
		{
		case NUMBER : 				return "Number";
		case HORIZONTAL_SYMBOL : 	return ">"; 
		case VERTICAL_SYMBOL : 		return "^";
		case EMPTY : 				return "NaN"; 
		default : 					return "Err";
		}
	}
	
	// pour chaque type on assossier un numero
	final static public int NUMBER = 0;
	final static public int HORIZONTAL_SYMBOL = 1;
	final static public int VERTICAL_SYMBOL = 2;
	final static public int EMPTY = -1;
}
